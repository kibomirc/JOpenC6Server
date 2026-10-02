package com.c6server.utils;

import com.c6server.c6enum.C6EnumUserProfilePreferences;
import com.c6server.model.UserProfileSearchEntity;

public class NetFriendSearchUtils {

    public static UserProfileSearchEntity parsePreferences(byte[] data) {
        UserProfileSearchEntity search = new UserProfileSearchEntity();

        int offset = 13;
        int numPreferences = data[offset++] & 0xFF;

        for (int i = 0; i < numPreferences; i++) {
            byte index = data[offset++];
            byte val   = data[offset++];

            try {
                C6EnumUserProfilePreferences pref = C6EnumUserProfilePreferences.fromBytes(index, val);
                switch (index) {
                    case 0x01 -> search.setEta(pref);
                    case 0x02 -> search.setGenere(pref);
                    case 0x03 -> search.setOrientamento(pref);
                    case 0x04 -> search.setOccupazione(pref);
                    case 0x05 -> search.setAreaGeografica(pref);
                    case 0x06 -> search.setRegione(pref);
                    case 0x07 -> search.setHobby(pref);
                    case 0x08 -> search.setSport(pref);
                    case 0x09 -> search.setGenereMusicale(pref);
                    case 0x0A -> search.setGenereCinematografico(pref);
                    case 0x0B -> search.setComunitaVirtuale(pref);
                    case 0x0C -> search.setOdiCordiali(pref);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Preferenza sconosciuta: 0x"
                        + String.format("%02X", index) + " 0x"
                        + String.format("%02X", val));
            }
        }

        return search;
    }
}
