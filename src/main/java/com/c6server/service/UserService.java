package com.c6server.service;

import com.c6server.dao.DatabaseConnection;
import com.c6server.dao.UserDAO;
import com.c6server.dao.UserPreferencesDAO;
import com.c6server.model.UserProfileEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

public class UserService {

    public enum RegisterError {
        NICK_EMPTY, NICK_TOO_LONG, NICK_TAKEN,
        PASSWORD_INVALID,
        EMAIL_EMPTY, EMAIL_TAKEN
    }

    private static final int NICK_MAX_LENGTH = 10;
    private static final String PASSWORD_PATTERN = "[a-z0-9]+";


    public Set<RegisterError> validate(String nickname, String password, String email)
            throws SQLException {
        Set<RegisterError> errors = EnumSet.noneOf(RegisterError.class);

        boolean nickValid = true;
        if (nickname == null || nickname.isBlank()) {
            errors.add(RegisterError.NICK_EMPTY);
            nickValid = false;
        } else if (nickname.length() > NICK_MAX_LENGTH) {
            errors.add(RegisterError.NICK_TOO_LONG);
            nickValid = false;
        }

        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            errors.add(RegisterError.PASSWORD_INVALID);
        }

        boolean emailValid = true;
        if (email == null || email.isBlank()) {
            errors.add(RegisterError.EMAIL_EMPTY);
            emailValid = false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);

            if (nickValid && userDAO.exists(nickname)) {
                errors.add(RegisterError.NICK_TAKEN);
            }
            if (emailValid && userDAO.existsByEmail(email)) {
                errors.add(RegisterError.EMAIL_TAKEN);
            }
        }

        return errors;
    }


    public Set<RegisterError> register(String nickname, String password, String email,
                                       UserProfileEntity profile) throws SQLException {
        Set<RegisterError> errors = validate(nickname, password, email);
        if (!errors.isEmpty()) {
            return errors;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                new UserDAO(conn).create(nickname, password, email);
                new UserPreferencesDAO(conn).saveProfile(nickname, profile);
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
        return errors;
    }
}