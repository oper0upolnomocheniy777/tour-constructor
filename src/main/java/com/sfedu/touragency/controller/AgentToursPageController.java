package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.service.TourService;
import com.sfedu.touragency.util.ServiceLocator;
import org.apache.logging.log4j.*;

import java.util.*;

public final class AgentToursPageController extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(AgentToursPageController.class);

    private TourService tourService = ServiceLocator.INSTANCE.get(TourService.class);

    @Override
    public void get(RequestService reqService) {
        List<Tour> tours = tourService.findAll();
        Collections.reverse(tours);
        reqService.setPageAttribute("tours", tours);
    }

    @Override
    public void post(RequestService reqService) {
        String command = reqService.getString("command");
        Long id = reqService.getLong("id").orElse(null);

        switch (command) {
            case "toggle":
                tourService.toggleEnabled(id);
                break;
            default:
                LOGGER.info("Default branch should not be reached");
        }
    }
}
