package com.c6server.dao;

import com.c6server.c6enum.C6EnumRoomPreferences;
import com.c6server.model.RoomProfileEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomDAO {
        private final Connection connection;

        public RoomDAO(Connection connection) {
            this.connection = connection;
        }

        // CREATE: Inserisce una nuova stanza
        public void create(String roomName, String description, String ownerNickname, String type, RoomProfileEntity profile) throws SQLException {
            StringBuilder columns = new StringBuilder("roomName, description, ownerNickname, type");
            StringBuilder placeholders = new StringBuilder("?, ?, ?, ?");
            List<Object> values = new ArrayList<>(List.of(roomName, description, ownerNickname, type));

            addColumnIfPresent(columns, placeholders, values, "eta", profile.getEta());
            addColumnIfPresent(columns, placeholders, values, "genere", profile.getGenere());
            addColumnIfPresent(columns, placeholders, values, "orientamento", profile.getOrientamento());
            addColumnIfPresent(columns, placeholders, values, "occupazione", profile.getOccupazione());
            addColumnIfPresent(columns, placeholders, values, "area_geografica", profile.getAreaGeografica());
            addColumnIfPresent(columns, placeholders, values, "regione_provincia", profile.getRegioneProvincia());
            addColumnIfPresent(columns, placeholders, values, "comunita_virtuale", profile.getComunitaVirtuale());

            addColumnIfPresentList(columns, placeholders, values, "hobby", profile.getHobby());
            addColumnIfPresentList(columns, placeholders, values, "sport", profile.getSport());
            addColumnIfPresentList(columns, placeholders, values, "genere_musicale", profile.getGeneriMusicali());
            addColumnIfPresentList(columns, placeholders, values, "genere_cinematografico", profile.getGeneriFilm());
            addColumnIfPresentList(columns, placeholders, values, "odi_cordiali", profile.getOdiCordiali());

            String sql = "INSERT INTO rooms (" + columns + ") VALUES (" + placeholders + ");";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < values.size(); i++) {
                    pstmt.setString(i + 1, values.get(i).toString());
                }
                pstmt.executeUpdate();
                System.out.println("Stanza creata: " + roomName);
            }
        }

    private void addColumnIfPresent(StringBuilder columns, StringBuilder placeholders,
                                    List<Object> values, String columnName,
                                    C6EnumRoomPreferences pref) {
        if (pref != null) {
            columns.append(", ").append(columnName);
            placeholders.append(", ?");
            values.add(pref.name());
        }
    }

    private void addColumnIfPresentList(StringBuilder columns, StringBuilder placeholders,
                                        List<Object> values, String columnName,
                                        List<C6EnumRoomPreferences> prefs) {
        if (prefs != null && !prefs.isEmpty()) {
            columns.append(", ").append(columnName);
            placeholders.append(", ?");
            String joined = prefs.stream()
                    .map(C6EnumRoomPreferences::name)
                    .collect(Collectors.joining(","));
            values.add(joined);
        }
    }

        public List<String> getRoomsAndType() throws SQLException {
            String sql = "SELECT roomName, type FROM rooms;";
            List<String> roomList = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        roomList.add(rs.getString("roomName") + "," + rs.getString("type"));
                    }
                }
            }
            return roomList;
        }

        // Controlla se una stanza esiste
        public boolean exists(String roomName) throws SQLException {
            String sql = "SELECT 1 FROM rooms WHERE roomName = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }
        }

        // DELETE: Elimina una stanza (CASCADE elimina anche i room_members)
        public void delete(String roomName) throws SQLException {
            String sql = "DELETE FROM rooms WHERE roomName = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                pstmt.executeUpdate();
                System.out.println("Stanza eliminata: " + roomName);
            }
        }

        // Aggiunge un utente alla stanza
        public void addMember(String roomName, String nickname) throws SQLException {
            String sql = "INSERT INTO room_members (roomName, nickname) VALUES (?, ?);";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                pstmt.setString(2, nickname);
                pstmt.executeUpdate();
                System.out.println("Utente '" + nickname + "' aggiunto alla stanza: " + roomName);
            }
        }

        // Rimuove un utente dalla stanza
        public void removeMember(String roomName, String nickname) throws SQLException {
            String sql = "DELETE FROM room_members WHERE roomName = ? AND nickname = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                pstmt.setString(2, nickname);
                pstmt.executeUpdate();
                System.out.println("Utente '" + nickname + "' rimosso dalla stanza: " + roomName);
            }
        }

        // Controlla se un utente è già nella stanza
        public boolean isMember(String roomName, String nickname) throws SQLException {
            String sql = "SELECT 1 FROM room_members WHERE roomName = ? AND nickname = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                pstmt.setString(2, nickname);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }
        }

        // Restituisce il numero di utenti connessi alla stanza
        public int getConnectedUsersCount(String roomName) throws SQLException {
            String sql = "SELECT COUNT(*) FROM room_members WHERE roomName = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        }

        // Restituisce la lista dei nickname degli utenti connessi alla stanza
        public List<String> getMembers(String roomName) throws SQLException {
            String sql = "SELECT nickname FROM room_members WHERE roomName = ?;";
            List<String> members = new ArrayList<>();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        members.add(rs.getString("nickname"));
                    }
                }
            }
            return members;
        }

        // Restituisce il tipo della stanza
        public String getType(String roomName) throws SQLException {
            String sql = "SELECT type FROM rooms WHERE roomName = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("type");
                    }
                    throw new SQLException("Stanza non trovata: " + roomName);
                }
            }
        }

    // Restituisce la descrizione della stanza
    public String getDescription(String roomName) throws SQLException {
        String sql = "SELECT description FROM rooms WHERE roomName = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roomName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("description");
                }
                throw new SQLException("Stanza non trovata: " + roomName);
            }
        }
    }

        // Restituisce l'owner della stanza
        public String getOwnerNickname(String roomName) throws SQLException {
            String sql = "SELECT ownerNickname FROM rooms WHERE roomName = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("ownerNickname");
                    }
                    throw new SQLException("Stanza non trovata: " + roomName);
                }
            }
        }

    private void setPreferenza(String roomName, String colonna, C6EnumRoomPreferences pref) throws SQLException {
        String sql = "UPDATE rooms SET " + colonna + " = ? WHERE roomName = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pref != null ? pref.name() : null);
            pstmt.setString(2, roomName);
            pstmt.executeUpdate();
        }
    }

    private C6EnumRoomPreferences getPreferenza(String roomName, String colonna) throws SQLException {
        String sql = "SELECT " + colonna + " FROM rooms WHERE roomName = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roomName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String val = rs.getString(colonna);
                    return val != null ? C6EnumRoomPreferences.valueOf(val) : null;
                }
                throw new SQLException("Stanza non trovata: " + roomName);
            }
        }
    }

    public void setEta(String roomName, C6EnumRoomPreferences eta) throws SQLException {
        setPreferenza(roomName, "eta", eta);
    }

    public C6EnumRoomPreferences getEta(String roomName) throws SQLException {
        return getPreferenza(roomName, "eta");
    }

    public void setGenere(String roomName, C6EnumRoomPreferences genere) throws SQLException {
        setPreferenza(roomName, "genere", genere);
    }

    public C6EnumRoomPreferences getGenere(String roomName) throws SQLException {
        return getPreferenza(roomName, "genere");
    }

    public void setOrientamento(String roomName, C6EnumRoomPreferences orientamento) throws SQLException {
        setPreferenza(roomName, "orientamento", orientamento);
    }

    public C6EnumRoomPreferences getOrientamento(String roomName) throws SQLException {
        return getPreferenza(roomName, "orientamento");
    }

    public void setOccupazione(String roomName, C6EnumRoomPreferences occupazione) throws SQLException {
        setPreferenza(roomName, "occupazione", occupazione);
    }

    public C6EnumRoomPreferences getOccupazione(String roomName) throws SQLException {
        return getPreferenza(roomName, "occupazione");
    }

    public void setAreaGeografica(String roomName, C6EnumRoomPreferences areaGeografica) throws SQLException {
        setPreferenza(roomName, "area_geografica", areaGeografica);
    }

    public C6EnumRoomPreferences getAreaGeografica(String roomName) throws SQLException {
        return getPreferenza(roomName, "area_geografica");
    }

    public void setRegioneProvinciale(String roomName, C6EnumRoomPreferences regioneProvincia) throws SQLException {
        setPreferenza(roomName, "regione_provincia", regioneProvincia);
    }

    public C6EnumRoomPreferences getRegioneProvinciale(String roomName) throws SQLException {
        return getPreferenza(roomName, "regione_provincia");
    }

    public void setHobby(String roomName, C6EnumRoomPreferences hobby) throws SQLException {
        setPreferenza(roomName, "hobby", hobby);
    }

    public C6EnumRoomPreferences getHobby(String roomName) throws SQLException {
        return getPreferenza(roomName, "hobby");
    }

    public void setSport(String roomName, C6EnumRoomPreferences sport) throws SQLException {
        setPreferenza(roomName, "sport", sport);
    }

    public C6EnumRoomPreferences getSport(String roomName) throws SQLException {
        return getPreferenza(roomName, "sport");
    }

    public void setGenereMusicale(String roomName, C6EnumRoomPreferences genereMusicale) throws SQLException {
        setPreferenza(roomName, "genere_musicale", genereMusicale);
    }

    public C6EnumRoomPreferences getGenereMusicale(String roomName) throws SQLException {
        return getPreferenza(roomName, "genere_musicale");
    }

    public void setGenereCinematografico(String roomName, C6EnumRoomPreferences genereCinematografico) throws SQLException {
        setPreferenza(roomName, "genere_cinematografico", genereCinematografico);
    }

    public C6EnumRoomPreferences getGenereCinematografico(String roomName) throws SQLException {
        return getPreferenza(roomName, "genere_cinematografico");
    }

    public void setComunitaVirtuale(String roomName, C6EnumRoomPreferences comunitaVirtuale) throws SQLException {
        setPreferenza(roomName, "comunita_virtuale", comunitaVirtuale);
    }

    public C6EnumRoomPreferences getComunitaVirtuale(String roomName) throws SQLException {
        return getPreferenza(roomName, "comunita_virtuale");
    }

    public void setOdiCordiali(String roomName, C6EnumRoomPreferences odiCordiali) throws SQLException {
        setPreferenza(roomName, "odi_cordiali", odiCordiali);
    }

    public C6EnumRoomPreferences getOdiCordiali(String roomName) throws SQLException {
        return getPreferenza(roomName, "odi_cordiali");
    }
    }
