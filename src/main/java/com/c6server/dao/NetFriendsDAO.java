package com.c6server.dao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NetFriendsDAO {
    private final Connection connection;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public NetFriendsDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveOrUpdateList(List<String> netFriends, String nickname) throws SQLException {
        String sqlSelect = "SELECT netfriends FROM netfriends WHERE user_nickname = ?;";
        String netFriendAcutal = null;
        boolean exist = false;

        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            pstmtSelect.setString(1, nickname);
            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                    exist = true;
                    netFriendAcutal = rs.getString("netfriends");
                }
            }
        }

        Set<String> listaUnita = new LinkedHashSet<>();

        try {
            if (exist) {
                if (netFriendAcutal != null && !netFriendAcutal.isEmpty()) {
                    List<String> netFriendAcutalList = objectMapper.readValue(netFriendAcutal, new TypeReference<List<String>>() {});
                    listaUnita.addAll(netFriendAcutalList);
                }

                if (netFriends != null) {
                    listaUnita.addAll(netFriends);
                }

                String jsonFinale = objectMapper.writeValueAsString(new ArrayList<>(listaUnita));

                String sqlUpdate = "UPDATE netfriends SET netfriends = ? WHERE user_nickname = ?;";
                try (PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setString(1, jsonFinale);
                    pstmtUpdate.setString(2, nickname);
                    pstmtUpdate.executeUpdate();
                    System.out.println("Lista aggiornata (unita) con Jackson per l'utente: " + nickname);
                }

            } else {
                if (netFriends != null) {
                    listaUnita.addAll(netFriends);
                }
                String jsonFinale = objectMapper.writeValueAsString(new ArrayList<>(listaUnita));

                String sqlInsert = "INSERT INTO netfriends (netfriends, user_nickname) VALUES (?, ?);";
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert)) {
                    pstmtInsert.setString(1, jsonFinale);
                    pstmtInsert.setString(2, nickname);
                    pstmtInsert.executeUpdate();
                    System.out.println("Nuova lista creata con Jackson per l'utente: " + nickname);
                }
            }
        } catch (JsonProcessingException e) {
            throw new SQLException("Errore Jackson durante la serializzazione/deserializzazione della lista", e);
        }
    }

    public List<String> getListContentByUser(String userNickname) throws SQLException {
        String sql = "SELECT netfriends FROM netfriends WHERE user_nickname = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userNickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String jsonDalDb = rs.getString("netfriends");

                    if (jsonDalDb != null && !jsonDalDb.isEmpty()) {
                        return objectMapper.readValue(jsonDalDb, new TypeReference<List<String>>() {});
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new SQLException("Errore Jackson durante la deserializzazione della lista", e);
        }
        return new ArrayList<>();
    }


    public List<String> getNetFriendsOnline(String userNickname) throws SQLException {
        String sql = "SELECT json_group_array(j.value) AS netfriends " +
                "FROM netfriends n, json_each(n.netfriends) j " +
                "JOIN users u ON u.nickname = j.value " +
                "WHERE n.user_nickname = ? AND u.online = 1;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userNickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String jsonDalDb = rs.getString("netfriends");

                    if (jsonDalDb != null && !jsonDalDb.isEmpty()) {
                        return objectMapper.readValue(jsonDalDb, new TypeReference<List<String>>() {});
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new SQLException("Errore Jackson durante la deserializzazione della lista", e);
        }
        return new ArrayList<>();
    }



    public void deleteList(String userNickname) throws SQLException {
        String sql = "DELETE FROM netfriends WHERE user_nickname = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userNickname);
            pstmt.executeUpdate();
            System.out.println("Lista rimossa dal database.");
        }
    }
}
