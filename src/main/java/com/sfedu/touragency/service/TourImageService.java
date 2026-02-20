package com.sfedu.touragency.service;

import com.sfedu.touragency.domain.TourImage;

import java.util.*;

public interface TourImageService extends CrudService<TourImage, Long> {
    List<TourImage> findByTour(long tourId);
}
