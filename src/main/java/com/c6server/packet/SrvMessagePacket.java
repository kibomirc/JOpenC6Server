package com.c6server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class SrvMessagePacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x0F };

    // TO DO da implementare

    private String nickMittente;
    private String nickDestinatario;
    private byte[] stile;
    private String messaggio;
    private Integer count;

    public String getNickMittente() {
        return nickMittente;
    }

    public void setNickMittente(String nickMittente) {
        this.nickMittente = nickMittente;
    }

    public String getNickDestinatario() {
        return nickDestinatario;
    }

    public void setNickDestinatario(String nickDestinatario) {
        this.nickDestinatario = nickDestinatario;
    }

    public byte[] getStile() {
        return stile;
    }

    public void setStile(byte[] stile) {
        this.stile = stile;
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


    public void setCount(Integer count) {
        this.count = count;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public byte[] getLengthWithMittente() {
        int lenNickMittente = nickMittente.length();
        byte lenNickMittenteByte = (byte) (lenNickMittente & 0xFF);

        byte[] nickMittenteBytes = nickMittente.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenNickMittenteByte}, nickMittenteBytes);
    }

    public byte[] getLengthWithDestinatario() {
        int lenNickDestinatario = nickDestinatario.length();
        byte lenNickDestinatarioByte = (byte) (lenNickDestinatario & 0xFF);

        byte[] nickDestinatarioBytes = nickDestinatario.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenNickDestinatarioByte}, nickDestinatarioBytes);
    }


    public byte[] getLengthStileWithMessage() {
        int totalLen = stile.length + messaggio.getBytes(StandardCharsets.UTF_8).length;

        byte[] lenBytes = new byte[2];
        lenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        lenBytes[1] = (byte) (totalLen & 0xFF);

        return lenBytes;
    }


    public byte[] getLengthMessaggio() {
        int lenMessaggio = messaggio.length();
        byte lenMessaggioByte = (byte) (lenMessaggio & 0xFF);

        byte[] messaggioBytes = messaggio.getBytes(StandardCharsets.UTF_8);

        return messaggioBytes;
    }

    // costruisce i byte da spedire al destinatario
    public byte[] getLength() {

        int lenNickMittente = getLengthWithMittente().length;
        int lenNickDestinatario = getLengthWithDestinatario().length;
        int lenStileWithMessage  = getLengthStileWithMessage().length;
        int lenStileBytes  = stile.length;
        int lenMessaggio = getLengthMessaggio().length;

        int totalLen = lenNickMittente + lenNickDestinatario + lenStileWithMessage + lenStileBytes + lenMessaggio;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getSrvMessagePacket() throws IOException {
        ByteArrayOutputStream srvMessagePacketComposit = new ByteArrayOutputStream();

        srvMessagePacketComposit.write(SERVER_COMMAND);
        srvMessagePacketComposit.write(getCount());
        srvMessagePacketComposit.write(getLength());
        srvMessagePacketComposit.write(getLengthWithMittente());
        srvMessagePacketComposit.write(getLengthWithDestinatario());
        srvMessagePacketComposit.write(getLengthStileWithMessage());
        srvMessagePacketComposit.write(getStile());
        srvMessagePacketComposit.write(getMessaggio().getBytes(StandardCharsets.UTF_8));

        byte[] srvMessagePack = srvMessagePacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));

        System.out.println("Length With Nick Mittente: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithMittente()));
        System.out.println("Length With Nick Destinatario: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithDestinatario()));
        System.out.println("Length Stile With Message: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthStileWithMessage()));
        System.out.println("Stile: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getStile()));
        System.out.println("Messaggio: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getMessaggio().getBytes(StandardCharsets.UTF_8)));

        return srvMessagePack;
    }
}
