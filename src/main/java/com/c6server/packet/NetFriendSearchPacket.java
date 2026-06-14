package com.c6server.packet;

import com.c6server.c6enum.C6EnumNetFriend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NetFriendSearchPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x16 };

    private int count;

    private List<String> netFriends = new ArrayList();

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

    public int lengthNetFriends() {
        return netFriends.size();
    }

    public void addNetFriend(String nickname) {
        netFriends.add(nickname);
    }

    public byte[] getNumNetFriends() {
        int size = netFriends.size();
        byte[] value = new byte[2];
        value[0] = (byte) ((size >> 8) & 0xFF);
        value[1] = (byte) (size & 0xFF);
        return value;
    }

    public byte[] getLengthWithNetFriends() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (String nick : netFriends) {
            byte[] nickBytes = nick.getBytes(StandardCharsets.ISO_8859_1);
            buf.write((byte) nickBytes.length);
            buf.write(nickBytes);
            buf.write(C6EnumNetFriend.ONLY_NETFRIEND.getCode());
        }
        buf.write(new byte[]{ 0x00, 0x00 });
        return buf.toByteArray();
    }

    public byte[] getLength() {
        int lengthNetFriends = 2;
        int status = 0;
        int unknowByte = 2;

        int numLengthWithNick = 0;
        for (String nick : netFriends) {
            numLengthWithNick  += 1 + nick.getBytes(StandardCharsets.ISO_8859_1).length;
            status += 1;
        }


        int totalLen = lengthNetFriends + numLengthWithNick + status + unknowByte;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getNetFriendSearchPacket() throws IOException {
        ByteArrayOutputStream netFriendSearchPacket = new ByteArrayOutputStream();

        netFriendSearchPacket.write(SERVER_COMMAND);
        netFriendSearchPacket.write(getCount());
        netFriendSearchPacket.write(getLength());
        netFriendSearchPacket.write(getNumNetFriends());
        if(lengthNetFriends() > 0) netFriendSearchPacket.write(getLengthWithNetFriends());


        System.out.println("LOG PACCHETTO COMPLETO: NET FRIENDS SEARCH PACKET");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));

        if(lengthNetFriends() > 0) System.out.println("Length With Nick Mittente: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithNetFriends()));


        System.out.println("Payload: " + java.util.HexFormat.ofDelimiter(" ").formatHex(netFriendSearchPacket.toByteArray()));
        return netFriendSearchPacket.toByteArray();
    }


}
