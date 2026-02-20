package com.sfedu.touragency.service;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.util.Group;

import java.util.*;

public interface PurchaseService extends CrudService<Purchase, Long> {
    List<Purchase> findByUser(Long id);

    Purchase deepen(Purchase purchase);

    Purchase purchase(Long userId, Long tourId);

    List<Purchase> findByUserTour(Long userId, Long tourId);

    List<Group<Tour, Purchase>> findByUserGroupByTourOrdered(Long id);

    void acknowledge(Long purchaseId);

    void cancel(Long purchaseId);

    void use(Long purchaseId);

    void purchase(Long userId, Long tourId, int number);

    List<Purchase> findNotProcessed();
}
