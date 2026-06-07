package com.c6server.c6enum;

public enum C6EnumRoomPreferences {
    ARTI_MARZIALI(0x08, 0x04);

    private final byte index;
    private final byte val;

    C6EnumRoomPreferences(int index, int val) {
        this.index = (byte) index;
        this.val   = (byte) val;
    }

    public byte getIndex() { return index; }
    public byte getVal()   { return val; }

    public static C6EnumRoomPreferences fromBytes(byte index, byte val) {
        for (C6EnumRoomPreferences p : C6EnumRoomPreferences.values()) {
            if (p.index == index && p.val == val) return p;
        }
        throw new IllegalArgumentException("Preferenza sconosciuta: " + index + " " + val);
    }
}
