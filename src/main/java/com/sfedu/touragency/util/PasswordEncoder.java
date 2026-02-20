package com.sfedu.touragency.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Contains one static method that encodes a password
 */
public class PasswordEncoder {
    private static final Logger LOGGER = LogManager.getLogger(PasswordEncoder.class);

    public static String encodePassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Cannot find digest algorithm", e);
            return null;
        }
        byte[] passwordBytes = password.getBytes();
        byte[] hash = md.digest(passwordBytes);
        String passwordHash =
                Base64.getEncoder().encodeToString(hash);

        return passwordHash;
    }
}
