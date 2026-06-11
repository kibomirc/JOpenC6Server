package com.c6server.utils;

import java.nio.charset.StandardCharsets;

public class UsersUtils {

    public static String getProfileName(byte[] decoded) {
        int nameLength = decoded[decoded.length - 5] & 0xFF;

        return new String(
                decoded,
                decoded.length - nameLength,
                nameLength,
                StandardCharsets.ISO_8859_1
        );
    }
}
