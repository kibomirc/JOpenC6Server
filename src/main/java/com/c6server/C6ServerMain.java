package com.c6server;

import com.c6server.dao.DatabaseConnection;
import com.c6server.handler.ClientHandler;
import com.c6server.utils.HttpServerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class C6ServerMain {

    private static final Logger logger = LogManager.getLogger(C6ServerMain.class);
    private static final int PORT = 4800;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException, SQLException {

        Connection conn = DatabaseConnection.getConnection();

        threadPool.submit(() -> {
            try {
                HttpServerUtils.startServer();
            } catch (Exception e) {
                logger.error("Errore HTTP server", e);
            }
        });

        threadPool.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                logger.info("In ascolto sulla porta " + PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(() -> ClientHandler.handle(clientSocket, conn));
                }
            } catch (IOException e) {
                logger.error("Errore ServerSocket", e);
            }
        });
    }
}