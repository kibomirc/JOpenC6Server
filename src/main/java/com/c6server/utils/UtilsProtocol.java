package com.c6server.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsProtocol {

    private final static Integer INIT_NICK_LIST = 14;

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
        // 10 0F 00 03 00 10 10 03 00 03 00 0A 00 01 07 62 69 67 61 6C 65 78 05 30 0A 7D 53 44 31 7D 2A 31 73 2B 74
        // 10 0F 00 03 00 10 10 03 00 03 00 0A 00 01 07 62 69 67 61 6C 65 78 67 3A 0E 57 5F 3B 47 16 42 3F 5D 0D 07
        List<String> netFriends = new ArrayList<>();
        // 12 e 13 sono i byte per il numero di nick
        int numNick = ((decodePacket[12] & 0xFF) << 8) | (decodePacket[13] & 0xFF);

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
            lenNick = decodePacket[INIT_NICK_LIST + sumNick + netFriends.size()] & 0xFF;
        } else {
            lenNick = decodePacket[INIT_NICK_LIST] & 0xFF;
        }

        return lenNick;
    }

    private static Integer getPosNick(List<String> netFriends) {

        Integer sumNick = netFriends.stream()
                .mapToInt(String::length)
                .sum();

        return INIT_NICK_LIST + sumNick + netFriends.size() + 1;
    }

    public static List<String> getReqUsersOnLogin(byte[] decodePacket) {
        //Pacchetto Originale: 10 0F 00 02 00 08 10 0D 00 02 00 02 01 2A 3D 67 53 19 29 25 10 03 00 03 00 16 00 03 07 62 69 67 61 6C 65 78 04 69 76 61 6E 06 64 61 78 77 65 62 36 22 68 57 1A 2E 0F 01 45 3F 49 32 74
        //Pacchetto Normalizzato                                                           10 03 00 03 00 16 00 03 07 62 69 67 61 6C 65 78 04 69 76 61 6E 06 64 61 78 77 65 62 36 22 68 57 1A 2E 0F 01 45 3F 49 32 74
        List<String> netFriends = new ArrayList<>();
        byte[] normalizePacket = Arrays.copyOfRange(decodePacket, 14, decodePacket.length);

        System.out.println("");

        System.out.print("Pacchetto Originale: ");
        for (byte b : decodePacket) {
            System.out.printf("%02X ", b);
        }
        System.out.println(); // Va a capo per separare i flussi

        // 2. Stampa del pacchetto normalizzato (dal byte 19 in poi)
        System.out.print("Pacchetto Normalizzato (dal 19 a fine): ");
        for (byte b : normalizePacket) {
            System.out.printf("%02X ", b);
        }
        System.out.println("\n"); // Va a capo due volte


        netFriends = getReqUsers(normalizePacket);

        return netFriends;
    }

    public static byte extractCmdReqUserOnLogin(byte[] data) {
        if (data.length < 8) {
            throw new IllegalArgumentException("Pacchetto troppo corto: servono almeno 8 byte");
        }

        if(data.length < 21) { return 0; }
        return data[21];
    }
}
