package com.c6server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LoginUserConnPacket {

    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x04 };
    private final byte[] LEN = new byte[] { 0x00, 0x00 };
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

    public byte[] getLoginUserConnPacket() throws IOException {
        ByteArrayOutputStream loginUserConnPacketComposit = new ByteArrayOutputStream();

        loginUserConnPacketComposit.write(SERVER_COMMAND);
        loginUserConnPacketComposit.write(getCount());
        loginUserConnPacketComposit.write(LEN);

        byte[] loginUserConnPacket = loginUserConnPacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO LOGIN USER CONNECTION:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(LEN));

        return loginUserConnPacket;
    }
}
