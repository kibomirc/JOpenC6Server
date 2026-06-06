package com.c6server.packet;

import com.c6server.c6enum.C6EnumRoom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class ProfileRoomPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x37 };
    private final byte[] UNKNOW_BYTE = new byte[] { 0x00, 0x00, 0x00, 0x00 };
    private String roomName;
    private String ownerNickname;
    private String descrizioneRoom;
    private String roomType;
    private Integer count;

    public void setCount(Integer count) {
        this.count = count;
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

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public void setDescrizioneRoom(String descrizioneRoom) {
        this.descrizioneRoom = descrizioneRoom;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public byte[] getRoomType() {
        return new byte[]{ C6EnumRoom.valueOf(roomType).getCode() };
    }

    public byte[] getLengthWithOwnerNickname() {
        int lenOwnerNickname = ownerNickname.length();
        byte lenOwnerNicknameByte = (byte) (lenOwnerNickname & 0xFF);

        byte[] ownerNicknameBytes = ownerNickname.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenOwnerNicknameByte}, ownerNicknameBytes);
    }

    public byte[] getLengthWithRoomName() {
        int lenRoomName = roomName.length();
        byte lenRoomNameByte = (byte) (lenRoomName & 0xFF);

        byte[] roomNameBytes = roomName.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenRoomNameByte}, roomNameBytes);
    }

    public byte[] getLengthWithDescrizioneRoom() {
        int lenDescrizioneRoom = descrizioneRoom.length();
        byte lenDescrizioneRoomByte = (byte) (lenDescrizioneRoom & 0xFF);

        byte[] descrizioneRoomBytes = descrizioneRoom.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenDescrizioneRoomByte}, descrizioneRoomBytes);
    }

    public byte[] getLength() {

        int lenRoomName = getLengthWithRoomName().length;
        int typeRoom = 1;
        int lenDescrizioneRoom = getLengthWithDescrizioneRoom().length;
        int lenOwnerNickname = getLengthWithOwnerNickname().length;
        int lenUnknowByte = UNKNOW_BYTE.length;

        int totalLen = lenRoomName + typeRoom + lenDescrizioneRoom + lenOwnerNickname + lenUnknowByte;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getProfileRoomPacket() throws IOException {
        ByteArrayOutputStream profileRoomPacketComposit = new ByteArrayOutputStream();

        profileRoomPacketComposit.write(SERVER_COMMAND);
        profileRoomPacketComposit.write(getCount());
        profileRoomPacketComposit.write(getLength());
        profileRoomPacketComposit.write(getLengthWithRoomName());
        profileRoomPacketComposit.write(getRoomType());
        profileRoomPacketComposit.write(getLengthWithDescrizioneRoom());
        profileRoomPacketComposit.write(getLengthWithOwnerNickname());
        profileRoomPacketComposit.write(UNKNOW_BYTE);

        byte[] profileRoomPacket = profileRoomPacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("LengthWithRoomName: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithRoomName()));
        System.out.println("RoomType: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getRoomType()));
        System.out.println("LengthWithDescrizioneRoom: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithDescrizioneRoom()));
        System.out.println("LengthWithDescrizioneRoom: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithDescrizioneRoom()));
        System.out.println("Preference ROOM da vedere dopo: " + java.util.HexFormat.ofDelimiter(" ").formatHex(UNKNOW_BYTE));
        System.out.println("Payload (Hex): " + java.util.HexFormat.ofDelimiter(" ").formatHex(profileRoomPacket));



        return profileRoomPacket;

    }



}
