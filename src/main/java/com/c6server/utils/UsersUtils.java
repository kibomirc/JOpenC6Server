package com.c6server.utils;

import java.nio.charset.StandardCharsets;

public class UsersUtils {

    public static String getProfileName(byte[] decoded) {
        int nameLength = decoded[12] & 0xFF;

        return new String(
                decoded,
                13,
                nameLength,
                StandardCharsets.ISO_8859_1
        );
    }
}
