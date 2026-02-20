package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.service.UserService;
import com.sfedu.touragency.util.ServiceLocator;

public final class UpdateDiscountController extends Controller {
    private UserService userService = ServiceLocator.INSTANCE.get(UserService.class);

    @Override
    public void post(RequestService reqService) {
        Long userId = reqService.getLong("id").orElse(null);
        Integer discount = reqService.getInt("discount").orElse(0);
        User user = userService.read(userId);
        user.setDiscount(discount);

        userService.update(user);

        reqService.redirect("/agent/users.html");
    }
}
