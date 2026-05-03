package com.c6server.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsProtocol {

    private final static Integer INIT_NICK_LIST = 12;

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



    public static List<String> getReqUsers(byte[] decodePacket) {
        // 10 0F 00 06 00 10 10 03 00 06 00 0A 00 01 07 62 69 67 61 6C 65 78 (in tuo onore xD)
        List<String> netFriends = new ArrayList<>();
        // 12 e 13 sono i byte per il numero di nick
        int numNick = ((decodePacket[11] & 0xFF) << 8) | (decodePacket[12] & 0xFF);

        for(int i=0; i < numNick; i++) { // ciclo per il numero dei nick
             // prelevo la lunghezza del nick
            int lenNick = getNickLength(netFriends,decodePacket);
            StringBuilder nick = new StringBuilder();
            for(int inick=0; inick < lenNick; inick++) {
                char cnick = (char) decodePacket[getPosNick(netFriends) + inick];
                nick.append(cnick);
            }
            netFriends.add(String.valueOf(nick));
        }



        return netFriends;
    }


    private static Integer getNickLength(List<String> netFriends, byte[] decodePacket) {
        int lenNick;

        Integer sumNick = netFriends.stream()
                .mapToInt(String::length)
                .sum();

        if(sumNick != 0) {
            lenNick = decodePacket[INIT_NICK_LIST + sumNick + netFriends.size() + 1] & 0xFF;
        } else {
            lenNick = decodePacket[INIT_NICK_LIST + 1] & 0xFF;
        }

        return lenNick;
    }

    private static Integer getPosNick(List<String> netFriends) {

        Integer sumNick = netFriends.stream()
                .mapToInt(String::length)
                .sum();

        return INIT_NICK_LIST + sumNick + netFriends.size() + 2;
    }

    public static List<String> getReqUsersOnLogin(byte[] decodePacket) {
        //10 0F 00 02 00 08 10 0D 00 02 00 02 01 2A 07 18 17 14 17 00 10 03 00 03 00 11 00 02 07 62 69 67 61 6C 65 78 06 64 61 78 77 65 62 07 18 17 13 17 10 10 0A 00 04 00 01 64
        List<String> netFriends = new ArrayList<>();
        byte[] normalizePacket = Arrays.copyOfRange(decodePacket, 15, decodePacket.length);
        netFriends = getReqUsers(normalizePacket);

        return netFriends;
    }

    public static byte extractCmdReqUserOnLogin(byte[] data) {
        if (data.length < 8) {
            throw new IllegalArgumentException("Pacchetto troppo corto: servono almeno 8 byte");
        }

        return data[21];
    }
}
