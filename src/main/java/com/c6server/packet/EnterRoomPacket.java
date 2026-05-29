package com.c6server.packet;

import com.c6server.utils.UtilsProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

// TODO deve essere modificato per gestire più room lo faccio dopo IMPORTANTE!
public class EnterRoomPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x32 };
    private final byte[] UNKNOWBYTE_1 = new byte[] { 0x03 };
    private final byte[] UNKNOWBYTE_2 = new byte[] { 0x00, 0x00 };
    private Integer count;
    private String room;
    private int numUtenti;
    private String nickname;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCount(Integer count) {
        this.count = count;
    }


    public void setRoom(String room) {
        this.room = room;
    }

    public void setNumUtenti(int numUtenti) {
        this.numUtenti = numUtenti;
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

    public byte[] getNumUtenti() {
        byte[] value = new byte[2];
        value[0] = (byte) ((this.numUtenti >> 8) & 0xFF);
        value[1] = (byte) (this.numUtenti & 0xFF);
        return value;
    }

    public byte[] getLengthWithNickname() {
        int lenNickname = nickname.length();
        byte lenNickNicknameByte = (byte) (lenNickname & 0xFF);

        byte[] nicknameBytes = nickname.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenNickNicknameByte}, nicknameBytes);
    }


    public byte[] getLengthWithRoom() {
        int lenRoom = room.length();
        byte lenRoomByte = (byte) (lenRoom & 0xFF);
        byte[] roomByte = room.getBytes(StandardCharsets.UTF_8);
        return concatBytes(new byte[]{lenRoomByte}, roomByte);
    }

    public byte[] getLength() {
        int lenRoom = getLengthWithRoom().length;
        int unknowbyte1 = UNKNOWBYTE_1.length;
        int numUtenti = getNumUtenti().length;
        int lenNickname = getLengthWithNickname().length;
        int unknowbyte2 = UNKNOWBYTE_2.length;

        int totalLen = lenRoom + unknowbyte1 + numUtenti + lenNickname + unknowbyte2;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getEnterRoomPacket() throws IOException {
        ByteArrayOutputStream enterRoomPacketComposit = new ByteArrayOutputStream();
        enterRoomPacketComposit.write(SERVER_COMMAND);
        enterRoomPacketComposit.write(getCount());
        enterRoomPacketComposit.write(getLength());
        enterRoomPacketComposit.write(getLengthWithRoom());
        enterRoomPacketComposit.write(UNKNOWBYTE_1);
        enterRoomPacketComposit.write(getNumUtenti());
        enterRoomPacketComposit.write(getLengthWithNickname());
        enterRoomPacketComposit.write(UNKNOWBYTE_2);


        byte[] enterRoomPacket = enterRoomPacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO ENTER ROOM PACKET:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("numPuls: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getNumUtenti()));
        System.out.println("Payload (Hex): " + java.util.HexFormat.ofDelimiter(" ").formatHex(enterRoomPacket));



        return enterRoomPacket;
    }


}
