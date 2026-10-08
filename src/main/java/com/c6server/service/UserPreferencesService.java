package com.c6server.service;

import com.c6server.c6enum.C6EnumUserProfilePreferences;
import com.c6server.dao.DatabaseConnection;
import com.c6server.dao.UserPreferencesDAO;
import com.c6server.model.UserProfileEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserPreferencesService {

    /**
     * Converte la voce scelta in un menu (categoria + posizione) nella costante dell'enum.
     * Restituisce null se non è stato scelto nulla ("----------") o il valore non è valido.
     */
    public C6EnumUserProfilePreferences toPreference(int categoryIndex, String rawPosition) {
        if (rawPosition == null) {
            return null;
        }
        try {
            int position = Integer.parseInt(rawPosition.trim());
            if (position <= 0 || position > Byte.MAX_VALUE) {
                return null;
            }
            return C6EnumUserProfilePreferences.fromBytes((byte) categoryIndex, (byte) position);
        } catch (IllegalArgumentException e) {   // numero non valido o costante inesistente
            return null;
        }
    }

    /** Costruisce il profilo; valori nulli o ripetuti vengono ignorati. */
    public UserProfileEntity buildProfile(Collection<C6EnumUserProfilePreferences> scelte) {
        Set<C6EnumUserProfilePreferences> uniche = new LinkedHashSet<>();
        for (C6EnumUserProfilePreferences p : scelte) {
            if (p != null) {
                uniche.add(p);
            }
        }

        UserProfileEntity profile = new UserProfileEntity();
        for (C6EnumUserProfilePreferences p : uniche) {
            profile.addGeneric(p);
        }
        return profile;
    }

    public void saveProfile(String nickname, UserProfileEntity profile) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            new UserPreferencesDAO(conn).saveProfile(nickname, profile);
        }
    }
}