package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.service.TourService;
import com.sfedu.touragency.service.UserService;
import com.sfedu.touragency.util.ServiceLocator;

public final class BuyController extends Controller {
    private TourService tourService = ServiceLocator.INSTANCE.get(TourService.class);

    private UserService userService = ServiceLocator.INSTANCE.get(UserService.class);

    @Override
    public void get(RequestService reqService) {
        Long tourId = reqService.getLong("tourId").get();
        Tour tour = tourService.read(tourId);
        Long userId = reqService.getUser().get().getId();

        reqService.setPageAttribute("tour", tour);
        reqService.setPageAttribute("discount", userService.computeDiscount(userId, tourId));
        reqService.setPageAttribute("price", tourService.computePrice(tourId, userId));
    }
}
