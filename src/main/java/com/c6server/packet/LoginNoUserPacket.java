package com.c6server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LoginNoUserPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x0E };
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

    public byte[] getLoginNoUserPacket() throws IOException {
        ByteArrayOutputStream loginNoUserPacketComposit = new ByteArrayOutputStream();

        loginNoUserPacketComposit.write(SERVER_COMMAND);
        loginNoUserPacketComposit.write(getCount());
        loginNoUserPacketComposit.write(LEN);

        byte[] loginNoUserPacket = loginNoUserPacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO LOGIN USER NOT EXIST:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(LEN));

        return loginNoUserPacket;
    }
}
