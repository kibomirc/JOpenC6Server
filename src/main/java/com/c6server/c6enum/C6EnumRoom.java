package com.c6server.c6enum;

public enum C6EnumRoom {
        PRIVATE_USER_ROOM(0x00),
        PUBLIC_USER_ROOM(0x01),
        PRIVATE_SERVER_ROOM(0x02),
        PUBLIC_SERVER_ROOM(0x03),
        PRIVATE_USER_ROOM_ONLY_READER(0x10),
        PUBLIC_USER_ROOM_ONLY_READER(0x11),
        PRIVATE_SERVER_ROOM_ONLY_READER(0x12),
        PUBLIC_SERVER_ROOM_ONLY_READER(0x13);

        private final int code;

        C6EnumRoom(int code) {
            this.code = code;
        }

        public byte getCode() {
            return (byte) code;
        }

    public static C6EnumRoom fromCode(int code) {
        for (C6EnumRoom c6Protocol : values()) {
            if (c6Protocol.code == code) {
                return c6Protocol;
            }
        }
        throw new IllegalArgumentException("Errore nel protocollo: " + code);
    }

}
