package com.c6server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class NotifyRoomPacket {

    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x33 };
    private Integer count;
    private String room;
    private String nickname;

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
        int lenNickname = getLengthWithNickname().length;

        int totalLen = lenRoom + lenNickname;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getNotifyRoomPacket() throws IOException {
        ByteArrayOutputStream notifyRoomPacketComposit = new ByteArrayOutputStream();
        notifyRoomPacketComposit.write(SERVER_COMMAND);
        notifyRoomPacketComposit.write(getCount());
        notifyRoomPacketComposit.write(getLength());
        notifyRoomPacketComposit.write(getLengthWithNickname());
        notifyRoomPacketComposit.write(getLengthWithRoom());



        byte[] notifyRoomPacket = notifyRoomPacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO NOTIFY ROOM PACKET:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("Nickname user in room: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithNickname()));
        System.out.println("Room to notify: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithRoom()));
        System.out.println("Payload : " + java.util.HexFormat.ofDelimiter(" ").formatHex(notifyRoomPacket));


        return notifyRoomPacket;
    }
}
