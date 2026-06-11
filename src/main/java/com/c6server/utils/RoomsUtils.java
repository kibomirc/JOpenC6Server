package com.c6server.utils;

import java.nio.charset.StandardCharsets;

public class RoomsUtils {

    public static String getRoomNameProfile(byte[] decoded) {
        int lengthIndex = 12;
        int nameLength = decoded[lengthIndex] & 0xFF;
        return new String(decoded, lengthIndex + 1, nameLength, StandardCharsets.ISO_8859_1);
    }

    public static String getRoomName(byte[] decoded) {
            int nickLengthIndex = 12;
            int nickLength = decoded[nickLengthIndex] & 0xFF;

            int roomLengthIndex = nickLengthIndex + 1 + nickLength;   // salta il nickname
            int roomLength = decoded[roomLengthIndex] & 0xFF;

            return new String(decoded, roomLengthIndex + 1, roomLength, StandardCharsets.ISO_8859_1);
    }

}
