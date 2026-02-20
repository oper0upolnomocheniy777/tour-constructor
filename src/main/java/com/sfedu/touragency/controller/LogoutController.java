package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.service.AuthService;
import com.sfedu.touragency.util.ServiceLocator;
import org.apache.logging.log4j.*;

public final class LogoutController extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(LogoutController.class);

    private AuthService authService = ServiceLocator.INSTANCE.get(AuthService.class);

    @Override
    public void post(RequestService reqService) {
        authService.logout(reqService.getRequest());
        reqService.redirect("/login.html");
    }
}
