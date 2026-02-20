package com.sfedu.touragency.persistence.dao;

import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.persistence.Dao;

import java.util.*;

public interface ReviewDao extends Dao<Review, Long> {

    List<Review> findByTour(Long id);

    boolean canVote(Long userId, Long tourId);

    Review findByPurchase(Long userId, Long tourId);
}
