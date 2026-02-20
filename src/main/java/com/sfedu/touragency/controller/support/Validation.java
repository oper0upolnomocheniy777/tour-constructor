package com.sfedu.touragency.controller.support;

import com.sfedu.touragency.domain.User;

import java.util.*;

/**
 * This class holds static methods intended for user input validation
 */
public class Validation {
    public static Optional<String> check(User user) {

        if (user.getLastName() == null) {
            return Optional.of("validation.user.last_name_empty");
        }else {
            if (user.getLastName().length() < 3) {
                return Optional.of("validation.user.last_name_len");
            }
        }

        if (user.getFirstName() == null) {
            return Optional.of("validation.user.first_name_empty");
        } else {
            if (user.getFirstName().length() < 3) {
                return Optional.of("validation.user.first_name_len");
            }
        }

        if (user.getUsername().length() < 3) {
            return Optional.of("validation.user.username_len");
        }

        if (user.getPassword().length() < 6
                || user.getPassword().length() >= 48) {
            return Optional.of("validation.user.password_len");
        }

        String tel = user.getTelephone();
        if (!tel.matches("^\\+[0-9]{10,}$")) {
            return Optional.of("validation.user.telephone");
        }

        return Optional.empty();
    }
}
