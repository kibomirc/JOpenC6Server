package com.c6server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {

    private static final ConcurrentHashMap<String, Socket> clients = new ConcurrentHashMap<>();

    public static void register(String nickname, Socket socket) {
        if (clients.containsKey(nickname)) {
            System.out.println("ATTENZIONE: " + nickname + " già registrato — sovrascrittura!");
        }
        clients.put(nickname, socket);
        System.out.println("Registrato: " + nickname + " — totale connessi: " + clients.size());
    }

    public static void unregister(String nickname) {
        Socket socket = clients.remove(nickname);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Errore chiusura socket " + nickname + ": " + e.getMessage());
            }
        }
        System.out.println("Rimosso: " + nickname + " — totale connessi: " + clients.size());
    }

    public static void sendTo(String nickname, byte[] data) throws IOException {
        Socket socket = clients.get(nickname);
        if (socket != null) {
            OutputStream out = socket.getOutputStream();
            synchronized (out) {
                out.write(data);
                out.flush();
                System.out.println("sendTo: dati inviati a " + nickname + " — " + data.length + " byte");
            }
        } else {
            System.out.println("Utente non collegato bisogna usare OF_MESSAGE");
        }
    }

    public static boolean isOnline(String nickname) {
        return clients.containsKey(nickname);
    }
}