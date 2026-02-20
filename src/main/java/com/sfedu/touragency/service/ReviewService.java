package com.sfedu.touragency.service;

import com.sfedu.touragency.domain.Review;

import java.util.*;

public interface ReviewService extends CrudService<Review, Long> {
    List<Review> findByTour(Long id);

    boolean canVote(Long userId, Long tourId);

    Review findByPurchase(Long userId, Long tourId);
}
