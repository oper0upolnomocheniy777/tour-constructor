package com.sfedu.touragency.service;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.persistence.dao.ToursDynamicFilter;

import java.math.*;
import java.util.*;

public interface TourService extends CrudService<Tour, Long> {

    Set<Tour> findRandomHotTours(int count);

    void toggleEnabled(Long id);

    BigDecimal computePrice(Long tourId, Long userId);

    List<Tour> executeDynamicFilter(ToursDynamicFilter filter);
}
