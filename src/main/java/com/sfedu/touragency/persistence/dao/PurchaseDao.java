package com.sfedu.touragency.persistence.dao;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.domain.PurchaseStatus;
import com.sfedu.touragency.persistence.Dao;

import java.util.*;

public interface PurchaseDao extends Dao<Purchase,Long> {
    List<Purchase> findByUser(Long userId);

    List<Purchase> findByUserTour(Long userId, Long tourId);

    List<Purchase> findByStatusOrderByDate(PurchaseStatus active);
}
