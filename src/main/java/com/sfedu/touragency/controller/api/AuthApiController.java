package com.sfedu.touragency.controller.api;

import com.google.gson.Gson;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.service.AuthService;
import com.sfedu.touragency.service.UserService;
import com.sfedu.touragency.util.ServiceLocator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthApiController extends HttpServlet {
    private AuthService authService = ServiceLocator.INSTANCE.get(AuthService.class);
    private UserService userService = ServiceLocator.INSTANCE.get(UserService.class);
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        if ("/login".equals(pathInfo)) {
            handleLogin(req, resp);
        } else if ("/register".equals(pathInfo)) {
            handleRegister(req, resp);
        } else if ("/logout".equals(pathInfo)) {
            handleLogout(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Endpoint not found");
            resp.getWriter().write(gson.toJson(error));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, String> credentials = gson.fromJson(reader, Map.class);
        
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (authService.login(req, username, password)) {
            User user = (User) req.getSession().getAttribute("user");
            
            // Убираем пароль из ответа
            user.setPassword(null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", req.getSession().getId());
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Неверное имя пользователя или пароль");
            resp.getWriter().write(gson.toJson(error));
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, String> userData = gson.fromJson(reader, Map.class);
        
        User user = new User();
        user.setUsername(userData.get("username"));
        user.setPassword(userData.get("password"));
        user.setFirstName(userData.get("firstName"));
        user.setLastName(userData.get("lastName"));
        user.setTelephone(userData.get("telephone"));
        
        if (authService.register(user)) {
            authService.login(req, user.getUsername(), userData.get("password"));
            
            user.setPassword(null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", req.getSession().getId());
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Пользователь с таким именем уже существует");
            resp.getWriter().write(gson.toJson(error));
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) {
        authService.logout(req);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        if ("/me".equals(pathInfo)) {
            User user = (User) req.getSession().getAttribute("user");
            if (user != null) {
                user.setPassword(null);
                resp.getWriter().write(gson.toJson(user));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}