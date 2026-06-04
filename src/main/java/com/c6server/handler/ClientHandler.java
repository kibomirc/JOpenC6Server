package com.c6server.handler;

import com.c6server.ClientRegistry;
import com.c6server.c6enum.C6EnumClient;
import com.c6server.dao.NetFriendsDAO;
import com.c6server.dao.UserDAO;
import com.c6server.model.LoginEntity;
import com.c6server.model.MessageRequest;
import com.c6server.packet.*;
import com.c6server.utils.AESUtils;
import com.c6server.utils.PingManagerUtils;
import com.c6server.utils.UtilsProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
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

    public static void handle(Socket socket, Connection conn) throws SQLException, IOException {
        String nickname = null;
        NetFriendsDAO netFriendsDAO = new NetFriendsDAO(conn);
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

                    if (nickname == null) {
                        Thread.sleep(500);
                        break;
                    }

                    ClientRegistry.register(nickname, socket);
                    PingManagerUtils.getInstance().onClientConnected(nickname);

                    handleNewUsers(nickname, conn);
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
                if (cmdClient == C6EnumClient.CLIENT_REQ_EXIT.getCode()) {
                    handleExitUser(nickname, conn);
                }
                if (cmdClient == C6EnumClient.PONG.getCode()) {
                    PingManagerUtils.getInstance().onPongReceived(nickname);
                }
                if (cmdClient == C6EnumClient.REQ_ROOM_LIST.getCode()) {
                    handleReqRoomList(nickname,conn,out);
                }
                if (cmdClient == C6EnumClient.ENTER_ROOM.getCode()) {
                    handleEnterRoom(nickname, conn, out);
                }
                if (cmdClient == C6EnumClient.MESSAGE_ROOM.getCode()) {
                    handleMessageRoom(decoded,nickname, conn, out);
                }
                if (cmdClient == C6EnumClient.EXIT_ROOM.getCode()) {
                    handleExitRoom(nickname, conn, out);
                }
            }

        } catch (IOException | SQLException e) {
            logger.error("Errore connessione client " + nickname + " — " + e.getMessage());
            netFriendsDAO.updateStatus(nickname,false);
            ClientRegistry.unregister(nickname);

            PingManagerUtils.getInstance().onClientDisconnected(nickname);
        } catch (InterruptedException | NoSuchAlgorithmException e) {
            netFriendsDAO.updateStatus(nickname,false);
            ClientRegistry.unregister(nickname);

            PingManagerUtils.getInstance().onClientDisconnected(nickname);
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

        // Check se il nick esiste
        UserDAO userDAO = new UserDAO(conn);

        if (!userDAO.exists(loginEntity.getNick())) {
            LoginNoUserPacket loginNoUserPacket = new LoginNoUserPacket();
            loginNoUserPacket.setCount(0);

            out.write(loginNoUserPacket.getLoginNoUserPacket());
            out.flush();

            logger.warn("L'Utente non esiste: " + loginEntity.getNick());
            return null;
        }

        // check utente già connesso
        if(userDAO.getStatusOnline(loginEntity.getNick())) {
            LoginUserConnPacket loginUserConnPacket = new LoginUserConnPacket();
            loginUserConnPacket.setCount(0);

            out.write(loginUserConnPacket.getLoginUserConnPacket());
            out.flush();

            logger.warn("Utente già connesso! : " + loginEntity.getNick());
            return null;

        }
        // Devo per forza salvare in chiaro la password su db perchè la key cambia e quindi anche la codifica
        boolean passCheck = UtilsProtocol.checkC6Control(key, AESUtils.decrypt(userDAO.getPassword(loginEntity.getNick())), loginEntity.getPassEncoded(), true);
        if (!passCheck) {
            LoginErrorPassPacket loginErrorPassPacket = new LoginErrorPassPacket();
            loginErrorPassPacket.setCount(0);

            out.write(loginErrorPassPacket.getLoginErrorPassPacket());
            out.flush();

            logger.warn("Password sbagliata per: " + loginEntity.getNick());
            return null;
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

    // ------------------------------------------------------------------------
    // NEW_USERS - cancella un netfriend
    // ------------------------------------------------------------------------
    private static void handleNewUsers(String nickname, Connection conn)
            throws IOException, SQLException, NoSuchAlgorithmException {

        // prelevo tutti i netFriend che hanno "nickname" come amico
        NetFriendsDAO netFriendsDAO = new NetFriendsDAO(conn);
        netFriendsDAO.updateStatus(nickname,true);
        List<String> netFriendsToNotify = netFriendsDAO.getNetFriendsToNotify(nickname);

        NewUserPacket newUserPacket = new NewUserPacket();
        newUserPacket.setCount(0);
        newUserPacket.setNickname(nickname);

        // inviare il pacchetto NewUserPacket ai netFriend
        for(String netFriend : netFriendsToNotify) {
            ClientRegistry.sendTo(netFriend, newUserPacket.getNewUserPacket());
        }
    }


    // ------------------------------------------------------------------------
    // EXIT_USERS - cancella un netfriend
    // ------------------------------------------------------------------------
    private static void handleExitUser(String nickname, Connection conn)
            throws IOException, SQLException, NoSuchAlgorithmException {

        // prelevo tutti i netFriend che hanno "nickname" come amico
        NetFriendsDAO netFriendsDAO = new NetFriendsDAO(conn);
        netFriendsDAO.updateStatus(nickname,false);
        List<String> netFriendsToNotify = netFriendsDAO.getNetFriendsToNotify(nickname);

        ExitUserPacket exitUserPacket = new ExitUserPacket();
        exitUserPacket.setCount(0);
        exitUserPacket.setNickname(nickname);

        // inviare il pacchetto NewUserPacket ai netFriend
        for(String netFriend : netFriendsToNotify) {
            ClientRegistry.sendTo(netFriend, exitUserPacket.getExitUserPacket());
        }

        PingManagerUtils.getInstance().onClientDisconnected(nickname);
    }


    // ------------------------------------------------------------------------
    // REQ_ROOM_LIST - preleva dal db le room list da inviare al client
    // ------------------------------------------------------------------------
    private static void handleReqRoomList(String nickname, Connection conn, OutputStream out)
            throws IOException, SQLException, NoSuchAlgorithmException {

        System.out.println("L' Utente sta chiedendo le stanze");

        SendRoomPacket sendRoomPacket = new SendRoomPacket();
        sendRoomPacket.setCount(0);
        sendRoomPacket.addRoom("JOpenC6Server",100);
        sendRoomPacket.addRoom("Amici",10);
        sendRoomPacket.addNetFriendRoom("stanza privata!",4);
        out.write(sendRoomPacket.getSendRoom());
        out.flush();

    }

    // ------------------------------------------------------------------------
    // ENTER_ROOM - preleva dal db le room list da inviare al client
    // ------------------------------------------------------------------------
    private static void handleEnterRoom(String nickname, Connection conn, OutputStream out)
            throws IOException, SQLException, NoSuchAlgorithmException {

        System.out.println("L' Utente vuole entrare in una stanza");

        // devo estrapolare il nome della stanza
        // simuliamo che entro on JOpenC6Server

        EnterRoomPacket enterRoomPacket = new EnterRoomPacket();
        enterRoomPacket.setCount(0);
        enterRoomPacket.addRoom("JOpenC6Server",1,List.of("bigalex"));

        // notifica utente chat
        NotifyEnterRoomPacket notifyEnterRoomPacket = new NotifyEnterRoomPacket();
        notifyEnterRoomPacket.setRoom("JOpenC6Server");
        notifyEnterRoomPacket.setCount(0);
        notifyEnterRoomPacket.setNickname("ivan");


        out.write(enterRoomPacket.getEnterRoomPacket());
        out.flush();

        ClientRegistry.sendTo("bigalex", notifyEnterRoomPacket.getNotifyRoomPacket());

    }


    // -------------------------------------------------------------------------
    // MESSAGE_ROOM — intercetta i messaggi inviati in room
    // -------------------------------------------------------------------------

    private static void handleMessageRoom(byte[] decoded,String nickname, Connection conn, OutputStream out)
            throws IOException, SQLException, NoSuchAlgorithmException {

        System.out.println("L utente " + nickname + " ha inviato un messaggio nella stanza!");

        //TODO CABLIAMO IL MESSAGGIO IN STANZA E INDIRIZIAMOLO A BIGALEX

        MessageRoomPacket messageRoomPacket = new MessageRoomPacket();
        messageRoomPacket.setCount(0);
        messageRoomPacket.setNicknameMittente("ivan");
        messageRoomPacket.setRoomDestinazione("JOpenC6Server");
        messageRoomPacket.setStile(new byte[]{ 0x00, 0x02 });
        messageRoomPacket.setMessaggio("Funzionaaaaa");

        ClientRegistry.sendTo("bigalex", messageRoomPacket.getMessageRoomPacket());
    }

    // ------------------------------------------------------------------------
    // EXIT_ROOM - Gestione
    // ------------------------------------------------------------------------
    private static void handleExitRoom(String nickname, Connection conn, OutputStream out)
            throws IOException, SQLException, NoSuchAlgorithmException {

        System.out.println("L' Utente è uscito dalla stanza");

        NotifyExitRoomPacket notifyExitRoomPacket = new NotifyExitRoomPacket();
        notifyExitRoomPacket.setCount(0);
        notifyExitRoomPacket.setRoom("JOpenC6Server");
        notifyExitRoomPacket.setNickname("ivan");

        ClientRegistry.sendTo("bigalex", notifyExitRoomPacket.getNotifyRoomPacket());

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
