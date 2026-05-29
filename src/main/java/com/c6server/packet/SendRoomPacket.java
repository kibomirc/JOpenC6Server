package com.c6server.packet;

import com.c6server.utils.UtilsProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
// TODO aggiungiamo la stanza dei netfriend

public class SendRoomPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x36 };
    private final byte[] UNKNOWBYTE_PUBLIC_ROOM = new byte[] { 0x13, 0x07, 0x0B };
    private final byte[] UNKNOWBYTE_NETFRIENDS_ROOM = new byte[] { 0x44, 0x07, 0x0B };
    private final byte[] UNKNOWBYTE_2 = new byte[] { 0x00, 0x00 };

    private Integer count;
    private Integer numRooms; // 2 byte


    private List<Room> rooms = new ArrayList<>();
    private List<Room> netFriendRooms = new ArrayList<>();

    public void setCount(Integer count) {
        this.count = count;
    }


    public byte[] getNumRooms() {
        int total = rooms.size() + netFriendRooms.size();
        byte[] value = new byte[2];
        value[0] = (byte) ((total >> 8) & 0xFF);
        value[1] = (byte) (total & 0xFF);
        return value;
    }

    public byte[] getCount() {
        if (count < 0 || count > 0xFFFF) {
            throw new IllegalArgumentException("Valore fuori dal range di 2 byte");
        }

        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((count >> 8) & 0xFF);
        bytes[1] = (byte) (count & 0xFF);

        return bytes;
    }

    public void addRoom(String room, int numUsers) {
        this.rooms.add(new Room(room, numUsers));
    }

    public void addNetFriendRoom(String room, int numUsers) {
        this.netFriendRooms.add(new Room(room, numUsers));
    }

    public int getLengthWithRoomsAndNetFriendsRoom() {
        int total = 0;
        for (Room room : rooms) {
            total += 1 + room.nomeRoom.getBytes(StandardCharsets.ISO_8859_1).length; // 1 byte lunghezza + nome
            total += 5; // separatore [count] [UNKNOWBYTE_1]
        }

        for (Room netFriendroom : netFriendRooms) {
            total += 1 + netFriendroom.nomeRoom.getBytes(StandardCharsets.ISO_8859_1).length; // 1 byte lunghezza + nome
            total += 5; // separatore [count] [UNKNOWBYTE_1]
        }

        total += 4; // 2 * UNKNOWBYTE_2
        return total;
    }

    public byte[] getLength() {
        int lenNumRooms = getNumRooms().length;
        int lenWithRoomAndNetFriendsRoom = this.getLengthWithRoomsAndNetFriendsRoom();

        int totalLen = lenNumRooms + lenWithRoomAndNetFriendsRoom;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getSendRoom() throws IOException {
        ByteArrayOutputStream sendRoomsComposit = new ByteArrayOutputStream();
        sendRoomsComposit.write(SERVER_COMMAND);
        sendRoomsComposit.write(getCount());
        sendRoomsComposit.write(getLength());
        sendRoomsComposit.write(UNKNOWBYTE_2);
        sendRoomsComposit.write(getNumRooms());
        for (Room room : rooms) {
            sendRoomsComposit.write(room.getLengthWithRoom());
            sendRoomsComposit.write(room.getNumUsers());
            sendRoomsComposit.write(UNKNOWBYTE_PUBLIC_ROOM);
        }
        for (Room room : netFriendRooms) {
            sendRoomsComposit.write(room.getLengthWithRoom());
            sendRoomsComposit.write(room.getNumUsers());
            sendRoomsComposit.write(UNKNOWBYTE_NETFRIENDS_ROOM);
        }
        sendRoomsComposit.write(UNKNOWBYTE_2);

        byte[] sendRooms = sendRoomsComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("numRoom: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getNumRooms()));
        System.out.println("Payload Completo (Hex): " + java.util.HexFormat.ofDelimiter(" ").formatHex(sendRoomsComposit.toByteArray()));

        return sendRooms;
    }


    public static class Room {
        String nomeRoom;
        int numUsers;

        public Room(String nomeRoom, int numUsers) {
            this.nomeRoom = nomeRoom;
            this.numUsers = numUsers;
        }

        public byte[] getLengthWithRoom() {
            byte[] nameBytes = nomeRoom.getBytes(StandardCharsets.ISO_8859_1);
            byte[] result = new byte[1 + nameBytes.length];
            result[0] = (byte) nameBytes.length;
            System.arraycopy(nameBytes, 0, result, 1, nameBytes.length);
            return result;
        }

        public byte[] getNumUsers() {
            byte[] value = new byte[2];
            value[0] = (byte) ((numUsers >> 8) & 0xFF);
            value[1] = (byte) (numUsers & 0xFF);
            return value;
        }
    }
}
