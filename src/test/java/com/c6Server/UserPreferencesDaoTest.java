package com.c6Server;

import com.c6server.dao.UserPreferencesDAO;
import com.c6server.model.UserProfileEntity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static com.c6server.c6enum.C6EnumUserProfilePreferences.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del DAO user_preferences su SQLite.
 */
class UserPreferencesDaoTest {

    private static final String NICKNAME = "ivan";
    private static final String DB_FILE =
            System.getProperty("c6.test.db", "c6-server-test.db");

    private static DataSource dataSource;
    private static UserPreferencesDAO dao;

    // ------------------------------------------------------------------
    // SETUP: datasource SQLite su file, schema, utente ivan
    // ------------------------------------------------------------------

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        // SQLite di default NON fa rispettare le foreign key: va attivato
        config.enforceForeignKeys(true);

        SQLiteDataSource ds = new SQLiteDataSource(config);
        ds.setUrl("jdbc:sqlite:" + DB_FILE);
        dataSource = ds;
        dao = new UserPreferencesDAO(ds);

        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        nickname TEXT PRIMARY KEY,
                        nome     TEXT NOT NULL,
                        email    TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        online   BOOLEAN NOT NULL DEFAULT FALSE,
                        status   TEXT NOT NULL DEFAULT 'available',
                        ping_ms  INTEGER NOT NULL DEFAULT 0,

