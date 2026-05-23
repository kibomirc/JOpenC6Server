package com.c6server.handler;

import com.c6server.ClientRegistry;
import com.c6server.c6enum.C6EnumClient;
import com.c6server.dao.NetFriendsDAO;
import com.c6server.dao.UserDAO;
import com.c6server.model.LoginEntity;
import com.c6server.model.MessageRequest;
import com.c6server.packet.*;
import com.c6server.utils.UtilsProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ClientHandler {

    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    // -------------------------------------------------------------------------
    // Entry point — chiamato dal threadPool in C6ServerMain
    // -------------------------------------------------------------------------

    public static void handle(Socket socket, Connection conn) {
        String nickname = null;
        System.out.println("Nuova connessione da: " + socket.getInetAddress() + ":" + socket.getPort());

        try (Socket s = socket;
             InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream()) {

            byte[] key = sendHelo(out);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] data      = Arrays.copyOf(buffer, bytesRead);
                byte[] orderKey  = UtilsProtocol.reorderKey(key);
                byte[] decoded   = UtilsProtocol.decodePacket(data, orderKey);
                byte   cmdClient = UtilsProtocol.extractCmdClient(decoded);

                if (cmdClient == C6EnumClient.LOGIN.getCode()) {
                    nickname = handleLogin(decoded, key, out, conn);
                    ClientRegistry.register(nickname, out);
                }
                if (cmdClient == C6EnumClient.REQ_PULS.getCode()) {
                    handleReqPuls(decoded, out);
                }
                if (cmdClient == C6EnumClient.REQ_USERS.getCode()) {
                    handleReqUsers(decoded, out, nickname, conn);
                }
                if (cmdClient == C6EnumClient.OL_MESSAGE.getCode()) {
                    handleOLMessage(decoded,out);
                }
                if (cmdClient == C6EnumClient.DEL_USERS.getCode()) {
                    handleDelUsers(decoded, out, nickname, conn);
                }
            }

        } catch (IOException | SQLException e) {
            logger.error("Errore connessione client " + nickname + " — " + e.getMessage());
            ClientRegistry.unregister(nickname);
        } catch (NoSuchAlgorithmException e) {
            ClientRegistry.unregister(nickname);
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // Handshake iniziale — invia HELO + key al client, restituisce la key
    // -------------------------------------------------------------------------

    private static byte[] sendHelo(OutputStream out)
            throws IOException, NoSuchAlgorithmException {

        byte[] heloProtocol = {0x20, 0x12, 0x00, 0x01, 0x00, 0x0B, 0x00, 0x02, 0x08};
        byte[] key = UtilsProtocol.codKey();

        byte[] heloMessage = new byte[heloProtocol.length + key.length];
        System.arraycopy(heloProtocol, 0, heloMessage, 0, heloProtocol.length);
        System.arraycopy(key, 0, heloMessage, heloProtocol.length, key.length);

        out.write(heloMessage);
        out.flush();
        return key;
    }

    // -------------------------------------------------------------------------
    // LOGIN — restituisce il nickname autenticato
    // -------------------------------------------------------------------------

    private static String handleLogin(byte[] decoded, byte[] key, OutputStream out, Connection conn)
            throws IOException, SQLException, NoSuchAlgorithmException {

        logger.info("Il client sta effettuando il login");

        LoginEntity loginEntity = UtilsProtocol.loginData(decoded);

        boolean nickCheck = UtilsProtocol.checkC6Control(key, loginEntity.getNick(), loginEntity.getNickEncoded(), false);
        if (!nickCheck) {
            logger.warn("Spoofing rilevato, connessione chiusa.");
            throw new IOException("Spoofing rilevato");
        }

        // TODO: sostituire "password" con lettura da SQLite quando implementata la registrazione
        boolean passCheck = UtilsProtocol.checkC6Control(key, "password", loginEntity.getPassEncoded(), true);
        if (!passCheck) {
            logger.warn("Password sbagliata per: " + loginEntity.getNick());
            throw new IOException("Password sbagliata");
        }

        UserDAO userDAO = new UserDAO(conn);
        if (!userDAO.exists(loginEntity.getNick())) {
            // TODO: inviare messaggio di errore
            throw new IOException("Utente non esiste: " + loginEntity.getNick());
        }

        logger.info("Login riuscito per: " + loginEntity.getNick());

        out.write(buildInfoLoginResponse());
        out.flush();

        return loginEntity.getNick();
    }

    private static byte[] buildInfoLoginResponse() throws IOException {
        InfoLoginPacket infoLoginEntity = new InfoLoginPacket();
        infoLoginEntity.setCount(2);
        infoLoginEntity.setGif("http://localhost:80/images/banner1.gif");
        infoLoginEntity.setLinkBanner("http://localhost:80/welcome");
        infoLoginEntity.setNome("Banner 123");
        infoLoginEntity.setId("1");
        infoLoginEntity.setLinkButton("https://www.c6online.it");
        infoLoginEntity.setDescr("JC6Server");

        WelcomeEntityPacket welcomeEntityPacket = new WelcomeEntityPacket();
        welcomeEntityPacket.setCount(3);
        welcomeEntityPacket.setBenvenuto("Benvenuto. In italia sono ... non importa, goditi il momento!");

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(infoLoginEntity.getInfoLogin());
        buf.write(welcomeEntityPacket.getWelcomeMessage());
        return buf.toByteArray();
    }

    // -------------------------------------------------------------------------
    // REQ_PULS — invia i pulsanti; gestisce anche il caso wine (REQ_USERS accoppiato)
    // -------------------------------------------------------------------------

    private static void handleReqPuls(byte[] decoded, OutputStream out)
            throws IOException, NoSuchAlgorithmException {

        SendPulsPacket sendPulsPacket = new SendPulsPacket();
        sendPulsPacket.setCount(4);
        sendPulsPacket.setNumPuls(5);
        sendPulsPacket.addButton("JOpenC6 Server", "https://www.jopenc6.it");
        sendPulsPacket.addButton("OpenC6",  "https://web.archive.org/web/20040722065013/http://openc6.extracon.it/index.php");
        sendPulsPacket.addButton("Icona",   "https://www.icona.it/");
        sendPulsPacket.addButton("Alice",   "https://www.tim.it/");
        sendPulsPacket.addButton("Virgilio","https://www.virgilio.it/");

        out.write(sendPulsPacket.getSndPuls());
        out.flush();
        logger.debug("SND_PULS inviato");

        // quando sto in debug la REQ_USERS viene inviata insieme alla REQ_PULS
        if (C6EnumClient.REQ_USERS.getCode() == UtilsProtocol.extractCmdReqUserOnLogin(decoded)) {
            logger.debug("REQ_USERS accoppiato a REQ_PULS (casistica debug)");
            List<String> netFriends = UtilsProtocol.getReqUsersOnLogin(decoded);
            // TODO: sostituire con check reale su DB
            List<String> netFriendsOnline = List.of("nick"); // lista mockkata
            sendOnlineUsers(netFriendsOnline, 5, out);
        }
    }

    // -------------------------------------------------------------------------
    // REQ_USERS — salva la lista amici e risponde con gli utenti online
    // -------------------------------------------------------------------------

    private static void handleReqUsers(byte[] decoded, OutputStream out, String nickname, Connection conn)
            throws IOException, SQLException, NoSuchAlgorithmException {

        List<String> netFriends = UtilsProtocol.getReqUsers(decoded);
        logger.debug("REQ_USERS ricevuto, netFriends: " + netFriends);

        NetFriendsDAO netFriendsDAO = new NetFriendsDAO(conn);
        netFriendsDAO.addNetFriends(nickname,netFriends);

        List<String> netFriendsOnline = netFriendsDAO.getNetFriendsOnline(nickname);
        sendOnlineUsers(netFriendsOnline, 0, out);
    }


    //--------------------------------------------------------------------------
    // OL_MESSAGE - invia messaggio privato utente
    //--------------------------------------------------------------------------

    private static void handleOLMessage(byte[] decoded, OutputStream out) throws IOException {

        MessageRequest messageRequest = UtilsProtocol.parseMessage(decoded);
        SrvMessagePacket srvMessagePacket = new SrvMessagePacket();

        srvMessagePacket.setCount(6);
        srvMessagePacket.setNickMittente(messageRequest.getNickMittente());
        srvMessagePacket.setNickDestinatario(messageRequest.getNickDestinatario());
        srvMessagePacket.setStile(messageRequest.getStile());
        srvMessagePacket.setMessaggio(messageRequest.getMessaggio());

        ClientRegistry.sendTo(messageRequest.getNickDestinatario(), srvMessagePacket.getSrvMessagePacket());
    }

    // ------------------------------------------------------------------------
    // DEL_USERS - cancella un netfriend
    // ------------------------------------------------------------------------
    private static void handleDelUsers(byte[] decoded, OutputStream out, String nickname, Connection conn)
            throws IOException, SQLException, NoSuchAlgorithmException {

        List<String> netFriends = UtilsProtocol.getReqUsers(decoded);
        logger.debug("REQ_DEL ricevuto, netFriends: " + netFriends);

        NetFriendsDAO netFriendsDAO = new NetFriendsDAO(conn);
        netFriendsDAO.deleteNetFriends(nickname,netFriends);
    }

    // -------------------------------------------------------------------------
    // costruisce e invia SND_USERS
    // -------------------------------------------------------------------------

    public static void sendOnlineUsers(List<String> onlineNicks, int count, OutputStream out)
            throws IOException, NoSuchAlgorithmException {

        SendUsersPacket sendUsersPacket = new SendUsersPacket();
        sendUsersPacket.setCount(count);
        for (String nick : onlineNicks) {
            sendUsersPacket.addNetFriend(nick);
        }
        out.write(sendUsersPacket.getSndUsers());
        out.flush();
    }
}
