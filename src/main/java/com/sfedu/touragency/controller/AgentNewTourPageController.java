package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Tour;

public class AgentNewTourPageController extends Controller {
    @Override
    public void get(RequestService reqService) {
        Tour tour = (Tour) reqService.getFlashParameter("tour");
        reqService.setPageAttribute("tour", tour);
    }
}
