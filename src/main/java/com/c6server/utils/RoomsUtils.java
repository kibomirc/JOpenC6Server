package com.c6server.utils;

import java.nio.charset.StandardCharsets;

public class RoomsUtils {

    public static String getRoomName(byte[] decoded) {
        int lengthIndex = 12;
        int nameLength = decoded[lengthIndex] & 0xFF;
        return new String(decoded, lengthIndex + 1, nameLength, StandardCharsets.ISO_8859_1);
    }

    public static int getNumPreferences(byte[] decoded) {
        // salta: SERVER_COMMAND(2) + count(2) + length(2) = 6
        int offset = 6;

        // salta roomName
        int roomNameLen = decoded[offset++] & 0xFF;
        offset += roomNameLen;

        // salta roomType (1 byte)
        offset++;

        // salta descrizione
        int descLen = decoded[offset++] & 0xFF;
        offset += descLen;

        // salta ownerNickname
        int ownerLen = decoded[offset++] & 0xFF;
        offset += ownerLen;

        // salta UNKNOW_BYTE (4 byte)
        offset += 4;

        // leggi 2 byte e convertili in intero
        int high = decoded[offset] & 0xFF;
        int low  = decoded[offset + 1] & 0xFF;
        return (high << 8) | low;
    }

}
