package com.c6server.utils;

import com.c6server.c6enum.C6EnumRoom;
import com.c6server.c6enum.C6EnumRoomPreferences;
import com.c6server.model.RoomProfileEntity;

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

    public static String getRoomMessage(byte[] decoded) {
        // salta il nickname
        int nickLengthIndex = 12;
        int nickLength = decoded[nickLengthIndex] & 0xFF;

        // salta il nome della stanza
        int roomLengthIndex = nickLengthIndex + 1 + nickLength;
        int roomLength = decoded[roomLengthIndex] & 0xFF;

        // blocco messaggio: [2 byte lunghezza][2 byte header][testo]
        int msgBlockIndex = roomLengthIndex + 1 + roomLength;
        int msgBlockLength = ((decoded[msgBlockIndex] & 0xFF) << 8)
                |  (decoded[msgBlockIndex + 1] & 0xFF);

        int textIndex  = msgBlockIndex + 4;       // salta lunghezza (2) + header (2)
        int textLength = msgBlockLength - 2;      // il blocco include i 2 byte di header

        return new String(decoded, textIndex, textLength, StandardCharsets.ISO_8859_1);
    }

    // TODO da implementare
    public static RoomProfileEntity getRoomProfile(byte[] decoded) {
        RoomProfileEntity profile = new RoomProfileEntity();

        int offset = 12; // inizio nickname

        int nickLen = decoded[offset] & 0xFF;
        offset += 1 + nickLen;

        int roomLen = decoded[offset] & 0xFF;
        offset += 1 + roomLen;

        int descLen = decoded[offset] & 0xFF;
        offset += 1 + descLen;

        offset += 2; // tipo stanza + byte successivo

        int count = decoded[offset] & 0xFF;
        offset += 1;

        for (int i = 0; i < count; i++) {
            byte index = decoded[offset];
            byte val = decoded[offset + 1];
            try {
                C6EnumRoomPreferences pref = C6EnumRoomPreferences.fromBytes(index, val);
                profile.addGeneric(pref);
            } catch (IllegalArgumentException e) {
                System.out.println("Coppia non riconosciuta: " + index + "," + val);
            }
            offset += 2;
        }

        return profile;
    }

    // TODO da implementare
    public static String getTypeRoom(byte[] decoded) {
        return C6EnumRoom.PUBLIC_USER_ROOM.name();
    }

}
