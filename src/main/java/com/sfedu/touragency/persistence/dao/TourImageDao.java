package com.sfedu.touragency.persistence.dao;

import com.sfedu.touragency.domain.TourImage;
import com.sfedu.touragency.persistence.Dao;

import java.util.*;

public interface TourImageDao extends Dao<TourImage, Long> {
    List<TourImage> findByTour(Long tourId);
}
