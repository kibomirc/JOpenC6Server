package com.c6server.dao;

import java.sql.*;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // CREATE: Inserisce un nuovo utente
    public void create(String nickname) throws SQLException {
        String sql = "INSERT INTO users (nickname) VALUES (?);";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.executeUpdate();
            System.out.println("Utente creato: " + nickname);
        }
    }

    public boolean exists(String nickname) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE nickname = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updatePing(String nickname, Integer pingMs) throws SQLException {
        String sql = "UPDATE users SET ping_ms = ? WHERE nickname = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pingMs != null) {
                pstmt.setInt(1, pingMs);
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, nickname);
            pstmt.executeUpdate();
            System.out.println("Ping aggiornato a " + (pingMs != null ? pingMs + "ms" : "NULL") + " per l'utente: " + nickname);
        }
    }

    public void updateStatus(String nickname, String status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE nickname = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, nickname);
            pstmt.executeUpdate();
            System.out.println("Stato aggiornato a '" + status + "' per l'utente: " + nickname);
        }
    }

    public void delete(String nickname) throws SQLException {
        String sql = "DELETE FROM users WHERE nickname = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.executeUpdate();
            System.out.println("Utente rimosso (e lista associata eliminata): " + nickname);
        }
    }

    public boolean getStatusOnline(String nickname) throws SQLException {
        String sql = "SELECT online FROM users WHERE nickname = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("online");
                }
                throw new SQLException("Utente non trovato: " + nickname);
            }
        }
    }

}

