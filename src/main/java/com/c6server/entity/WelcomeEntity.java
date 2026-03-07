package com.c6server.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WelcomeEntity {
    private final byte[] SERVER_COMMAND = new byte[] {0x20, 0x10};

    private Integer count;
    String lenBenvenuto;
    String benvenuto;

    public void setBenvenuto(String benvenuto) {
        this.benvenuto = benvenuto;
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

    public byte[] getLengthWithBenvenuto() {
          //TODO cambiare tutte le logiche anche alla infoLoginEntity
        int length = benvenuto.length();
        byte[] lenBenvenutoBytes = new byte[2];
        lenBenvenutoBytes[0] = (byte) ((length >> 8) & 0xFF); // TODO deve essere convertito in big endiamo non deve essere 12 00 ma 00 12
        lenBenvenutoBytes[1] = (byte) (length & 0xFF);

        byte[] benvenutoBytes = benvenuto.getBytes(StandardCharsets.UTF_8);

        return concatBytes(lenBenvenutoBytes,benvenutoBytes);
    }

    public byte[] getLength() {
        int lenBenvenuto = getLengthWithBenvenuto().length; // lunghezza totale

        return new byte[] {
                (byte) ((lenBenvenuto >> 8) & 0xFF),  // byte alto (più significativo)
                (byte) (lenBenvenuto & 0xFF)          // byte basso (meno significativo)
        };
    }

    public byte[] getWelcomeMessage() throws IOException {
        ByteArrayOutputStream wmComposit = new ByteArrayOutputStream();
        wmComposit.write(SERVER_COMMAND);
        wmComposit.write(getCount());
        wmComposit.write(getLength());
        wmComposit.write(getLengthWithBenvenuto());

        byte[] infoLogin = wmComposit.toByteArray();

        return infoLogin;
    }



    public static byte[] concatBytes(byte[] len, byte[] bytes) {
        byte[] result = new byte[len.length + bytes.length];
        System.arraycopy(len, 0, result, 0, len.length);
        System.arraycopy(bytes, 0, result, len.length, bytes.length);
        return result;
    }
}
