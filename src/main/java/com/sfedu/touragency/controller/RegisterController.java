package com.sfedu.touragency.controller;

import com.sfedu.touragency.controller.support.RequestExtractors;
import com.sfedu.touragency.controller.support.Validation;
import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.service.AuthService;
import com.sfedu.touragency.util.ServiceLocator;

import java.util.*;

public final class RegisterController extends Controller {

    private AuthService authService = ServiceLocator.INSTANCE.get(AuthService.class);

    @Override
    public void get(RequestService reqService) {
        User user = (User) reqService.getFlashParameter("user");
        reqService.setPageAttribute("user", user);
        reqService.setPageAttribute("error", reqService.getFlashParameter("error"));
    }

    @Override
    public void post(RequestService reqService) {
        User user = RequestExtractors.extractUser(reqService);

        Optional<String> invalid = Validation.check(user);
        if (invalid.isPresent()) {
            user.setPassword(null);
            reqService.putFlashParameter("user", user);
            reqService.putFlashParameter("error", invalid.get());

            reqService.redirect("/register.html");
            return;
        }

        if(!authService.register(user)) {
            reqService.putFlashParameter("error", "register.error.username");

            reqService.redirect("/register.html");
            return;
        }

        reqService.redirect("/login.html");
    }
}
