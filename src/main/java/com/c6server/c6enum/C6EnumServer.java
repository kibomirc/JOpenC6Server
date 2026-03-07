package com.c6server.c6enum;

public enum C6EnumServer {
    INFOLOGIN(0x01),
    REDIRECT(0x02),
    LOGIN_ERRPASS(0x03),
    LOGIN_USERCONN(0x04),
    SND_USERS(0x06),
    STATUS_USER(0x0A),
    LOGIN_NOUSER(0x0E),
    SRV_MESSAGE(0x0F),
    WELCOME(0x10),
    PING(0x11),
    HELO(0x12),
    ENCODED_MSG(0x18);

    private final int code;

    C6EnumServer(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static C6EnumServer fromCode(int code) {
        for (C6EnumServer c6Server : values()) {
            if (c6Server.code == code) {
                return c6Server;
            }
        }
        throw new IllegalArgumentException("Errore nel protocollo: " + code);
    }
}

