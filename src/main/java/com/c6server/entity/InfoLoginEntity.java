package com.c6server.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InfoLoginEntity {

    private final byte[] SERVER_COMMAND = new byte[] {0x20, 0x01};
    private final byte[] UNKNOWBYTE_1 = new byte[] {0x00, 0x00, 0x00, 0x01};
    private final byte[] UNKNOWBYTE_2 = new byte[] {0x00, 0x5A, 0x01};
    private final byte[] UNKNOWBYTE_3 = new byte[] {0x00, 0x00, 0x00 ,0x14};

    private Integer count;
    private String numBanners;
    private Integer lenGif;
    private String gif;
    private Integer lenLinkBanner;
    private String linkBanner;
    private Integer lenNome;
    private String nome;
    private String numeroPulsanti;
    private String id;
    private Integer lenLinkButton;
    private String linkButton;
    private Integer lenDescr;
    private String descr;

    // TODO Sistemare le len devono essere rappresentate su due byte unsigned

    public void setGif(String gif) {
        this.gif = gif;
    }

    public void setLinkBanner(String linkBanner) {
        this.linkBanner = linkBanner;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNumeroPulsanti(String numeroPulsanti) {
        this.numeroPulsanti = numeroPulsanti;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLinkButton(String linkButton) {
        this.linkButton = linkButton;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public static byte[] concatBytes(byte[] len, byte[] bytes) {
        byte[] result = new byte[len.length + bytes.length];
        System.arraycopy(len, 0, result, 0, len.length);
        System.arraycopy(bytes, 0, result, len.length, bytes.length);
        return result;
    }

    public byte[] getNumBannersBytes() {
        // per il momento gestiamo un solo banner
        return "1".getBytes(StandardCharsets.UTF_8);
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

    public byte[] getLengthWithGif() {

        int lenGif = gif.length();
        byte[] lenGifBytes = new byte[2];
        lenGifBytes[0] = (byte) ((lenGif >> 8) & 0xFF);
        lenGifBytes[1] = (byte) (lenGif & 0xFF);

        byte[] gifBytes = gif.getBytes(StandardCharsets.UTF_8);

        return concatBytes(lenGifBytes,gifBytes);
    }

    public byte[] getLengthWithLinkBanner() {

        int lenLinkBanner = linkButton.length();
        byte[] lenLinkBannerBytes = new byte[2];
        lenLinkBannerBytes[0] = (byte) ((lenLinkBanner >> 8) & 0xFF);
        lenLinkBannerBytes[1] = (byte) (lenLinkBanner & 0xFF);

        byte[] linkBannerBytes = linkBanner.getBytes(StandardCharsets.UTF_8);

        return concatBytes(lenLinkBannerBytes,linkBannerBytes);
    }

    public byte[] getLengthWithName() {

        int lenNome = nome.length();
        byte[] lenNomeBytes = new byte[2];
        lenNomeBytes[0] = (byte) ((lenNome >> 8) & 0xFF);
        lenNomeBytes[1] = (byte) (lenNome & 0xFF);


        byte[] linkBannerBytes = linkBanner.getBytes(StandardCharsets.UTF_8);

        return concatBytes(lenNomeBytes,linkBannerBytes);
    }

    public byte[] getNumeroPulsanti() {
        // per il momento gestiamo un solo pulsante
        return "1".getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getId() {
        int idNum = Integer.parseInt(id);

        byte[] value = new byte[1];
        value[0] = (byte) (0x48 + idNum);

        return value;
    }

    public byte[] getLengthWithLinkButton() {

        int lenLinkButton = linkButton.length();
        byte[] lenLinkButtonBytes = new byte[2];
        lenLinkButtonBytes[0] = (byte) ((lenLinkButton >> 8) & 0xFF);
        lenLinkButtonBytes[1] = (byte) (lenLinkButton & 0xFF);

        byte[] linkButtonBytes = linkButton.getBytes(StandardCharsets.UTF_8);

        return concatBytes(lenLinkButtonBytes,linkButtonBytes);
    }

    public byte[] getLengthWithDescr() {


        int lenDescr = descr.length();
        byte[] lenDescrBytes = new byte[2];
        lenDescrBytes[0] = (byte) ((lenDescr >> 8) & 0xFF);
        lenDescrBytes[1] = (byte) (lenDescr & 0xFF);

        byte[] descrBytes = descr.getBytes(StandardCharsets.UTF_8);

        return concatBytes(lenDescrBytes,descrBytes);
    }

    public byte[] getLength() {

        int unknownBytes = 11;
        int lenNumBanner = getNumBannersBytes().length; // numBanners
        int lenGif = getLengthWithGif().length; // lenGif + Gif
        int lenBanner = getLengthWithLinkBanner().length; // LenLinkBanner + LinkBanner
        int lenName = getLengthWithName().length; // lenNome + nome
        int lenButton = getNumeroPulsanti().length; // numPulsanti
        int lenId = getId().length; // Id
        int lenLink = getLengthWithLinkButton().length; // lenLinkButton + linkButton
        int lenDescr = getLengthWithDescr().length; // LenDescr + Descr

        int totalLen = unknownBytes + lenNumBanner + lenGif + lenBanner + lenName + lenButton + lenId + lenLink + lenDescr;

        return new byte[] { (byte) (totalLen & 0xFF) };
    }

    public byte[] getInfoLogin() throws IOException {
        ByteArrayOutputStream infoLoginComposit = new ByteArrayOutputStream();
        infoLoginComposit.write(SERVER_COMMAND);
        infoLoginComposit.write(getCount());
        infoLoginComposit.write(getLength());
        infoLoginComposit.write(UNKNOWBYTE_1);
        infoLoginComposit.write(getNumBannersBytes());
        infoLoginComposit.write(getLengthWithGif());
        infoLoginComposit.write(getLengthWithLinkBanner());
        infoLoginComposit.write(getLengthWithName());
        infoLoginComposit.write(getNumeroPulsanti());
        infoLoginComposit.write(UNKNOWBYTE_2);
        infoLoginComposit.write(getId());
        infoLoginComposit.write(getLengthWithLinkButton());
        infoLoginComposit.write(getLengthWithDescr());
        infoLoginComposit.write(UNKNOWBYTE_3);

        byte[] infoLogin = infoLoginComposit.toByteArray();

        return infoLogin;
    }




}
