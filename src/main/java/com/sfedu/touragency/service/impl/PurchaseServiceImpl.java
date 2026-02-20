package com.sfedu.touragency.service.impl;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.domain.PurchaseStatus;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.dao.PurchaseDao;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.persistence.transaction.Transaction;
import com.sfedu.touragency.service.AbstractDaoService;
import com.sfedu.touragency.service.PurchaseService;
import com.sfedu.touragency.util.Group;
import com.sfedu.touragency.util.Tuple;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class PurchaseServiceImpl extends AbstractDaoService<Purchase, Long>
        implements PurchaseService {

    private PurchaseDao purchaseDao;

    private TourDao tourDao;

    private UserDao userDao;

    private ConnectionManager cm;

    private static final double HUNDR_PERCENT = 100.0;

    public PurchaseServiceImpl(PurchaseDao purchaseDao, TourDao tourDao, UserDao userDao, ConnectionManager cm) {
        this.purchaseDao = purchaseDao;
        this.tourDao = tourDao;
        this.userDao = userDao;
        this.cm = cm;
    }

    @Override
    public void create(Purchase purchase) {
        Long id = purchaseDao.create(purchase);
        purchase.setId(id);
    }

    @Override
    public Purchase read(Long id) {
        Purchase purchase = super.read(id);

        if (purchase != null) {
            return deepen(purchase);
        }

        return null;
    }

    @Override
    public List<Purchase> findByUser(Long id) {
        List<Purchase> purchases = purchaseDao.findByUser(id);
        purchases.forEach(this::deepen);
        return purchases;
    }

    @Override
    public Purchase deepen(Purchase purchase) {
        Transaction.tx(cm, () -> {
            purchase.setUser(userDao.read(purchase.getUser().getId()));
            purchase.setTour(tourDao.read(purchase.getTour().getId()));
        });

        return purchase;
    }

    @Override
    public Purchase purchase(Long userId, Long tourId) {
        Purchase purchase = new Purchase();
        Tour tour = tourDao.read(tourId);
        User user = userDao.read(userId);

        int maxDiscount = Math.max(tour.getDiscount(), user.getDiscount());
        double discountDouble = (HUNDR_PERCENT - maxDiscount) / HUNDR_PERCENT;
        BigDecimal discount = new BigDecimal(discountDouble);
        BigDecimal price = tour.getPrice().multiply(discount);

        purchase.setTour(new Tour(tourId));
        purchase.setUser(new User(userId));
        purchase.setDate(new Date());
        purchase.setPrice(price);

        Long id = purchaseDao.create(purchase);
        purchase.setId(id);

        return purchase;
    }

    @Override
    public List<Purchase> findByUserTour(Long userId, Long tourId) {
        List<Purchase> purchases = purchaseDao.findByUserTour(userId, tourId);
        purchases.forEach(this::deepen);
        return purchases;
    }

    @Override
    public List<Group<Tour, Purchase>> findByUserGroupByTourOrdered(Long userId) {
        List<Purchase> purchases = findByUser(userId);
        Map<Tour, List<Purchase>> grouped =
                new TreeMap<>((t1, t2) -> (int) (t1.getId() - t2.getId()));

        for (Purchase p: purchases) {
            List<Purchase> group = grouped.getOrDefault(p.getTour(), new ArrayList<>());
            group.add(p);
            grouped.put(p.getTour(), group);
        }

        List<Group<Tour, Purchase>> sorted = grouped.entrySet().stream()
                .map(e -> new Group<>(e.getKey(), e.getValue()))
                .map(g -> new Tuple<>(g, Collections.max(g.getElems(),
                        Comparator.comparing(Purchase::getDate))))
                .sorted(Comparator.comparing(t -> t.getSecond().getDate()))
                .map(Tuple::getFirst)
                .collect(Collectors.toList());

        Collections.reverse(sorted);
        return sorted;
    }

    @Override
    public void acknowledge(Long purchaseId) {
        Purchase purchase = purchaseDao.read(purchaseId);
        purchase.setStatus(PurchaseStatus.PREPARED);
        purchaseDao.update(purchase);
    }

    @Override
    public void cancel(Long purchaseId) {
        Purchase purchase = purchaseDao.read(purchaseId);
        purchase.setStatus(PurchaseStatus.CANCELED);
        purchaseDao.update(purchase);
    }

    @Override
    public void use(Long purchaseId) {
        Purchase purchase = purchaseDao.read(purchaseId);
        purchase.setStatus(PurchaseStatus.USED);
        purchaseDao.update(purchase);
    }

    @Override
    public void purchase(Long userId, Long tourId, int number) {
        for (int i = 0; i < number; i++) {
            purchase(userId, tourId);
        }
    }

    @Override
    public List<Purchase> findNotProcessed() {
        List<Purchase> purchases =
                purchaseDao.findByStatusOrderByDate(PurchaseStatus.ACTIVE);
        purchases.forEach(this::deepen);
        return purchases;
    }

    @Override
    public PurchaseDao getBackingDao() {
        return purchaseDao;
    }

}
