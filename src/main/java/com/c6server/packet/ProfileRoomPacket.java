package com.c6server.packet;

import com.c6server.c6enum.C6EnumRoom;
import com.c6server.c6enum.C6EnumRoomPreferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class ProfileRoomPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x37 };
    private final byte[] UNKNOW_BYTE = new byte[] { 0x00, 0x00, 0x00, 0x01, 0x08, 0x04 };
    private String roomName;
    private String ownerNickname;
    private String descrizioneRoom;
    private String roomType;
    private Integer count;
    private List<RoomPreference> preferences = new ArrayList<>();

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

    public void addPreference(C6EnumRoomPreferences pref) {
        preferences.add(new RoomPreference(pref.getIndex(), pref.getVal()));
    }

    public byte[] getPreferences() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // contatore su 4 byte
        int count = preferences.size();
        out.write((count >> 24) & 0xFF);
        out.write((count >> 16) & 0xFF);
        out.write((count >> 8)  & 0xFF);
        out.write(count         & 0xFF);

        // per ogni preferenza: indice + valore
        for (RoomPreference pref : preferences) {
            out.write(pref.index);
            out.write(pref.value);
        }

        return out.toByteArray();
    }

    public byte[] getLengthWithRoomName() {
        byte[] roomNameBytes = roomName.getBytes(StandardCharsets.ISO_8859_1);
        byte lenRoomNameByte = (byte) (roomNameBytes.length & 0xFF);
        return concatBytes(new byte[]{lenRoomNameByte}, roomNameBytes);
    }

    public byte[] getLengthWithOwnerNickname() {
        byte[] ownerNicknameBytes = ownerNickname.getBytes(StandardCharsets.ISO_8859_1);
        byte lenOwnerNicknameByte = (byte) (ownerNicknameBytes.length & 0xFF);
        return concatBytes(new byte[]{lenOwnerNicknameByte}, ownerNicknameBytes);
    }

    public byte[] getLengthWithDescrizioneRoom() {
        byte[] descrizioneRoomBytes = descrizioneRoom.getBytes(StandardCharsets.ISO_8859_1);
        byte lenDescrizioneRoomByte = (byte) (descrizioneRoomBytes.length & 0xFF);
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

    public class RoomPreference {
        public final byte index;
        public final byte value;

        public RoomPreference(byte index, byte value) {
            this.index = index;
            this.value = value;
        }
    }

}
