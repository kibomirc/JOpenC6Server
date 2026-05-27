package com.c6server.model;

import java.time.Instant;

public class PingEntryModel {
    private final String nickname;
    private volatile Instant pingSentAt;
    private volatile boolean pendingPong;

    public PingEntryModel(String nickname) {
        this.nickname = nickname;
        this.pendingPong = false;
    }

    public void markPingSent() {
        this.pingSentAt = Instant.now();
        this.pendingPong = true;
    }

    public void markPongReceived() {
        this.pendingPong = false;
        this.pingSentAt = null;
    }

    public boolean isPendingPong() { return pendingPong; }
    public Instant getPingSentAt() { return pingSentAt; }
    public String getNickname() { return nickname; }
}
