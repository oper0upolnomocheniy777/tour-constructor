package com.sfedu.touragency.controller;

import com.sfedu.touragency.controller.support.RequestExtractors;
import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.security.AccessDeniedException;
import com.sfedu.touragency.service.ReviewService;
import com.sfedu.touragency.util.ServiceLocator;

public class ReviewController extends Controller {

    private ReviewService reviewService = ServiceLocator.INSTANCE.get(ReviewService.class);

    @Override
    public void post(RequestService reqService) {
        Review review = RequestExtractors.extractReview(reqService);
        reviewService.create(review);
    }

    @Override
    public void put(RequestService reqService) {
        Review review = RequestExtractors.extractReview(reqService);
        reviewService.update(review);
    }

    @Override
    public void delete(RequestService reqService) {
        Long id = reqService.getLong("id").get();

        Review review = reviewService.read(id);
        User user = reqService.getUser().get();

        if (!review.getAuthor().getId().equals(user.getId())
                && !reqService.getRequest().isUserInRole(Role.TOUR_AGENT.name())) {
            throw new AccessDeniedException();
        }

        reviewService.delete(id);
    }
}
