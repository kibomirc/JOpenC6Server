package com.c6server.utils;

import com.c6server.ClientRegistry;
import com.c6server.model.PingEntryModel;
import com.c6server.packet.PingPacket;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PingManagerUtils {

    private final byte[] PING_PACKET;
    private static final long PING_INTERVAL_SEC    = 5 * 60;
    private static final long PONG_TIMEOUT_SEC     = 60;
    private static final long CHECKER_INTERVAL_SEC = 10;

    private static PingManagerUtils instance;

    private final ConcurrentHashMap<String, PingEntryModel> pingStates = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    public static PingManagerUtils getInstance() throws IOException {
        if (instance == null) {
            instance = new PingManagerUtils();
        }
        return instance;
    }

    private PingManagerUtils() throws IOException {
        this.PING_PACKET = buildPingPacket();
    }

    // -----------------------------------------------------------------------
    // Avvio / Stop
    // -----------------------------------------------------------------------

    public void start() {
        scheduler.scheduleAtFixedRate(
                this::sendPingToAll,
                PING_INTERVAL_SEC,
                PING_INTERVAL_SEC,
                TimeUnit.SECONDS
        );

        scheduler.scheduleAtFixedRate(
                this::checkTimeouts,
                CHECKER_INTERVAL_SEC,
                CHECKER_INTERVAL_SEC,
                TimeUnit.SECONDS
        );

        System.out.println("[PingManagerUtils] Avviato — ping ogni " + PING_INTERVAL_SEC + "s, timeout " + PONG_TIMEOUT_SEC + "s");
    }

    public void stop() {
        scheduler.shutdownNow();
        System.out.println("[PingManagerUtils] Fermato.");
    }

    // -----------------------------------------------------------------------
    // Ciclo di vita dei client
    // -----------------------------------------------------------------------

    public void onClientConnected(String nickname) {
        pingStates.put(nickname, new PingEntryModel(nickname));
        System.out.println("[PingManagerUtils] Tracking avviato per: " + nickname);
    }

    public void onClientDisconnected(String nickname) {
        pingStates.remove(nickname);
        System.out.println("[PingManagerUtils] Tracking rimosso per: " + nickname);
    }

    public void onPongReceived(String nickname) {
        PingEntryModel entry = pingStates.get(nickname);
        if (entry != null) {
            entry.markPongReceived();
            System.out.println("[PingManagerUtils] PONG ricevuto da: " + nickname);
        }
    }

    // -----------------------------------------------------------------------
    // Logica interna
    // -----------------------------------------------------------------------

    private void sendPingToAll() {
        System.out.println("[PingManagerUtils] Invio PING a " + pingStates.size() + " client...");

        for (Map.Entry<String, PingEntryModel> e : pingStates.entrySet()) {
            String nickname = e.getKey();
            PingEntryModel entry = e.getValue();

            try {
                ClientRegistry.sendTo(nickname, PING_PACKET);
                entry.markPingSent();
                System.out.println("[PingManagerUtils] PING inviato a: " + nickname);
            } catch (IOException ex) {
                System.out.println("[PingManagerUtils] Errore PING su " + nickname + ": " + ex.getMessage());
                disconnect(nickname, "errore I/O durante PING");
            }
        }
    }

    private void checkTimeouts() {
        Instant now = Instant.now();

        for (Map.Entry<String, PingEntryModel> e : pingStates.entrySet()) {
            PingEntryModel entry = e.getValue();

            if (entry.isPendingPong()) {
                long elapsed = Duration.between(entry.getPingSentAt(), now).getSeconds();

                if (elapsed >= PONG_TIMEOUT_SEC) {
                    disconnect(entry.getNickname(), "timeout PONG (" + elapsed + "s)");
                }
            }
        }
    }

    private void disconnect(String nickname, String reason) {
        System.out.println("[PingManagerUtils] Disconnessione forzata: " + nickname + " — motivo: " + reason);
        pingStates.remove(nickname);
        ClientRegistry.unregister(nickname);
    }

    // -----------------------------------------------------------------------
    // Costruzione pacchetto PING
    // -----------------------------------------------------------------------

    private static byte[] buildPingPacket() throws IOException {
        PingPacket pingPacket = new PingPacket();
        pingPacket.setCount(0);
        return pingPacket.getPingPacket();
    }
}