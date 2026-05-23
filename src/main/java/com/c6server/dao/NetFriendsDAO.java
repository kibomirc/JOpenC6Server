package com.c6server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NetFriendsDAO {
    private final Connection connection;

    public NetFriendsDAO(Connection connection) {
        this.connection = connection;
    }

    public void addNetFriends(String userNickname, List<String> netFriends) throws SQLException {
        if (netFriends == null || netFriends.isEmpty()) return;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF;");
        }

        String sql = "INSERT OR IGNORE INTO netfriends (user_nickname, netfriend) VALUES (?, ?);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String friend : netFriends) {
                pstmt.setString(1, userNickname);
                pstmt.setString(2, friend);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Lista amici salvata per l'utente: " + userNickname);
        }
    }

    public List<String> getListContentByUser(String userNickname) throws SQLException {
        String sql = "SELECT netfriend FROM netfriends WHERE user_nickname = ?;";
        List<String> netFriends = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userNickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    netFriends.add(rs.getString("netfriend"));
                }
            }
        }
        return netFriends;
    }

    public List<String> getNetFriendsOnline(String userNickname) throws SQLException {
        String sql = "SELECT n.netfriend FROM netfriends n " +
                "JOIN users u ON u.nickname = n.netfriend " +
                "WHERE n.user_nickname = ? AND u.online = 1;";
        List<String> netFriends = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userNickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    netFriends.add(rs.getString("netfriend"));
                }
            }
        }
        return netFriends;
    }

    public void deleteNetFriends(String userNickname, List<String> netFriends) throws SQLException {
        String sql = "DELETE FROM netfriends WHERE user_nickname = ? AND netfriend = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String netFriend : netFriends) {
                pstmt.setString(1, userNickname);
                pstmt.setString(2, netFriend);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("NetFriend rimosso per l'utente: " + userNickname);
        }
    }

    public void deleteAllNetFriends(String userNickname) throws SQLException {
        String sql = "DELETE FROM netfriends WHERE user_nickname = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userNickname);
            pstmt.executeUpdate();
            System.out.println("Lista amici rimossa per l'utente: " + userNickname);
        }
    }
}