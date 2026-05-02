package com.c6server.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UtilsProtocol {

    public static byte[] concatBytes(byte[] len, byte[] bytes) {
        byte[] result = new byte[len.length + bytes.length];
        System.arraycopy(len, 0, result, 0, len.length);
        System.arraycopy(bytes, 0, result, len.length, bytes.length);
        return result;
    }

    public static byte[] getLengthField(String text) {
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte lenByte = (byte) textBytes.length;
        return concatBytes(new byte[]{lenByte}, textBytes);
    }


// TODO to be implemented
    public static List<String> getReqUsers(byte[] decodePacket) {
        List<String> netFriends = new ArrayList<>();


        return netFriends;
    }

// TODO to be implemented
    public static List<String> getReqUsersOnLogin(byte[] decodePacket) {
        List<String> netFriends = new ArrayList<>();

        return netFriends;
    }

    public static byte extractCmdReqUserOnLogin(byte[] data) {
        if (data.length < 8) {
            throw new IllegalArgumentException("Pacchetto troppo corto: servono almeno 8 byte");
        }

        return data[21];
    }
}
