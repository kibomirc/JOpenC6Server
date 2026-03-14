package com.c6server.utils;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpServerUtils {
        public static void startServer() {
            try {
                // Crea il server sulla porta 80
                HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);

                // Definisce l'endpoint "/images"
                server.createContext("/images", exchange -> {
                    System.out.println("Richiesta ricevuta su porta 80: " + exchange.getRequestURI());
                    String imagePath = "/banner1.gif";
                    try (InputStream is = HttpServerUtils.class.getResourceAsStream(imagePath)) {
                        if (is == null) {
                            String response = "Immagine non trovata nelle risorse";
                            exchange.sendResponseHeaders(404, response.length());
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            exchange.getResponseHeaders().set("Content-Type", "image/gif");
                            byte[] imageBytes = is.readAllBytes();
                            exchange.sendResponseHeaders(200, imageBytes.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(imageBytes);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                server.setExecutor(null);
                server.start();
                System.out.println("HTTP Server avviato su http://localhost:80/images");

            } catch (IOException e) {
                System.err.println("Errore nell'avvio del server: " + e.getMessage());
            }
        }
}
