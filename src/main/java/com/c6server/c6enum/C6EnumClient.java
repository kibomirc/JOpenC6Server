package com.c6server.c6enum;

public enum C6EnumClient {
    CONNECT(0x00),
    LOGIN(0x01),
    CLIENT_REQ_EXIT(0x02),    // logout
    REQ_USERS(0x03),          // list connected users
    DEL_USERS(0x04),          // unlist connected users
    OL_MESSAGE(0x08),         // on line message
    CHNG_STATUS(0x0A),
    PONG(0x0B),
    NICK(0x10),
    QUIT(0x18),
    VERSION(0x19);

    private final int code;

    C6EnumClient(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static C6EnumClient fromCode(int code) {
        for (C6EnumClient c6Protocol : values()) {
            if (c6Protocol.code == code) {
                return c6Protocol;
            }
        }
        throw new IllegalArgumentException("Errore nel protocollo: " + code);
    }
}