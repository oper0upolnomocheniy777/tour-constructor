package com.sfedu.touragency.controller;


import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.service.AuthService;
import com.sfedu.touragency.util.ServiceLocator;

public final class LoginController extends Controller {

    private AuthService authService = ServiceLocator.INSTANCE.get(AuthService.class);

    @Override
    public void get(RequestService reqService) {
        String username = (String) reqService.getFlashParameter("username");
        reqService.setPageAttribute("username", username);
    }

    @Override
    public void post(RequestService reqService) {
        String username = reqService.getString("username");
        String password = reqService.getString("password");
        String destination = reqService.getRequest().getHeader("Referer");

        if (authService.login(reqService.getRequest(), username, password)) {
            if(destination.contains("login")) {
                reqService.redirect("/");
            } else {
                reqService.redirect(destination);
            }
        } else {
            reqService.redirect("/login.html?failed=true");
            reqService.putFlashParameter("username", username);
        }
    }
}
