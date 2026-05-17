package com.c6server;

import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {

    private static final ConcurrentHashMap<String, OutputStream> clients = new ConcurrentHashMap<>();

    public static void register(String nickname, OutputStream out) {
        clients.put(nickname, out);
    }

    public static void unregister(String nickname) {
        clients.remove(nickname);
    }

    public static OutputStream getOutputStream(String nickname) {
        return clients.get(nickname);
    }

    public static boolean isOnline(String nickname) {
        return clients.containsKey(nickname);
    }
}
