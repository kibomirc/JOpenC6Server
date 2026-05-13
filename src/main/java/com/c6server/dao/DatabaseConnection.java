package com.c6server.dao;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:c6.db";
    private static Connection connection = null;


    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL);

                // 2. FONDAMENTALE PER LA CONSISTENZA: Forza SQLite ad attivare le Foreign Keys
                // Se ometti questo passaggio, il vincolo "ON DELETE CASCADE" non funzionerà.
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }

                initTable(connection);

                System.out.println("Connessione a SQLite stabilita e Foreign Keys attivate.");

            } catch (SQLException e) {
                System.err.println("Impossibile connettersi al database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    private static void initTable(Connection conn) {

        String[] fileSql = {
                "users.sql",
                "netfriends.sql"
        };

        for (String nomeFile : fileSql) {
            try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream(nomeFile)) {
                if (is == null) {
                    System.err.println("ATTENZIONE: File " + nomeFile + " non trovato in src/main/resources!");
                    continue;
                }

                String sqlScript = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sqlScript);
                    System.out.println("Eseguito con successo lo script: " + nomeFile);
                }
            } catch (Exception e) {
                System.err.println("Errore durante l'esecuzione dello script " + nomeFile + ": " + e.getMessage());
            }
        }
    }


    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connessione a SQLite chiusa correttamente.");
                }
            } catch (SQLException e) {
                System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }
}
