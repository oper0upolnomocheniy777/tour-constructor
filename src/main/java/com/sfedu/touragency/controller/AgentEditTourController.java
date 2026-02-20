package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.service.TourService;
import com.sfedu.touragency.util.ServiceLocator;

public class AgentEditTourController extends Controller {

    private TourService tourService = ServiceLocator.INSTANCE.get(TourService.class);

    @Override
    public void get(RequestService reqService) {
        Long id = reqService.getLong("id").orElse(null);
        reqService.setPageAttribute("tour", tourService.read(id));
    }
}
