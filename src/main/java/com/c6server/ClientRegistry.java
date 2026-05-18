package com.c6server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {

    private static final ConcurrentHashMap<String, OutputStream> clients = new ConcurrentHashMap<>();

    public static void register(String nickname, OutputStream out) {
        if (clients.containsKey(nickname)) {
            System.out.println("ATTENZIONE: " + nickname + " già registrato — sovrascrittura!");
        }
        clients.put(nickname, out);
        System.out.println("Registrato: " + nickname + " — totale connessi: " + clients.size());
    }

    public static void unregister(String nickname) {
        clients.remove(nickname);
        System.out.println("Rimosso: " + nickname + " — totale connessi: " + clients.size());
    }

    public static OutputStream getOutputStream(String nickname) {
        return clients.get(nickname);
    }

    public static void sendTo(String nickname, byte[] data) throws IOException {
        OutputStream out = clients.get(nickname);
        if (out != null) {
            synchronized (out) { // un solo thread alla volta scrive su questa out
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
