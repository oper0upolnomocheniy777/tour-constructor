package com.sfedu.touragency.service.impl;

import com.sfedu.touragency.domain.TourImage;
import com.sfedu.touragency.persistence.dao.TourImageDao;
import com.sfedu.touragency.service.AbstractDaoService;
import com.sfedu.touragency.service.TourImageService;

import java.util.*;

public class TourImageServiceImpl extends AbstractDaoService<TourImage, Long>
        implements TourImageService {
    private TourImageDao tourImageDao;

    public TourImageServiceImpl(TourImageDao tourImageDao) {
        this.tourImageDao = tourImageDao;
    }

    @Override
    public void create(TourImage tourImage) {
        Long id = tourImageDao.create(tourImage);
        tourImage.setId(id);
    }

    // backing dao
    @Override
    public TourImageDao getBackingDao() {
        return tourImageDao;
    }

    @Override
    public List<TourImage> findByTour(long tourId) {
        return tourImageDao.findByTour(tourId);
    }
}
