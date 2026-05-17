package com.c6server.utils;

import com.c6server.model.LoginEntity;
import com.c6server.packet.SendUsersPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

    // TODO Algoritmo codifica password e nickname
    // Codifica una stringa usando una chiave server e un flag Psw
    public static void GenerKey(byte[] pDest, String pSorg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(pSorg.getBytes(StandardCharsets.ISO_8859_1));
        System.arraycopy(digest, 0, pDest, 0, 16);
    }

    public static byte[] codecC6(String strCode, byte[] strKeyServer, byte[] strDest, boolean psw) throws
            NoSuchAlgorithmException {
        StringBuilder tt = new StringBuilder();

        if (psw) {
            tt.append(strCode);
            tt.append((char) (strKeyServer[0] & 0xFF));
            tt.append((char) (strKeyServer[2] & 0xFF));
        } else {
            tt.append("ANOCI");
            tt.append(Character.toLowerCase(strCode.charAt(strCode.length() - 1)));
            tt.append(Character.toLowerCase(strCode.charAt(0)));
            tt.append((char) (strKeyServer[4] & 0xFF));
            tt.append((char) (strKeyServer[2] & 0xFF));
        }

        // Genera la chiave MD5 da tt
        byte[] rawKey = new byte[16];
        GenerKey(rawKey, tt.toString());

        for (int i = 0; i < 16; i++) {
            int temp = rawKey[i] & 0xFF;
            temp = temp % 0x5E; // modulo 94
            temp += 0x20;       // somma 0x20 (ASCII 32)
            strDest[i] = (byte) temp;
        }

        return strDest;
    }

    public static boolean checkC6Control(byte[] orderKey, String stringEncode, byte[] compareDecodeValue,
                                         boolean psw) throws NoSuchAlgorithmException {
        byte[] hash = new byte[16];
        codecC6(stringEncode, orderKey, hash, psw);
        return Arrays.equals(hash, compareDecodeValue);
    }

    public static byte[] readNBytesFromIndex(byte[] data, int startIndex, int n) {
        if (startIndex < 0 || startIndex >= data.length) {
            throw new IllegalArgumentException("Indice di partenza non valido");
        }
        if (n < 0 || startIndex + n > data.length) {
            throw new IllegalArgumentException("Numero di byte da leggere non valido");
        }
        byte[] result = new byte[n];
        System.arraycopy(data, startIndex + 1, result, 0, n);
        return result;
    }

    public static byte[] codKey() {
        // azzero codifica
        byte[] randomBytes = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        for (int i = 0; i < randomBytes.length; i++) {
            randomBytes[i] = (byte) (randomBytes[i] & 0x7F);
        }

        return randomBytes;
    }

    public static byte extractCmdClient(byte[] data) {
        if (data.length < 8) {
            throw new IllegalArgumentException("Pacchetto troppo corto: servono almeno 8 byte");
        }

        return data[7]; // ottavo byte
    }

    public static byte[] reorderKey(byte[] key) {
        /*
         Prendo la chiava inviata al client e la riordino
         una volta riordinata dovrei anche salvara da qualche parte per utilizza futuri
         prevedo di usare redis
         */

        // 1 riordino chiave

        byte[] orderKey = new byte[8];

        // prelevo il 6 byte e si fa il mod 5
        int step = key[5] % 7;
        orderKey[0] = key[0];

        int start = 1;
        for (int i = 1; i < key.length - 1; i++) {
            int selectElement = (start + step);

            if (selectElement <= 7) {
                orderKey[i] = key[selectElement - 1];
                start = selectElement;
            } else {
                selectElement = selectElement % 7;
                orderKey[i] = key[selectElement - 1];
                start = selectElement;
            }
        }

        orderKey[7] = key[7];

        return orderKey;
    }

    public static byte[] decodePacket(byte[] data, byte[] orderKey) {

        // Devo suddividere i byte in 7 byte e fare uno xor con la orderKey
        // La decodifica su applica dal 7 byte in poi.

        // preleviamo da data dal 7 byte in poi
        byte[] bytesDecode = Arrays.copyOfRange(data, 0, 6);
        byte[] bytesEncoded = Arrays.copyOfRange(data, 6, data.length);
        List<byte[]> encodeBlock = new ArrayList<>();
        List<Byte> decodeBlock = new ArrayList<>();

        // per ogni blocco devo fare un xor con la orderKey -1
        byte[] xorKey = new byte[7];
        System.arraycopy(orderKey, 0, xorKey, 0, 7);

        // Divido i byteEncoded in gruppi da 7 byte
        for (int i = 0; i < bytesEncoded.length; i += 7) {
            int end = Math.min(i + 7, bytesEncoded.length);
            int length = end - i;
            byte[] block = new byte[length];
            System.arraycopy(bytesEncoded, i, block, 0, length);

            // stiamo blocco encodato
            System.out.println();

            encodeBlock.add(block);
        }

        for (byte[] block : encodeBlock) {
            for (int i = 0; i < block.length; i++) {
                byte[] xorBlock = new byte[7];
                xorBlock[i] = (byte) ((block[i] & 0xFF) ^ (xorKey[i] & 0xFF)); // XOR tra i valori unsigned

                // Stampiamo il risultato
                System.out.printf("block[%d] = 0x%02X, xorKey[%d] = 0x%02X, result = 0x%02X%n",
                        i, block[i], i, xorKey[i], xorBlock[i]);

                decodeBlock.add(xorBlock[i]);

            }
        }


        for (int i = bytesDecode.length - 1; i >= 0; i--) {
            decodeBlock.add(0, bytesDecode[i]); // Aggiunge all'inizio della lista
        }

        // Stampa il risultato in esadecimale

        byte[] decodeBlockByte = new byte[decodeBlock.size()];
        System.out.println("Risultato XOR:");
        for (int i = 0; i < decodeBlock.size(); i++) {
            byte b = decodeBlock.get(i);
            decodeBlockByte[i] = b;
            System.out.printf("%02X ", b);
        }


        return decodeBlockByte;
    }

    // Stampa nickname
    // Stampa nickname codificato
    // Stampa password codificata
    public static LoginEntity loginData(byte[] data) {
        // Devo andare all undicesimo byte e prelevare la lunghezza
        // e poi devo prelevare dal dodicesimo byte più la lunghezza

        int posNickLen = 12;
        byte nickLength = data[posNickLen];
        byte[] nick = readNBytesFromIndex(data, posNickLen, nickLength);

        int posPassLen = posNickLen + nickLength + 1;
        byte passLength = data[posPassLen];
        byte[] passEncode = readNBytesFromIndex(data, posPassLen, passLength);

        int posNickLen2 = posPassLen + passLength + 1;
        byte nickLength2 = data[posNickLen2];
        byte[] nickEncode = readNBytesFromIndex(data, posNickLen2, nickLength2);

        String nickAscii = new String(nick, StandardCharsets.US_ASCII);

        System.out.println("NICKNAME: " + nickAscii);

        System.out.print("PASSWORD ENCODED: ");
        for (byte b : passEncode) {
            System.out.printf("%02X ", b);
        }

        System.out.println();

        System.out.print("NICK ENCODED: ");
        for (byte b : nickEncode) {
            System.out.printf("%02X ", b);
        }

        System.out.println();

        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setNick(nickAscii);
        loginEntity.setPassEncoded(passEncode);
        loginEntity.setNickEncoded(nickEncode);

        return loginEntity;
    }
}
