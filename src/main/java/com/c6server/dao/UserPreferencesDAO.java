package com.c6server.dao;



import com.c6server.c6enum.C6EnumUserProfilePreferences;
import com.c6server.model.UserProfileEntity;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * DAO per la tabella user_preferences.
 *
 * Schema atteso:
 *   user_preferences(nickname TEXT, pref_index SMALLINT, slot SMALLINT, pref_val TEXT)
 *   PK (nickname, pref_index, slot)
 *
 * Strategia di salvataggio: delete-and-insert in transazione.
 */
public class UserPreferencesDAO {

    private static final String SQL_SELECT_BY_USER = """
            SELECT pref_val
            FROM user_preferences
            WHERE nickname = ?
            ORDER BY pref_index, slot
            """;

    private static final String SQL_DELETE_BY_USER = """
            DELETE FROM user_preferences
            WHERE nickname = ?
            """;

    private static final String SQL_DELETE_BY_USER_AND_CATEGORY = """
            DELETE FROM user_preferences
            WHERE nickname = ? AND pref_index = ?
            """;

    private static final String SQL_INSERT = """
            INSERT INTO user_preferences (nickname, pref_index, slot, pref_val)
            VALUES (?, ?, ?, ?)
            """;

    private static final String SQL_SELECT_USERS_BY_PREF = """
            SELECT nickname
            FROM user_preferences
            WHERE pref_val = ?
            ORDER BY nickname
            """;

    private final DataSource dataSource;

    public UserPreferencesDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ------------------------------------------------------------------
    // LETTURA
    // ------------------------------------------------------------------

    /**
     * Carica il profilo completo di un utente.
     * Se l'utente non ha preferenze salvate, restituisce un profilo vuoto.
     */
    public UserProfileEntity loadProfile(String nickname) throws SQLException {
        UserProfileEntity profile = new UserProfileEntity();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_USER)) {

            ps.setString(1, nickname);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("pref_val");
                    try {
                        profile.addGeneric(C6EnumUserProfilePreferences.valueOf(nome));
                    } catch (IllegalArgumentException e) {
                        // Valore in tabella che non corrisponde (piu') a nessuna
                        // costante dell'enum: dato sporco o rinomina non migrata.
                        throw new SQLException(
                                "Preferenza sconosciuta in tabella per l'utente "
                                        + nickname + ": " + nome, e);
                    }
                }
            }
        }
        return profile;
    }

    // ------------------------------------------------------------------
    // SCRITTURA
    // ------------------------------------------------------------------

    /**
     * Salva l'intero profilo di un utente sostituendo quello esistente.
     * Delete-and-insert in un'unica transazione: o va tutto a buon fine
     * o non viene toccato nulla.
     */
    public void saveProfile(String nickname, UserProfileEntity profile) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                // 1) cancella tutte le preferenze esistenti
                try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_USER)) {
                    ps.setString(1, nickname);
                    ps.executeUpdate();
                }

                // 2) reinserisce quelle del profilo, slot progressivi per categoria
                insertPreferences(conn, nickname, profile.toFlatList());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        }
    }

    /**
     * Sostituisce le preferenze di una singola categoria (es. solo gli hobby)
     * lasciando intatte le altre. prefIndex e' la categoria (es. 0x07),
     * nuovePreferenze devono appartenere tutte a quella categoria.
     */
    public void saveCategory(String nickname,
                             byte prefIndex,
                             List<C6EnumUserProfilePreferences> nuovePreferenze)
            throws SQLException {

        // validazione preventiva: tutte della categoria giusta, max 3
        if (nuovePreferenze.size() > UserProfileEntity.MAX_PER_CATEGORIA) {
            throw new IllegalArgumentException(
                    "Massimo " + UserProfileEntity.MAX_PER_CATEGORIA + " preferenze per categoria");
        }
        for (C6EnumUserProfilePreferences p : nuovePreferenze) {
            if (p.getIndex() != prefIndex) {
                throw new IllegalArgumentException(
                        p.name() + " non appartiene alla categoria " + prefIndex);
            }
        }

        try (Connection conn = dataSource.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps =
                             conn.prepareStatement(SQL_DELETE_BY_USER_AND_CATEGORY)) {
                    ps.setString(1, nickname);
                    ps.setShort(2, prefIndex);
                    ps.executeUpdate();
                }

                insertPreferences(conn, nickname, nuovePreferenze);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        }
    }

    /**
     * Cancella tutte le preferenze di un utente.
     * (Se hai ON DELETE CASCADE sulla FK, alla cancellazione dell'utente
     * non serve chiamarlo a mano.)
     */
    public void deleteAll(String nickname) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_USER)) {
            ps.setString(1, nickname);
            ps.executeUpdate();
        }
    }

    // ------------------------------------------------------------------
    // RICERCA
    // ------------------------------------------------------------------

    /**
     * Restituisce i nickname di tutti gli utenti che hanno
     * una certa preferenza (es. tutti quelli con HOBBY_CINEMA).
     */
    public List<String> findUsersByPreference(C6EnumUserProfilePreferences pref)
            throws SQLException {

        List<String> nicknames = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_USERS_BY_PREF)) {

            ps.setString(1, pref.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nicknames.add(rs.getString("nickname"));
                }
            }
        }
        return nicknames;
    }

    // ------------------------------------------------------------------
    // HELPER PRIVATI
    // ------------------------------------------------------------------

    /**
     * Inserisce una lista di preferenze calcolando lo slot progressivo
     * (1, 2, 3) per ciascuna categoria. Usa batch insert.
     * Va chiamato dentro una transazione gia' aperta.
     */
    private void insertPreferences(Connection conn,
                                   String nickname,
                                   List<C6EnumUserProfilePreferences> preferenze)
            throws SQLException {

        if (preferenze.isEmpty()) return;

        // contatore slot per categoria: index -> prossimo slot libero
        Map<Byte, Integer> slotPerCategoria = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            for (C6EnumUserProfilePreferences p : preferenze) {
                int slot = slotPerCategoria.merge(p.getIndex(), 1, Integer::sum);

                ps.setString(1, nickname);
                ps.setShort(2, p.getIndex());
                ps.setShort(3, (short) slot);
                ps.setString(4, p.name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
