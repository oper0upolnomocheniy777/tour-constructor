package com.sfedu.touragency.service.impl;

import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.service.AuthService;
import com.sfedu.touragency.service.UserService;
import com.sfedu.touragency.util.PasswordEncoder;
import org.apache.logging.log4j.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean login(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
            return true;
        } catch (ServletException e) {
            return false;
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        try {
            request.logout();
        } catch (ServletException e) {
            LOGGER.error("Error occurred when user tried to logout", e);
        }
    }

    @Override
    public boolean register(User user) {
        user.setPassword(PasswordEncoder.encodePassword(user.getPassword()));
        user.setRoles(Collections.singletonList(Role.CUSTOMER));
        try {
            userService.create(user);
        } catch (Exception e) {
            user.setId(null);
        }

        return user.getId() != null;
    }
}
