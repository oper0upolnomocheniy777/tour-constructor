package com.sfedu.touragency.service.impl;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.ToursDynamicFilter;
import com.sfedu.touragency.service.AbstractDaoService;
import com.sfedu.touragency.service.TourService;

import java.math.*;
import java.util.*;

public class TourServiceImpl extends AbstractDaoService<Tour, Long>
        implements TourService {

    private final int TRIALS_LIMIT = 5;

    private final Comparator<Tour> tourComparator =
            (t1, t2) -> (int) Math.signum(t1.getId() - t2.getId());

    private TourDao tourDao;

    public TourServiceImpl(TourDao tourDao) {
        this.tourDao = tourDao;
    }

    @Override
    public void create(Tour tour) {
        checkConstraintsOrThrow(tour);
        Long id = tourDao.create(tour);
        tour.setId(id);
    }

    @Override
    public void update(Tour tour) {
        Tour dbTour = read(tour.getId());
        tour.setAvgRating(dbTour.getAvgRating());
        tour.setVotesCount(dbTour.getVotesCount());
        checkConstraintsOrThrow(tour);
        super.update(tour);
    }

    @Override
    public Set<Tour> findRandomHotTours(int count) {
        Set<Tour> randomTours = new TreeSet<>(tourComparator);
        int trials = 0;

        while (randomTours.size() < count && trials <= TRIALS_LIMIT) {
            Tour tour = tourDao.findRandomHot();

            if(tour != null) {
                int currSize = randomTours.size();
                randomTours.add(tour);

                if(randomTours.size() == currSize) {
                    trials++;
                } else {
                    trials = 0;
                }
            } else {
                trials++;
            }
        }

        return randomTours;
    }

    @Override
    public void toggleEnabled(Long id) {
        Tour tour = tourDao.read(id);
        tour.setEnabled(!tour.isEnabled());
        tourDao.update(tour);
    }

    @Override
    public BigDecimal computePrice(Long tourId, Long userId) {
        return tourDao.computePrice(tourId, userId);
    }

    @Override
    public List<Tour> executeDynamicFilter(ToursDynamicFilter filter) {
        List<Tour> tours = tourDao.executeDynamicFilter(filter);
        return tours;
    }

    @Override
    public TourDao getBackingDao() {
        return tourDao;
    }

    private void checkConstraintsOrThrow(Tour tour) {
        if (tour.getPrice() != null && tour.getPrice().signum() <= 0) {
            throw new IllegalStateException("Tour price should be positive");
        }

        if (tour.getDiscount() < 0 || tour.getDiscount() >= 100) {
            throw new IllegalStateException("Discount should be positive and " +
                    "not exceeding 100%");
        }

         if (tour.getVotesCount() > 0) {
            if (tour.getAvgRating() < 1 || tour.getAvgRating() > 5) {
                throw new IllegalStateException("Average rating should be in range [1; 5] ");
            }
        } else if (tour.getVotesCount() < 0){
                throw new IllegalStateException("Votes count should be positive");
        }

    }
}
