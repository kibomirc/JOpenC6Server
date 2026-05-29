package com.c6server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class MessageRoomPacket {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x30 };
    private final byte[] UNKNOWBYTE_1 = new byte[] { 0x03 };

    private Integer count;
    private String nicknameMittente;
    private String roomDestinazione;
    private byte[] stile;
    private String messaggio;



    public void setNicknameMittente(String nicknameMittente) {
        this.nicknameMittente = nicknameMittente;
    }


    public void setRoomDestinazione(String roomDestinazione) {
        this.roomDestinazione = roomDestinazione;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public byte[] getStile() {
        return stile;
    }

    public void setStile(byte[] stile) {
        this.stile = stile;
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

    public byte[] getLengthWithNicknameMittente() {
        int lenNicknameMittente = nicknameMittente.length();
        byte lenNickNicknameMittenteByte = (byte) (lenNicknameMittente & 0xFF);

        byte[] nicknameMittenteBytes = nicknameMittente.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenNickNicknameMittenteByte}, nicknameMittenteBytes);
    }


    public byte[] getLengthWithRoomDestinazione() {
        int lenRoomDestinazione = roomDestinazione.length();
        byte lenRoomDestinazioneByte = (byte) (lenRoomDestinazione & 0xFF);

        byte[] roomDestinazioneBytes = roomDestinazione.getBytes(StandardCharsets.UTF_8);

        return concatBytes(new byte[]{lenRoomDestinazioneByte}, roomDestinazioneBytes);
    }

    public byte[] getLengthStileWithMessage() {
        int totalLen = stile.length + messaggio.getBytes(StandardCharsets.UTF_8).length;

        byte[] lenBytes = new byte[2];
        lenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        lenBytes[1] = (byte) (totalLen & 0xFF);

        return lenBytes;
    }

    public byte[] getLengthWithMessaggio() {
        byte[] messaggioBytes = messaggio.getBytes(StandardCharsets.UTF_8);
        int lenMessaggio = messaggioBytes.length;

        byte[] lenBytes = new byte[2];
        lenBytes[0] = (byte) ((lenMessaggio >> 8) & 0xFF);
        lenBytes[1] = (byte) (lenMessaggio & 0xFF);

        return concatBytes(lenBytes, messaggioBytes);
    }

    public byte[] getLength() {
            int lenWithNickNameMittente = getLengthWithNicknameMittente().length;
            int lenWithRoomDestinazione = getLengthWithRoomDestinazione().length;
            int unknowByte1 = UNKNOWBYTE_1.length;
            int lenStileWithMessaggio = getLengthStileWithMessage().length;
            int lenStile = getStile().length;
            int lenMessaggio = getMessaggio().length();


            int totalLen = lenWithNickNameMittente + lenWithRoomDestinazione + lenWithNickNameMittente + unknowByte1 + lenStileWithMessaggio + lenStile + lenMessaggio;

            byte[] totalLenBytes = new byte[2];
            totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
            totalLenBytes[1] = (byte) (totalLen & 0xFF);

            return totalLenBytes;
    }

    public byte[] getMessageRoomPacket() throws IOException {
        ByteArrayOutputStream messageRoomPacket = new ByteArrayOutputStream();

        messageRoomPacket.write(SERVER_COMMAND);
        messageRoomPacket.write(getCount());
        messageRoomPacket.write(getLength());
        messageRoomPacket.write(getLengthWithNicknameMittente());
        messageRoomPacket.write(getLengthWithRoomDestinazione());
        messageRoomPacket.write(getLengthWithNicknameMittente());
        messageRoomPacket.write(UNKNOWBYTE_1);
        messageRoomPacket.write(getLengthStileWithMessage());
        messageRoomPacket.write(getStile());
        messageRoomPacket.write(getMessaggio().getBytes(StandardCharsets.UTF_8));

        System.out.println("LOG PACCHETTO COMPLETO: MESSAGE IN ROOM");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));

        System.out.println("Length With Nick Mittente: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithNicknameMittente()));
        System.out.println("Length With Nick Destinatario: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthWithRoomDestinazione()));
        System.out.println("Length Stile With Message: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLengthStileWithMessage()));
        System.out.println("Stile: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getStile()));
        System.out.println("Messaggio: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getMessaggio().getBytes(StandardCharsets.UTF_8)));

        System.out.println("Payload: " + java.util.HexFormat.ofDelimiter(" ").formatHex(messageRoomPacket.toByteArray()));
        return messageRoomPacket.toByteArray();
    }
}
