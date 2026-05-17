package com.c6server.packet;

import com.c6server.utils.UtilsProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendUsersPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x06 };

    private Integer count;
    private List<NetFriends>  netFriendsOnline = new ArrayList<>();

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

    public void addNetFriend(String nick) {
        this.netFriendsOnline.add(new NetFriends(nick));
    }

    public byte[] getNumNick() {
        int netFriendsOnlineIndex = this.netFriendsOnline.size();

        byte[] totalLenNumNickBytes = new byte[2];
        totalLenNumNickBytes[0] = (byte) ((netFriendsOnlineIndex >> 8) & 0xFF);
        totalLenNumNickBytes[1] = (byte) (netFriendsOnlineIndex & 0xFF);
        return totalLenNumNickBytes;
    }

    public byte[] getLength() {
        int lenNick = 2;
        int nickLength = 0;
        for (NetFriends nf : netFriendsOnline) {
            nickLength += UtilsProtocol.getLengthField(nf.nick).length;
        }

        int totalLen = lenNick + nickLength;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }


    public byte[] getSndUsers() throws IOException {
        ByteArrayOutputStream sndUsersComposit = new ByteArrayOutputStream();
        sndUsersComposit.write(SERVER_COMMAND);
        sndUsersComposit.write(getCount());
        sndUsersComposit.write(getLength());
        sndUsersComposit.write(getNumNick());
        for (SendUsersPacket.NetFriends netFriend : netFriendsOnline) {
            sndUsersComposit.write(UtilsProtocol.getLengthField(netFriend.nick));
        }

        byte[] sndUsers = sndUsersComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("numNick: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getNumNick()));
        System.out.println("Payload (Hex): " + java.util.HexFormat.ofDelimiter(" ").formatHex(sndUsers));



        return sndUsers;
    }

    private static class NetFriends {
        String nick;

        public NetFriends(String descr) {
            this.nick = descr;
        }
    }
}
