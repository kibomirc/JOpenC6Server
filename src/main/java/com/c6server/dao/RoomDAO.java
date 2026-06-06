package com.c6server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
        private final Connection connection;

        public RoomDAO(Connection connection) {
            this.connection = connection;
        }

        // CREATE: Inserisce una nuova stanza
        public void create(String roomName, String description, String ownerNickname, String type) throws SQLException {
            String sql = "INSERT INTO rooms (roomName, description, ownerNickname, type) VALUES (?, ?, ?, ?);";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, roomName);
                pstmt.setString(2, description);
                pstmt.setString(3, ownerNickname);
                pstmt.setString(4, type);
                pstmt.executeUpdate();
                System.out.println("Stanza creata: " + roomName);
            }
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
    }
