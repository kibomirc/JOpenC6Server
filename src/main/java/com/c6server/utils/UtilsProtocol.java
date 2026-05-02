package com.c6server.utils;

import java.nio.charset.StandardCharsets;

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
}
