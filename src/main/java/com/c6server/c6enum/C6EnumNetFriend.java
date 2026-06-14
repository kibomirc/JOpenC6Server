package com.c6server.c6enum;

public enum C6EnumNetFriend {
    AVAILABLE(0x00),
    ONLY_NETFRIEND(0x08),
    OCCUPIED(0x10),
    AVAILABLE_CAM(0x60),
    ONLY_NETFRIEND_CAM(0x68),
    OCCUPIED_CAM(0x70);

    private final int code;

    C6EnumNetFriend(int code) {
        this.code = code;
    }

    public byte getCode() {
        return (byte) code;
    }
}
