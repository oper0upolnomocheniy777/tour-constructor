package com.sfedu.touragency.service.impl;

import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.service.AbstractDaoService;
import com.sfedu.touragency.service.UserService;

import java.math.*;
import java.util.*;

public class UserServiceImpl extends AbstractDaoService<User, Long>
        implements UserService {

    private UserDao userDao;

    private TourDao tourDao;

    public UserServiceImpl(UserDao userDao, TourDao tourDao) {
        this.userDao = userDao;
        this.tourDao = tourDao;
    }

    @Override
    public void create(User user) {
        checkConstraints(user);
        Long id = userDao.create(user);
        user.setId(id);
    }

    private void checkConstraints(User user) {
        if (user.getDiscount() < 0) {
            throw new IllegalStateException("Discount should be positive");
        }
    }

    @Override
    public UserDao getBackingDao() {
        return userDao;
    }

    @Override
    public List<User> findAllOrderByRegularity(boolean byTotalPrice) {
        List<User> users = userDao.findAllOrderByRegularity(byTotalPrice);
        users.forEach(u -> u.setRoles(userDao.readRoles(u.getId())));
        return users;
    }

    @Override
    public int countPurchases(Long userId) {
        return userDao.countPurchases(userId);
    }

    @Override
    public BigDecimal computePurchasesTotalPrice(Long userId) {
        return userDao.computePurchasesTotalPrice(userId);
    }

    @Override
    public void makeTourAgent(Long userId) {
        List<Role> roles = userDao.readRoles(userId);
        if(!roles.contains(Role.TOUR_AGENT)) {
            roles.add(Role.TOUR_AGENT);
        }
        userDao.updateRoles(userId, roles);
    }

    @Override
    public int computeDiscount(Long userId, Long tourId) {
        User user = userDao.read(userId);
        Tour tour = tourDao.read(tourId);
        return Math.max(user.getDiscount(), tour.getDiscount());
    }
}
