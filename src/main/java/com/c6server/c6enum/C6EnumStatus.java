package com.c6server.c6enum;

public enum C6EnumStatus {
    DISPONIBILE((byte) 0x64),
    SOLO_NETFRIEND((byte) 0x68),
    IMPEGNATO((byte) 0x70);

    private final byte code;

    C6EnumStatus(byte code) { this.code = code; }

    public byte getCode() { return code; }

    public static C6EnumStatus fromCode(byte b) {
        for (C6EnumStatus s : values()) {
            if (s.code == b) return s;
        }
        return null;
    }
}
