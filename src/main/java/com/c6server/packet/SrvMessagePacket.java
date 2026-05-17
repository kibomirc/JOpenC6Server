package com.c6server.packet;

public class SrvMessagePacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x0F };
   // TO DO da implementare

    private String nickMittente;
    private String nickDestinatario;
    private String testo;

    // costruisce i byte da spedire al destinatario
    public byte[] getPacket() { return null; }
}
