package com.c6server.entity;

import com.c6server.utils.UtilsProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class SendPulsEntity {
    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x15 };

    private Integer count;
    private Integer numPuls;
    private String descr;
    private String link;

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

    public byte[] getNumPuls() {
        byte[] value = new byte[1];
        value[0] = (byte) ((int) this.numPuls);
        return value;
    }

    public byte[] getLengthWithDescr() {
        byte[] descrBytes = descr.getBytes(StandardCharsets.UTF_8);
        int lenDescr = descrBytes.length;

        byte lenDescrByte = (byte) lenDescr;

        return UtilsProtocol.concatBytes(new byte[]{lenDescrByte}, descrBytes);
    }

    public byte[] getLengthWithLink() {
        byte[] linkBytes = link.getBytes(StandardCharsets.UTF_8);
        int lenLink = linkBytes.length;

        byte lenLinkByte = (byte) lenLink;

        return UtilsProtocol.concatBytes(new byte[]{lenLinkByte}, linkBytes);
    }

    public byte[] getLength() {


        int lenNumPuls = getNumPuls().length; // numPuls
        int lenLink = getLengthWithLink().length; // lenLink + link
        int lenDescr = getLengthWithDescr().length;    // lenDescr + Descr

        int totalLen = lenNumPuls + lenLink + lenDescr;

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
        sndPulsComposit.write(getLengthWithDescr());
        sndPulsComposit.write(getLengthWithLink());

        byte[] sndPuls = sndPulsComposit.toByteArray();

        return sndPuls;
    }


}
