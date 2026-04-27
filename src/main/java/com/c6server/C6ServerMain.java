package com.c6server;

import com.c6server.c6enum.C6EnumClient;
import com.c6server.c6enum.C6EnumServer;
import com.c6server.entity.InfoLoginEntity;
import com.c6server.entity.LoginEntity;
import com.c6server.entity.WelcomeEntity;
import com.c6server.utils.HttpServerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class C6ServerMain {
    private static final Logger logger = LogManager.getLogger(C6ServerMain.class);

    public static void main(String[] args) throws IOException {

        // server http per infoLogin
        new Thread(() -> { // lo metto in un thread separato per buona pratica
            try {
                HttpServerUtils.startServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        /*
            Mettiamoci in ascolto sulla 4800 tcp
         */

        new Thread(() -> { // thread esterno solo per sviluppi futuri
            try (ServerSocket serverSocket = new ServerSocket(4800)) {
                while (true) {
                    Socket c6socket = serverSocket.accept();
                    new Thread(() -> { // gestione più client
                        try (Socket s = c6socket;
                             InputStream in = s.getInputStream();
                             OutputStream out = s.getOutputStream()) {

                            byte[] heloProtocol = {
                                    0x20, 0x12, 0x00, 0x01, 0x00, 0x0B, 0x00, 0x02, 0x08
                            };
                            byte[] key = codKey(); // key da inviare al client
                            byte[] heloMessage = new byte[heloProtocol.length + key.length];

                            System.arraycopy(heloProtocol, 0, heloMessage, 0, heloProtocol.length);
                            System.arraycopy(key, 0, heloMessage, heloProtocol.length, key.length);

                            out.write(heloMessage);
                            out.flush();

                            byte[] buffer = new byte[1024];
                            int bytesRead = 0;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                byte[] data = Arrays.copyOf(buffer, bytesRead);
                                // Generare la oroderKey
                                byte[] orderKey = reorderKey(key);
                                // Decodificare i dati
                                byte[] decodePacket = decodePacket(data, orderKey);
                                byte cmdClient = extractCmdClient(decodePacket);
                                if (cmdClient == C6EnumClient.LOGIN.getCode()) {
                                    System.out.println("Il client sta effettuando il login");
                                    // TODO IMPLEMENTARE LOGICHE CHECK
                                    // Passare i dati decodificati alla loginData prelevando l' oggetto loginEntity
                                    LoginEntity loginEntity = new LoginEntity();
                                    loginEntity = loginData(decodePacket);
                                    // Codificare il nickname e confrontarlo con quello della loginData
                                    boolean nickCheck = checkC6Control(key, loginEntity.getNick(), loginEntity.getNickEncoded(), false);
                                    // Codificare la password (memorizzata in futuro su redis) e confrotnarla con quella della logindata
                                    // per il momento l'unica password accettata è "password" bisogna implementare un meccanismo di registrazione
                                    // e di prelievo della password
                                    boolean passCheck = checkC6Control(key, "password", loginEntity.getPassEncoded(), true);
                                    // Se i controlli sono entrambi true andare avanti (poi dovrò gestire l errore della password sbagliata o dell'utente già connesso.
                                    if (!nickCheck) {
                                        System.out.println("Lo spooffing non è consetito su questo server!!! Verrai segnalato");
                                        return;
                                    }
                                    if (!passCheck) {
                                        System.out.println("Password Sbagliata!");
                                    } else {
                                        System.out.println("Password corretta! ... procediamo con autenticazione");
                                    }

                                    // TODO FARE CHCEK INFOLOGIN

                                    InfoLoginEntity infoLoginEntity = new InfoLoginEntity();
                                    infoLoginEntity.setCount(2);
                                    //numBanner attualmente solo uno


                                    //Gif
                                    infoLoginEntity.setGif("https://static.c6online.it/banner/advert-00.gif?t=1777228815");

                                    //Link
                                    infoLoginEntity.setLinkBanner("https://www.c6online.it");
                                    infoLoginEntity.setNome("Banner 123");

                                    //Button
                                    infoLoginEntity.setId("1");
                                    infoLoginEntity.setLinkButton("https://www.c6online.it");
                                    infoLoginEntity.setDescr("JC6Server");

                                    byte[] infoLoginCmd = infoLoginEntity.getInfoLogin();

                                    //out.write(infoLoginCmd); // invio infoLogin al server
                                    //out.flush();
                                    System.out.println();

                                    //TODO FARE CHECK SU WELCOME MESSAGE
                                    WelcomeEntity welcomeEntity = new WelcomeEntity();
                                    welcomeEntity.setCount(3);
                                    welcomeEntity.setBenvenuto("Benvenuto. In italia sono ... É l'ora della nostaliga!");

                                    byte[] welcomeMessageCmd = welcomeEntity.getWelcomeMessage();
                                    //out.write(infoLoginCmd); // invio infoLogin al server
                                    //out.flush();

                                    ByteArrayOutputStream infoLoginAndMotd = new ByteArrayOutputStream();
                                    infoLoginAndMotd.write(infoLoginCmd);
                                    infoLoginAndMotd.write(welcomeMessageCmd);

                                    out.write(infoLoginAndMotd.toByteArray());

                                    System.out.println("INFOLOGIN:");
                                    for (byte b : infoLoginCmd) {
                                        System.out.printf("%02X ", b);
                                    }

                                    System.out.println();


                                    System.out.println("Welcome Message:");
                                    for (byte b : welcomeMessageCmd) {
                                        System.out.printf("%02X ", b);
                                    }

                                }
                                if (cmdClient != C6EnumClient.LOGIN.getCode()) {
                                    System.out.println("STANNO ARRIVANODI I DATI REQ_PLUS FUNZIONA!!!");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    // TODO mettere tutto in una classe a parte
    // Stampa nickname
    // Stampa nickname codificato
    // Stampa password codificata
    private static LoginEntity loginData(byte[] data) {
        // Devo andare all undicesimo byte e prelevare la lunghezza
        // e poi devo prelevare dal dodicesimo byte più la lunghezza

        int posNickLen = 12;
        byte nickLength = data[posNickLen];
        byte[] nick = readNBytesFromIndex(data, posNickLen, nickLength);

        int posPassLen = posNickLen + nickLength + 1;
        byte passLength = data[posPassLen];
        byte[] passEncode = readNBytesFromIndex(data, posPassLen, passLength);

        int posNickLen2 = posPassLen + passLength + 1;
        byte nickLength2 = data[posNickLen2];
        byte[] nickEncode = readNBytesFromIndex(data, posNickLen2, nickLength2);

        String nickAscii = new String(nick, StandardCharsets.US_ASCII);

        System.out.println("NICKNAME: " + nickAscii);

        System.out.print("PASSWORD ENCODED: ");
        for (byte b : passEncode) {
            System.out.printf("%02X ", b);
        }

        System.out.println();

        System.out.print("NICK ENCODED: ");
        for (byte b : nickEncode) {
            System.out.printf("%02X ", b);
        }

        System.out.println();

        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setNick(nickAscii);
        loginEntity.setPassEncoded(passEncode);
        loginEntity.setNickEncoded(nickEncode);

        return loginEntity;
    }

    // TODO Algoritmo codifica password e nickname
    // Codifica una stringa usando una chiave server e un flag Psw
    public static void GenerKey(byte[] pDest, String pSorg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(pSorg.getBytes(StandardCharsets.ISO_8859_1));
        System.arraycopy(digest, 0, pDest, 0, 16);
    }

    public static byte[] codecC6(String strCode, byte[] strKeyServer, byte[] strDest, boolean psw) throws
            NoSuchAlgorithmException {
        StringBuilder tt = new StringBuilder();

        if (psw) {
            tt.append(strCode);
            tt.append((char) (strKeyServer[0] & 0xFF));
            tt.append((char) (strKeyServer[2] & 0xFF));
        } else {
            tt.append("ANOCI");
            tt.append(Character.toLowerCase(strCode.charAt(strCode.length() - 1)));
            tt.append(Character.toLowerCase(strCode.charAt(0)));
            tt.append((char) (strKeyServer[4] & 0xFF));
            tt.append((char) (strKeyServer[2] & 0xFF));
        }

        // Genera la chiave MD5 da tt
        byte[] rawKey = new byte[16];
        GenerKey(rawKey, tt.toString());

        for (int i = 0; i < 16; i++) {
            int temp = rawKey[i] & 0xFF;
            temp = temp % 0x5E; // modulo 94
            temp += 0x20;       // somma 0x20 (ASCII 32)
            strDest[i] = (byte) temp;
        }

        return strDest;
    }

    public static boolean checkC6Control(byte[] orderKey, String stringEncode, byte[] compareDecodeValue,
                                         boolean psw) throws NoSuchAlgorithmException {
        byte[] hash = new byte[16];
        codecC6(stringEncode, orderKey, hash, psw);
        return Arrays.equals(hash, compareDecodeValue);
    }

    public static byte[] readNBytesFromIndex(byte[] data, int startIndex, int n) {
        if (startIndex < 0 || startIndex >= data.length) {
            throw new IllegalArgumentException("Indice di partenza non valido");
        }
        if (n < 0 || startIndex + n > data.length) {
            throw new IllegalArgumentException("Numero di byte da leggere non valido");
        }
        byte[] result = new byte[n];
        System.arraycopy(data, startIndex + 1, result, 0, n);
        return result;
    }

    private static byte[] codKey() {
        // azzero codifica
        byte[] randomBytes = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        for (int i = 0; i < randomBytes.length; i++) {
            randomBytes[i] = (byte) (randomBytes[i] & 0x7F);
        }

        return randomBytes;
    }

    private static byte extractCmdClient(byte[] data) {
        if (data.length < 8) {
            throw new IllegalArgumentException("Pacchetto troppo corto: servono almeno 8 byte");
        }

        return data[7]; // ottavo byte
    }

    private static byte[] reorderKey(byte[] key) {
        /*
         Prendo la chiava inviata al client e la riordino
         una volta riordinata dovrei anche salvara da qualche parte per utilizza futuri
         prevedo di usare redis
         */

        // 1 riordino chiave

        byte[] orderKey = new byte[8];

        // prelevo il 6 byte e si fa il mod 5
        int step = key[5] % 7;
        orderKey[0] = key[0];

        int start = 1;
        for (int i = 1; i < key.length - 1; i++) {
            int selectElement = (start + step);

            if (selectElement <= 7) {
                orderKey[i] = key[selectElement - 1];
                start = selectElement;
            } else {
                selectElement = selectElement % 7;
                orderKey[i] = key[selectElement - 1];
                start = selectElement;
            }
        }

        orderKey[7] = key[7];

        return orderKey;
    }

    private static byte[] decodePacket(byte[] data, byte[] orderKey) {

        // Devo suddividere i byte in 7 byte e fare uno xor con la orderKey
        // La decodifica su applica dal 7 byte in poi.

        // preleviamo da data dal 7 byte in poi
        byte[] bytesDecode = Arrays.copyOfRange(data, 0, 6);
        byte[] bytesEncoded = Arrays.copyOfRange(data, 6, data.length);
        List<byte[]> encodeBlock = new ArrayList<>();
        List<Byte> decodeBlock = new ArrayList<>();

        // per ogni blocco devo fare un xor con la orderKey -1
        byte[] xorKey = new byte[7];
        System.arraycopy(orderKey, 0, xorKey, 0, 7);

        // Divido i byteEncoded in gruppi da 7 byte
        for (int i = 0; i < bytesEncoded.length; i += 7) {
            int end = Math.min(i + 7, bytesEncoded.length);
            int length = end - i;
            byte[] block = new byte[length];
            System.arraycopy(bytesEncoded, i, block, 0, length);

            // stiamo blocco encodato
            System.out.println();

            encodeBlock.add(block);
        }

        for (byte[] block : encodeBlock) {
            for (int i = 0; i < block.length; i++) {
                byte[] xorBlock = new byte[7];
                xorBlock[i] = (byte) ((block[i] & 0xFF) ^ (xorKey[i] & 0xFF)); // XOR tra i valori unsigned

                // Stampiamo il risultato
                System.out.printf("block[%d] = 0x%02X, xorKey[%d] = 0x%02X, result = 0x%02X%n",
                        i, block[i], i, xorKey[i], xorBlock[i]);

                decodeBlock.add(xorBlock[i]);

            }
        }


        for (int i = bytesDecode.length - 1; i >= 0; i--) {
            decodeBlock.add(0, bytesDecode[i]); // Aggiunge all'inizio della lista
        }

        // Stampa il risultato in esadecimale

        byte[] decodeBlockByte = new byte[decodeBlock.size()];
        System.out.println("Risultato XOR:");
        for (int i = 0; i < decodeBlock.size(); i++) {
            byte b = decodeBlock.get(i);
            decodeBlockByte[i] = b;
            System.out.printf("%02X ", b);
        }


        return decodeBlockByte;
    }
}
