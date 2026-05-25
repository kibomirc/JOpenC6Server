package com.c6server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class ExitUserPacket {
    private final byte[] SERVER_COMMAND = new byte[] {0x20, 0x0A};
    private final byte[] BYTE_OFFLINE = new byte[] { 0x00 };
    private Integer count;
    private String nickname;

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNickname() {
        return nickname;
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

    public byte[] getLength() {
        int lenNickName = getLengthWithNickname().length;
        int byteOnlineLen = BYTE_OFFLINE.length;

        int totalLen = lenNickName + byteOnlineLen;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getExitUserPacket() throws IOException {
        ByteArrayOutputStream exitUserPacketComposit = new ByteArrayOutputStream();

        exitUserPacketComposit.write(SERVER_COMMAND);
        exitUserPacketComposit.write(getCount());
        exitUserPacketComposit.write(getLength());
        exitUserPacketComposit.write(getLengthWithNickname());
        exitUserPacketComposit.write(BYTE_OFFLINE);

        byte[] exitUserPacket = exitUserPacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO NEW USER:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("Length With Nickname: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithNickname()));
        System.out.println("BYTE_OFFLINE " + java.util.HexFormat.ofDelimiter(" ").formatHex(BYTE_OFFLINE));

        return exitUserPacket;
    }
}
