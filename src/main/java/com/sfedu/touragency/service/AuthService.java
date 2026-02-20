package com.sfedu.touragency.service;

import com.sfedu.touragency.domain.User;

import javax.servlet.http.*;

public interface AuthService {
    boolean login(HttpServletRequest request, String user, String password);
    void logout(HttpServletRequest request);
    boolean register(User user);
}
