package com.c6server.entity;

import com.c6server.utils.UtilsProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class SendPulsEntity {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x15 };

    private Integer count;
    private Integer numPuls;
    private String descr;
    private String link;

    private List<Button> buttons = new ArrayList<>();

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setNumPuls(Integer numPuls) {
        this.numPuls = numPuls;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setLink(String link) {
        this.link = link;
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

    public byte[] getLengthButton() {
        int buttonLen = 0;
        for (Button b : buttons) {
            buttonLen += UtilsProtocol.getLengthField(b.descr).length;
            buttonLen += UtilsProtocol.getLengthField(b.link).length;
        }

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((buttonLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (buttonLen & 0xFF);
        return totalLenBytes;
    }

    public byte[] getNumPuls() {
        byte[] value = new byte[2];
        value[0] = (byte) ((this.numPuls >> 8) & 0xFF);
        value[1] = (byte) (this.numPuls & 0xFF);
        return value;
    }

    public void addButton(String descr, String link) {
        this.buttons.add(new Button(descr, link));
    }

    public byte[] getLength() {


        int lenNumPuls = getNumPuls().length;
        byte[] buttonBytes = getLengthButton();

        int buttonLen = ((buttonBytes[0] & 0xFF) << 8) | (buttonBytes[1] & 0xFF);

        int totalLen = lenNumPuls + buttonLen;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }

    public byte[] getSndPuls() throws IOException {
        ByteArrayOutputStream sndPulsComposit = new ByteArrayOutputStream();
        sndPulsComposit.write(SERVER_COMMAND);
        sndPulsComposit.write(getCount());
        sndPulsComposit.write(getLength());
        sndPulsComposit.write(getNumPuls());
        for (Button b : buttons) {
            sndPulsComposit.write(UtilsProtocol.getLengthField(b.descr));
            sndPulsComposit.write(UtilsProtocol.getLengthField(b.link));
        }

        byte[] sndPuls = sndPulsComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO:");
        System.out.println("Server Command: " + java.util.HexFormat.ofDelimiter(" ").formatHex(SERVER_COMMAND));
        System.out.println("Command Count: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getCount()));
        System.out.println("Length (Total): " + java.util.HexFormat.ofDelimiter(" ").formatHex(getLength()));
        System.out.println("numPuls: " + java.util.HexFormat.ofDelimiter(" ").formatHex(getNumPuls()));
        System.out.println("Payload (Hex): " + java.util.HexFormat.ofDelimiter(" ").formatHex(sndPuls));



        return sndPuls;
    }


    public static class Button {
        String descr;
        String link;

        public Button(String descr, String link) {
            this.descr = descr;
            this.link = link;
        }
    }

}