                        CONSTRAINT check_status CHECK (status IN ('available', 'busy', 'away')),
                        CONSTRAINT check_ping_positivo CHECK (ping_ms >= 0)
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS user_preferences (
                        nickname    TEXT     NOT NULL REFERENCES users(nickname) ON DELETE CASCADE,
                        pref_index  SMALLINT NOT NULL,
                        slot        SMALLINT NOT NULL,
                        pref_val    TEXT     NOT NULL,

                        PRIMARY KEY (nickname, pref_index, slot),
                        CONSTRAINT check_slot CHECK (slot BETWEEN 1 AND 3),
                        CONSTRAINT check_categorie_singole
                            CHECK (pref_index NOT IN (1, 2, 3, 4, 5, 6, 11) OR slot = 1),
                        CONSTRAINT uq_no_duplicati UNIQUE (nickname, pref_index, pref_val)
                    )
                    """);

            // INSERT OR IGNORE: se ivan esiste gia' (run precedenti) non fallisce
            st.execute("""
                    INSERT OR IGNORE INTO users (nickname, nome, email, password)
                    VALUES ('ivan', 'Ivan', 'ivan@example.com', 'segretissima')
                    """);
        }
    }

    /**
     * A fine suite risalva il profilo completo, cosi' il file .db
     * rimane popolato con i dati di ivan e puoi ispezionarlo a mano.
     */
    @AfterAll
    static void lasciaIvanPopolato() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());
    }

    // ------------------------------------------------------------------
    // Helper: costruisce il profilo "tipo" di ivan
    // ------------------------------------------------------------------

    private static UserProfileEntity profiloDiIvan() {
        UserProfileEntity p = new UserProfileEntity();
        p.setEta(ETA_19_27);
        p.setGenere(MASCHILE);
        p.setOrientamento(ORIENTAMENTO_ETERO);
        p.setOccupazione(OCCUPAZIONE_ANALISTA_PROGRAMMATORE);
        p.setAreaGeografica(ITALIA);
        p.setRegioneProvincia(PROVINCIA_MILANO);
        p.addHobby(HOBBY_COMPUTER);
        p.addHobby(HOBBY_CINEMA);
        p.addHobby(HOBBY_VIAGGI);
        p.addSport(SPORT_NUOTO);
        p.addGenereMusicale(GENERE_MUSICALE_ROCK);
        p.addGenereMusicale(GENERE_MUSICALE_JAZZ);
        p.addGenereFilm(GENERE_CINEMATOGRAFICO_FILM_FANTASCIENZA);
        p.addOdioCordiale(ODI_CORDIALI_SVEGLIA);
        p.addOdioCordiale(ODI_CORDIALI_FAST_FOOD);
        return p;
    }

    // ------------------------------------------------------------------
    // TEST DAO
    // ------------------------------------------------------------------

    @Test
    @DisplayName("saveProfile + loadProfile: round-trip completo del profilo di ivan")
    void salvaECaricaProfiloCompleto() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());

        UserProfileEntity caricato = dao.loadProfile(NICKNAME);

        assertEquals(ETA_19_27, caricato.getEta());
        assertEquals(MASCHILE, caricato.getGenere());
        assertEquals(ORIENTAMENTO_ETERO, caricato.getOrientamento());
        assertEquals(OCCUPAZIONE_ANALISTA_PROGRAMMATORE, caricato.getOccupazione());
        assertEquals(ITALIA, caricato.getAreaGeografica());
        assertEquals(PROVINCIA_MILANO, caricato.getRegioneProvincia());
        assertNull(caricato.getComunitaVirtuale());

        assertEquals(List.of(HOBBY_COMPUTER, HOBBY_CINEMA, HOBBY_VIAGGI),
                caricato.getHobby());
        assertEquals(List.of(SPORT_NUOTO), caricato.getSport());
        assertEquals(List.of(GENERE_MUSICALE_ROCK, GENERE_MUSICALE_JAZZ),
                caricato.getGeneriMusicali());
        assertEquals(List.of(GENERE_CINEMATOGRAFICO_FILM_FANTASCIENZA),
                caricato.getGeneriFilm());
        assertEquals(List.of(ODI_CORDIALI_SVEGLIA, ODI_CORDIALI_FAST_FOOD),
                caricato.getOdiCordiali());
    }

    @Test
    @DisplayName("saveProfile sostituisce il profilo precedente (delete-and-insert)")
    void salvataggioSostituisceIlProfiloPrecedente() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());

        UserProfileEntity nuovo = new UserProfileEntity();
        nuovo.setOccupazione(OCCUPAZIONE_FILOSOFO_A);
        nuovo.addHobby(HOBBY_OZIO);
        dao.saveProfile(NICKNAME, nuovo);

        UserProfileEntity caricato = dao.loadProfile(NICKNAME);
        assertEquals(OCCUPAZIONE_FILOSOFO_A, caricato.getOccupazione());
        assertEquals(List.of(HOBBY_OZIO), caricato.getHobby());
        // tutto il resto deve essere sparito
        assertNull(caricato.getGenere());
        assertTrue(caricato.getGeneriMusicali().isEmpty());
        assertTrue(caricato.getOdiCordiali().isEmpty());
    }

    @Test
    @DisplayName("saveCategory sostituisce solo gli hobby, il resto rimane")
    void saveCategorySostituisceSoloGliHobby() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());

        dao.saveCategory(NICKNAME, UserProfileEntity.IDX_HOBBY,
                List.of(HOBBY_GIOCHI_DA_TAVOLO, HOBBY_SCRIVERE));

        UserProfileEntity caricato = dao.loadProfile(NICKNAME);
        assertEquals(List.of(HOBBY_GIOCHI_DA_TAVOLO, HOBBY_SCRIVERE),
                caricato.getHobby());
        // le altre categorie non sono state toccate
        assertEquals(OCCUPAZIONE_ANALISTA_PROGRAMMATORE, caricato.getOccupazione());
        assertEquals(List.of(GENERE_MUSICALE_ROCK, GENERE_MUSICALE_JAZZ),
                caricato.getGeneriMusicali());
    }

    @Test
    @DisplayName("findUsersByPreference trova ivan tramite un suo hobby")
    void findUsersByPreference() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());

        assertTrue(dao.findUsersByPreference(HOBBY_CINEMA).contains(NICKNAME));
        assertFalse(dao.findUsersByPreference(HOBBY_DANZA).contains(NICKNAME));
    }

    @Test
    @DisplayName("deleteAll cancella tutte le preferenze di ivan")
    void deleteAllSvuotaIlProfilo() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());
        dao.deleteAll(NICKNAME);
        assertTrue(dao.loadProfile(NICKNAME).toFlatList().isEmpty());
    }

    @Test
    @DisplayName("saveCategory rifiuta preferenze della categoria sbagliata")
    void saveCategoryRifiutaCategoriaSbagliata() {
        assertThrows(IllegalArgumentException.class, () ->
                dao.saveCategory(NICKNAME, UserProfileEntity.IDX_HOBBY,
                        List.of(GENERE_MUSICALE_JAZZ)));
    }

    // ------------------------------------------------------------------
    // TEST MODEL (vincoli in memoria)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("il model rifiuta il quarto hobby")
    void modelRifiutaQuartoHobby() {
        UserProfileEntity p = profiloDiIvan(); // ha gia' 3 hobby
        assertThrows(IllegalStateException.class,
                () -> p.addHobby(HOBBY_DANZA));
    }

    @Test
    @DisplayName("il model rifiuta un hobby duplicato")
    void modelRifiutaHobbyDuplicato() {
        UserProfileEntity p = new UserProfileEntity();
        p.addHobby(HOBBY_CINEMA);
        assertThrows(IllegalArgumentException.class,
                () -> p.addHobby(HOBBY_CINEMA));
    }

    @Test
    @DisplayName("il model rifiuta una preferenza nella categoria sbagliata")
    void modelRifiutaCategoriaSbagliata() {
        UserProfileEntity p = new UserProfileEntity();
        assertThrows(IllegalArgumentException.class,
                () -> p.setGenere(HOBBY_CINEMA));
        assertThrows(IllegalArgumentException.class,
                () -> p.addSport(GENERE_MUSICALE_ROCK));
    }

    // ------------------------------------------------------------------
    // TEST VINCOLI DEL DATABASE (insert "a mano" che devono fallire)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("il DB rifiuta uno slot fuori range (quarto hobby)")
    void dbRifiutaSlotQuattro() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());
        assertThrows(SQLException.class, () ->
                rawInsert(NICKNAME, UserProfileEntity.IDX_HOBBY, 4, "HOBBY_DANZA"));
    }

    @Test
    @DisplayName("il DB rifiuta una seconda occupazione (slot 2 su categoria singola)")
    void dbRifiutaSecondaOccupazione() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());
        assertThrows(SQLException.class, () ->
                rawInsert(NICKNAME, UserProfileEntity.IDX_OCCUPAZIONE, 2, "OCCUPAZIONE_MEDICO"));
    }

    @Test
    @DisplayName("il DB rifiuta lo stesso valore due volte (vincolo uq_no_duplicati)")
    void dbRifiutaValoreDuplicato() throws SQLException {
        dao.saveProfile(NICKNAME, profiloDiIvan());
        // slot 2 libero negli sport, ma SPORT_NUOTO c'e' gia' nello slot 1
        assertThrows(SQLException.class, () ->
                rawInsert(NICKNAME, UserProfileEntity.IDX_SPORT, 2, "SPORT_NUOTO"));
    }

    @Test
    @DisplayName("il DB rifiuta preferenze di un utente inesistente (foreign key)")
    void dbRifiutaUtenteInesistente() {
        assertThrows(SQLException.class, () ->
                rawInsert("utente_fantasma", UserProfileEntity.IDX_HOBBY, 1, "HOBBY_CINEMA"));
    }

    /** INSERT diretto che bypassa model e DAO, per testare i vincoli SQL. */
    private void rawInsert(String nickname, int prefIndex, int slot, String prefVal)
            throws SQLException {
        try (Connection conn = dataSource.getConnection();
             var ps = conn.prepareStatement(
                     "INSERT INTO user_preferences (nickname, pref_index, slot, pref_val) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, nickname);
            ps.setInt(2, prefIndex);
            ps.setInt(3, slot);
            ps.setString(4, prefVal);
            ps.executeUpdate();
        }
    }
}