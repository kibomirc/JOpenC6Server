package com.c6server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class LogoutUserPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x05 };
    private final byte[] UNKNOWBYTE = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00 };

    private Integer count;
    private String nickname;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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

    public byte[] getLengthWithNickname() {
        int lenNickname = nickname.length();
        byte lenNickNicknameByte = (byte) (lenNickname & 0xFF);

        byte[] nicknameBytes = nickname.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenNickNicknameByte}, nicknameBytes);
    }

    public byte[] getLength() {
        int lenLengthWithNickname = getLengthWithNickname().length;

        int totalLen = lenLengthWithNickname;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getLogoutUserPacket() throws IOException {
        ByteArrayOutputStream logoutUserPacket = new ByteArrayOutputStream();

        logoutUserPacket.write(SERVER_COMMAND);
        logoutUserPacket.write(getCount());
        logoutUserPacket.write(getLength());
        logoutUserPacket.write(getLengthWithNickname());

        System.out.println("LOG PACCHETTO COMPLETO: LOGOUT USER");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("UNKNOWBYTE :" + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithNickname()));

        System.out.println("Payload: " + java.util.HexFormat.ofDelimiter(" ").formatHex(logoutUserPacket.toByteArray()));
        return logoutUserPacket.toByteArray();
    }
}
