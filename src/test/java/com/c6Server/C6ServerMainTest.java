package com.c6Server;

import com.c6server.C6ServerMain;
import com.c6server.entity.InfoLoginEntity;
import com.c6server.entity.LoginEntity;
import com.c6server.entity.WelcomeEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class C6ServerMainTest
{
    @Test
    void reorderKeyTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // dati di esempio
        //byte[] key = { 0x64, 0x63, 0x66, 0x45, 0x51 , 0x59, 0x63, 0x4A };
        byte[] key = {0x02, 0x74, 0x03, 0x57,0x5B, 0x3A, 0x2D, 0x20 };
        String data = "C6";

        // ottieni il metodo privato statico
        Method method = C6ServerMain.class.getDeclaredMethod("reorderKey", byte[].class);
        method.setAccessible(true); // disabilita i controlli di accesso

        // invoca il metodo statico (null come oggetto perché è static)
        byte reorderKey = (byte) method.invoke(null, key);

    }

    @Test
    void decodePacketTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
/*
        byte[] data = { 0x10, 0x0F, 0x00, 0x01, 0x00, 0x35,
                        0x6A, 0x4B, 0x51, 0x04, 0x3E, 0x0F, 0x4C,
                        0x13, 0x3C, 0x30, 0x6B, 0x2E, 0x0C, 0x2B,
                        0x3C, 0x2A, 0x0F, 0x39, 0x4E, 0x66, 0x03,
                        0x28, 0x00, 0x1E, 0x45, 0x5A, 0x1F, 0x05,
                        0x6A, 0x00, 0x6C, 0x59, 0x75, 0x68, 0x7E,
                        0x1A, 0x2B, 0x2B, 0x59, 0x1A, 0x50, 0x0A,
                        0x54, 0x36, 0x09, 0x04, 0x14, 0x5F, 0x48,
                        0x7A, 0x4B, 0x51, 0x16 };

        byte[] orderKey = { 0x7A, 0x4A, 0x51, 0x05, 0x3E, 0x20, 0x48, 0x7B };
*/

        byte[] data = {
                0x10, 0x0F, 0x00, 0x01, 0x00, 0x35, 0x12, 0x02,
                0x5B, 0x2C, 0x74, 0x78, 0x3E, 0x6B, 0x75, 0x3A,
                0x43, 0x64, 0x15, 0x1C, 0x62, 0x62, 0x23, 0x06,
                0x3E, 0x21, 0x0F, 0x6B, 0x6E, 0x0F, 0x67, 0x59,
                0x7B, 0x0E, 0x12, 0x31, 0x17, 0x1D, 0x1F, 0x17,
                0x10, 0x52, 0x7B, 0x6F, 0x7F, 0x38, 0x1A, 0x78,
                0x2A, 0x61, 0x0E, 0x2C, 0x5E, 0x28, 0x3A, 0x02,
                0x02, 0x5B, 0x3E };


        byte[] orderKey = { 0x02, 0x03, 0x5B, 0x2D,
                0x74, 0x57, 0x3A, 0x20 };


        Method method = C6ServerMain.class.getDeclaredMethod("decodePacket", byte[].class ,byte[].class);
        method.setAccessible(true); // disabilita i controlli di accesso



        byte[] decodePacket = (byte[]) method.invoke(null, data, orderKey);
    }


    @Test
    void logindData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        byte[] dataDecode = {
                0x10, 0x0F, 0x00, 0x01, 0x00, 0x35,
                0x10, 0x01, 0x00, 0x01, 0x00, 0x2F,
                0x04, 0x69, 0x76, 0x61, 0x6E, 0x10,
                0x42, 0x26, 0x60, 0x61, 0x78, 0x2B,
                0x4A, 0x76, 0x35, 0x69, 0x6D, 0x54,
                0x4A, 0x2D, 0x2C, 0x34, 0x10, 0x32,
                0x4C, 0x30, 0x6B, 0x40, 0x2A, 0x50,
                0x78, 0x34, 0x52, 0x4C, 0x4D, 0x42,
                0x28, 0x62, 0x55, 0x01, 0x2A, 0x7F,
                0x00, 0x00, 0x01, 0x00, 0x13
        };

        byte[] key = {0x02, 0x74, 0x03, 0x57, 0x5B, 0x3A, 0x2D, 0x20};

        // Recupera il metodo loginData e invocalo
        Method loginMethod = C6ServerMain.class.getDeclaredMethod("loginData", byte[].class);
        loginMethod.setAccessible(true);
        LoginEntity loginEntity = (LoginEntity) loginMethod.invoke(null, dataDecode);

        String nickname = "ivan".trim();
        String password = "ciao".trim(); // non ricordo la password che avevo messo per il test

        // Recupera il metodo checkC6Control e invocalo
        Method checkC6Control = C6ServerMain.class.getDeclaredMethod(
                "checkC6Control",
                byte[].class, String.class, byte[].class, boolean.class
        );
        checkC6Control.setAccessible(true);

        // Controllo sul nickname
        boolean nickCheck = (boolean) checkC6Control.invoke(null, key, nickname, loginEntity.getNickEncoded(), false);
        System.out.println("NICK ENCODED CHECK: " + nickCheck);

        // Controllo sulla password
        boolean passCheck = (boolean) checkC6Control.invoke(null, key, password, loginEntity.getPassEncoded(), true);
        System.out.println("PASSWORD ENCODED CHECK: " + passCheck);
    }



    @Test
    void testMD5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // ivan in MD5 2c42e5cf1cdbafea04ed267018ef1511
        Method generKey = C6ServerMain.class.getDeclaredMethod("generKey", String.class);
        generKey.setAccessible(true);

        StringBuilder md5 = (StringBuilder) generKey.invoke(null,"ivan");

        System.out.println(md5);
    }


    @Test
    void testInfoLogin() throws IOException {
         InfoLoginEntity infoLoginEntity = new InfoLoginEntity();
         infoLoginEntity.setCount(2);
         //numBanner attualmente solo uno
        infoLoginEntity.setGif("/Users/ivan/Desktop/dragon-ball-dragon-ball-z.gif");
        infoLoginEntity.setLinkBanner("https://www.google.it");
        infoLoginEntity.setNome("JC6");
        infoLoginEntity.setId("1");
        infoLoginEntity.setLinkButton("https://www.google.it");
        infoLoginEntity.setDescr("JC6Server");

        byte[] infoLoginCmd = infoLoginEntity.getInfoLogin();

        System.out.println("INFOLOGIN:");
        for (byte b : infoLoginCmd) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }

    @Test
    void testWelcomeMessage() throws IOException {
        WelcomeEntity welcomeEntity = new WelcomeEntity();
        welcomeEntity.setCount(3);
        welcomeEntity.setBenvenuto("Welcome Message!");

        byte[] welcomeMessageCmd = welcomeEntity.getWelcomeMessage();

        System.out.println("Welcome Message:");
        for (byte b : welcomeMessageCmd) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }

}
