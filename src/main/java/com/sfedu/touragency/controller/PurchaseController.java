package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.service.PurchaseService;
import com.sfedu.touragency.service.TourService;
import com.sfedu.touragency.util.ServiceLocator;

import java.util.*;

public class PurchaseController extends Controller {

    private PurchaseService purchaseService =
            ServiceLocator.INSTANCE.get(PurchaseService.class);

    private TourService tourService = ServiceLocator.INSTANCE.get(TourService.class);

    @Override
    public void get(RequestService reqService) {
        Long tourId = reqService.getLong("tourId").get();
        Long userId = reqService.getUser().get().getId();
        List<Purchase> purchases = purchaseService.findByUserTour(userId, tourId);
        reqService.setPageAttribute("purchases", purchases);
        reqService.setPageAttribute("tourId", tourId);
        reqService.setPageAttribute("tour", purchases.get(0).getTour());
    }
}
