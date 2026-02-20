package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.service.UserService;
import com.sfedu.touragency.util.ServiceLocator;

public class AgentSetAdminController extends Controller {
    private UserService userService = ServiceLocator.INSTANCE.get(UserService.class);

    @Override
    public void post(RequestService reqService) {
        Long id = reqService.getLong("id").get();
        userService.makeTourAgent(id);
    }
}
