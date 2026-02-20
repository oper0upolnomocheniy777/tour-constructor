package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.service.TourService;
import com.sfedu.touragency.util.ServiceLocator;

import java.util.*;

public final class RandomHotTourController extends Controller {

    private TourService tourService = ServiceLocator.INSTANCE.get(TourService.class);

    private final int COUNT = 5;

    @Override
    public void get(RequestService reqService) {
        Set<Tour> randomTours = tourService.findRandomHotTours(COUNT);
        reqService.setPageAttribute("tours", randomTours);
    }
}
