package com.c6server.packet;

import com.c6server.utils.UtilsProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

// TODO deve essere modificato per gestire più room lo faccio dopo IMPORTANTE!
// TODO iniziamo aggiungendo la stanza dei netfriend
public class EnterRoomPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x32 };
    private final byte[] UNKNOWBYTE_1 = new byte[] { 0x03 };
    private final byte[] UNKNOWBYTE_2 = new byte[] { 0x00, 0x00 };
    private Integer count;
    private String nickname;
    private Room room;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void addRoom(String nomeRoom, int numUtenti) {
        this.room = new Room(nomeRoom, numUtenti);
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


    public byte[] getLengthWithNickname() {
        byte[] nicknameBytes = nickname.getBytes(StandardCharsets.UTF_8);
        return concatBytes(new byte[]{ (byte) nicknameBytes.length }, nicknameBytes);
    }

    public byte[] getLength() {
        int totalLen = room.getLengthWithRoom().length
                + UNKNOWBYTE_1.length
                + room.getNumUtenti().length
                + getLengthWithNickname().length
                + UNKNOWBYTE_2.length;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);
        return totalLenBytes;
    }

    public byte[] getEnterRoomPacket() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(SERVER_COMMAND);
        buf.write(getCount());
        buf.write(getLength());
        buf.write(room.getLengthWithRoom());
        buf.write(UNKNOWBYTE_1);
        buf.write(room.getNumUtenti());
        buf.write(getLengthWithNickname());
        buf.write(UNKNOWBYTE_2);

        byte[] enterRoomPacket = buf.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO ENTER ROOM PACKET:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("numUtenti: " + java.util.HexFormat.ofDelimiter(" ").formatHex(room.getNumUtenti()));
        System.out.println("Payload (Hex): " + java.util.HexFormat.ofDelimiter(" ").formatHex(enterRoomPacket));

        return enterRoomPacket;
    }

    public static class Room {
        String nomeRoom;
        int numUtenti;

        public Room(String nomeRoom, int numUtenti) {
            this.nomeRoom = nomeRoom;
            this.numUtenti = numUtenti;
        }

        public byte[] getLengthWithRoom() {
            byte[] roomBytes = nomeRoom.getBytes(StandardCharsets.UTF_8);
            return concatBytes(new byte[]{ (byte) roomBytes.length }, roomBytes);
        }

        public byte[] getNumUtenti() {
            byte[] value = new byte[2];
            value[0] = (byte) ((numUtenti >> 8) & 0xFF);
            value[1] = (byte) (numUtenti & 0xFF);
            return value;
        }
    }

}
