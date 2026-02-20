package com.sfedu.touragency.persistence.dao;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.persistence.Dao;

import java.math.*;
import java.util.*;

public interface TourDao extends Dao<Tour, Long> {
    BigDecimal computePrice(Long tourId, Long userId);

    Tour findRandomHot();

    List<Tour> executeDynamicFilter(ToursDynamicFilter filter);
}
