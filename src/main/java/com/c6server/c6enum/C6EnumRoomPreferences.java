package com.c6server.c6enum;

public enum C6EnumRoomPreferences {

    // SPORT
    SPORT_NON_DEFINITO          (0x08, 0x01),
    SPORT_NESSUNO               (0x08, 0x02),
    SPORT_AEROBICA              (0x08, 0x03),
    SPORT_ARTI_MARZIALI         (0x08, 0x04),
    SPORT_ATLETICA              (0x08, 0x05),
    SPORT_AUTOMOBILISMO         (0x08, 0x06),
    SPORT_BASKET                (0x08, 0x07),
    SPORT_BODY_BUILDING         (0x08, 0x08),
    SPORT_CALCIO_CALCETTO       (0x08, 0x09),
    SPORT_CANOTTAGGIO_CANOA     (0x08, 0x0A),
    SPORT_CICLISMO              (0x08, 0x0B),
    SPORT_EQUITAZIONE           (0x08, 0x0C),
    SPORT_GOLF                  (0x08, 0x0D),
    SPORT_MOTOCICLISMO          (0x08, 0x0E),
    SPORT_NUOTO                 (0x08, 0x0F),
    SPORT_PALLA_A_VOLO_BEACH_VOLLEY (0x08, 0x10),
    SPORT_PATTINAGGIO           (0x08, 0x11),
    SPORT_PESCA                 (0x08, 0x12),
    SPORT_RUGBY                 (0x08, 0x13),
    SPORT_SCI_SNOWBOARD         (0x08, 0x14),
    SPORT_ESTREMI               (0x08, 0x15),
    SPORT_SURF                  (0x08, 0x16),
    SPORT_TENNIS_SQUASH         (0x08, 0x17),
    SPORT_VELA                  (0x08, 0x18),
    SPORT_ALTRO                 (0x08, 0x19),
    SPORT_CACCIA                (0x08, 0x1A),
    SPORT_TREKKING              (0x08, 0x1B);


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
