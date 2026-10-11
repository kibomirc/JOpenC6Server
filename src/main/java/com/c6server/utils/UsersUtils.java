package com.c6server.utils;

import com.c6server.c6enum.C6EnumStatus;

import java.nio.charset.StandardCharsets;

public class UsersUtils {


    private static final int CHNG_STATUS_PACKET_LEN = 13;

    public static String getProfileName(byte[] decoded) {
        int nameLength = decoded[12] & 0xFF;

        return new String(
                decoded,
                13,
                nameLength,
                StandardCharsets.ISO_8859_1
        );
    }

    public static C6EnumStatus parseStatus(byte[] decoded) {
        if (decoded == null || decoded.length != CHNG_STATUS_PACKET_LEN) return null;
        return C6EnumStatus.fromCode(decoded[CHNG_STATUS_PACKET_LEN - 1]);
    }
}
