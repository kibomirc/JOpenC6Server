package com.c6server.packet;

import com.c6server.utils.UtilsProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

// TODO deve essere modificato per gestire più room lo faccio dopo IMPORTANTE!
// TODO iniziamo aggiungendo la stanza dei netfriend
public class EnterRoomPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x32 };
    private final byte[] UNKNOWBYTE_1 = new byte[] { 0x03 };
    private final byte[] UNKNOWBYTE_2 = new byte[] { 0x00, 0x00 };
    private Integer count;
    private Room room;


    public void setCount(Integer count) {
        this.count = count;
    }

    public void addRoom(String nomeRoom, int numUtenti,List<String> nicknames) {
        this.room = new Room(nomeRoom, numUtenti, nicknames);
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

    public byte[] getLength() throws IOException {
        int totalLen = room.getLengthWithRoom().length
                + UNKNOWBYTE_1.length
                + room.getNumUtenti().length
                + room.getNicknames().length
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
        buf.write(room.getNicknames());
        buf.write(UNKNOWBYTE_2);
        return buf.toByteArray();
    }

    public static class Room {
        String nomeRoom;
        int numUtenti;
        List<String> nicknames;

        public Room(String nomeRoom, int numUtenti, List<String> nicknames) {
            this.nomeRoom = nomeRoom;
            this.numUtenti = numUtenti;
            this.nicknames = nicknames;
        }

        public byte[] getNicknames() throws IOException {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (String nick : nicknames) {
                byte[] nickBytes = nick.getBytes(StandardCharsets.UTF_8);
                buf.write((byte) nickBytes.length);
                buf.write(nickBytes);
            }
            return buf.toByteArray();
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
