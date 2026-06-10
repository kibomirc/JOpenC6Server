package com.c6server.packet;

import com.c6server.c6enum.C6EnumRoomPreferences;
import com.c6server.c6enum.C6EnumUserProfilePreferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.c6server.packet.InfoLoginPacket.concatBytes;

public class ProfileUserPacket {

    private final byte[] SERVER_COMMAND = new byte[] { 0x20, 0x14 };
    private final byte[] UNKNOW_BYTE_1 = new byte[] { 0x00, 0x00, 0x00, 0x00 };
    private final byte[] UNKNOW_BYTE_2 = new byte[] { 0x20, 0x00 };
    private final byte[] NULL_PREFERENCES = new byte[] { 0x00 };

    private Integer count;
    private String nickname;
    private long epochSeconds;
    private List<UserPreference> preferences = new ArrayList<>();

    /*
                   20 14        → SERVER_COMMAND
                   00 00        → count
                   00 10        → length totale (16 byte)
                   04           → lunghezza nickname (4 byte)
                   69 76 61 6E  → "ivan"
                   6A 28 24 F0  → timestamp (09 giugno 2026 16:36)
                   00 00 00 00  → UNKNOW_BYTE
                   20 00        → header blocco preferenze
                   00           → numero preferenze (0)
     */




    /*
                    20 14        → SERVER_COMMAND
                    00 00        → count
                    00 3A        → length totale (58 byte)
                    06           → lunghezza nickname (6 byte)
                    64 61 78 77 65 62 → "daxweb"
                    6A 27 AF EB  → timestamp (09 giugno 2026 17:45)
                    00 00 00 00  → UNKNOW_BYTE
                    20 00        → header blocco preferenze
                    14           → numero preferenze (20)
                    01 06        → ETA          → ETA_36_45
                    02 02        → GENERE       → MASCHILE
                    03 02        → ORIENTAMENTO → ORIENTAMENTO_ETERO
                    04 03        → OCCUPAZIONE  → OCCUPAZIONE_ANALISTA_PROGRAMMATORE
                    05 02        → AREA_GEO     → ITALIA
                    0D 0B        → REGIONE      → REGIONE_MARCHE
                    06 47        → PROVINCIA    → PROVINCIA_MACERATA
                    07 07        → HOBBY        → HOBBY_COMPUTER
                    07 05        → HOBBY        → HOBBY_CINEMA
                    07 13        → HOBBY        → HOBBY_MUSICA_SUONARLA
                    08 08        → SPORT        → SPORT_BODY_BUILDING
                    09 18        → MUSICA       → GENERE_MUSICALE_ROCK_ALTERNATIVO
                    09 07        → MUSICA       → GENERE_MUSICALE_HARD_ROCK_HEAVY_METAL
                    09 12        → MUSICA       → GENERE_MUSICALE_ALTRO
                    0A 06        → CINEMA       → GENERE_CINEMATOGRAFICO_CLASSICI_BIANCO_E_NERO
                    0A 07        → CINEMA       → GENERE_CINEMATOGRAFICO_COMMEDIE
                    0A 0B        → CINEMA       → GENERE_CINEMATOGRAFICO_GIALLI
                    0C 06        → ODI          → ODI_CORDIALI_CALZINI_BIANCHI_CORTI
                    0C 03        → ODI          → ODI_CORDIALI_GLI_ADII
                    0C 05        → ODI          → ODI_CORDIALI_CALCIO
     */

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEpochSeconds(long epochSeconds) {
        this.epochSeconds = epochSeconds;
    }

    public String getNickname() {
        return nickname;
    }

    public byte[] getPreferences() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // contatore su 1 byte
        out.write(preferences.size() & 0xFF);

        // per ogni preferenza: indice + valore
        for (UserPreference pref : preferences) {
            out.write(pref.index);
            out.write(pref.value);
        }

        return out.toByteArray();
    }

    public void addPreference(C6EnumUserProfilePreferences pref) {
        preferences.add(new UserPreference(pref.getIndex(), pref.getVal()));
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

    public byte[] getEpochToBytes() {
        long epochOffset = 82800L;
        long epochTime = epochSeconds + epochOffset;

        return new byte[] {
                (byte) ((epochTime >> 24) & 0xFF),
                (byte) ((epochTime >> 16) & 0xFF),
                (byte) ((epochTime >> 8)  & 0xFF),
                (byte) (epochTime         & 0xFF)
        };
    }

    public byte[] getLengthWithNickname() {
        byte[] nicknameBytes = nickname.getBytes(StandardCharsets.ISO_8859_1);
        byte lenNicknameByte = (byte) (nicknameBytes.length & 0xFF);
        return concatBytes(new byte[]{lenNicknameByte}, nicknameBytes);
    }

    public byte[] getLength() {

        int lenRoomName = getLengthWithNickname().length;
        int lenTimestamp = 4;
        int lenUnknowByte1 = UNKNOW_BYTE_1.length;
        int lenUnknowByte2 = UNKNOW_BYTE_2.length;


        int lenPreferencesByte = 0;

        if(this.getPreferences().length > 0) {
            lenPreferencesByte = getPreferences().length;
        } else {
            lenPreferencesByte = NULL_PREFERENCES.length;
        }

        int totalLen = lenRoomName + lenTimestamp + lenUnknowByte1 + lenUnknowByte2 + lenPreferencesByte;

        byte[] totalLenBytes = new byte[2];
        totalLenBytes[0] = (byte) ((totalLen >> 8) & 0xFF);
        totalLenBytes[1] = (byte) (totalLen & 0xFF);

        return totalLenBytes;
    }


    public byte[] getProfileUserPacket() throws IOException {
        ByteArrayOutputStream profileUserPacketComposit = new ByteArrayOutputStream();

        profileUserPacketComposit.write(SERVER_COMMAND);
        profileUserPacketComposit.write(getCount());
        profileUserPacketComposit.write(getLength());
        profileUserPacketComposit.write(getLengthWithNickname());
        profileUserPacketComposit.write(getEpochToBytes());
        profileUserPacketComposit.write(UNKNOW_BYTE_1);
        profileUserPacketComposit.write(UNKNOW_BYTE_2);

        if(this.getPreferences().length > 0) {
            profileUserPacketComposit.write(getPreferences());
        } else {
            profileUserPacketComposit.write(NULL_PREFERENCES);
        }

        byte[] profileRoomPacket = profileUserPacketComposit.toByteArray();

        System.out.println("LOG PACCHETTO COMPLETO PROFILE USER PACKET:");
        System.out.println("Payload (Hex): " + java.util.HexFormat.ofDelimiter(" ").formatHex(profileRoomPacket));

        return profileRoomPacket;

    }


    public class UserPreference {
        public final byte index;
        public final byte value;

        public UserPreference(byte index, byte value) {
            this.index = index;
            this.value = value;
        }
    }
}
