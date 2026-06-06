package com.c6server.utils;

import java.nio.charset.StandardCharsets;

public class RoomsUtils {

    public static String getRoomName(byte[] decoded) {
        int lengthIndex = 12;
        int nameLength = decoded[lengthIndex] & 0xFF;
        return new String(decoded, lengthIndex + 1, nameLength, StandardCharsets.UTF_8);
    }

}
