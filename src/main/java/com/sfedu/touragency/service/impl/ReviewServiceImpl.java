package com.sfedu.touragency.service.impl;

import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.dao.ReviewDao;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.persistence.transaction.Transaction;
import com.sfedu.touragency.service.AbstractDaoService;
import com.sfedu.touragency.service.ReviewService;

import java.util.*;

public class ReviewServiceImpl extends AbstractDaoService<Review, Long>
        implements ReviewService {
    private ReviewDao reviewDao;

    private UserDao userDao;

    private TourDao tourDao;

    private ConnectionManager cm;

    public ReviewServiceImpl(ReviewDao reviewDao, UserDao userDao, TourDao tourDao,
                             ConnectionManager cm) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.tourDao = tourDao;
        this.cm = cm;
    }

    @Override
    public void create(Review review) {
        Transaction.tx(cm, () -> {
            checkConstraintsOrThrow(review);
            if(reviewDao.canVote(review.getAuthor().getId(), review.getTour().getId())) {
                review.setDate(new Date());
                Long id = reviewDao.create(review);
                review.setId(id);

                Tour tour = tourDao.read(review.getTour().getId());
                double rating = tour.getAvgRating() == null ? 0 : tour.getAvgRating();

                rating = rating*tour.getVotesCount() + review.getRating();
                rating = rating / (tour.getVotesCount() + 1);
                tour.setAvgRating(rating);
                tour.setVotesCount(tour.getVotesCount() + 1);

                tourDao.update(tour);
            }
        });
    }

    @Override
    public void update(Review review) {
        Transaction.tx(cm, () -> {
            checkConstraintsOrThrow(review);
            int delta = review.getRating() - reviewDao.read(review.getId()).getRating();
            review.setDate(new Date());
            super.update(review);

            Tour tour = tourDao.read(review.getTour().getId());
            double rating = (tour.getAvgRating()*tour.getVotesCount() + delta);
            rating = rating / tour.getVotesCount();
            tour.setAvgRating(rating);
            tourDao.update(tour);
        });
    }

    @Override
    public void delete(Long id) {
        Transaction.tx(cm, () -> {
            Review review = reviewDao.read(id);

            super.delete(id);

            Tour tour = tourDao.read(review.getTour().getId());
            Double rating = null;
            if (tour.getVotesCount() > 1) {
                rating = tour.getAvgRating()*tour.getVotesCount() - review.getRating();
                rating = rating / (tour.getVotesCount() - 1);
            }
            tour.setAvgRating(rating);
            tour.setVotesCount(tour.getVotesCount() - 1);
            tourDao.update(tour);
        });
        super.delete(id);
    }

    @Override
    public List<Review> findByTour(Long id) {
        List<Review> reviews = reviewDao.findByTour(id);
        for(Review review: reviews) {
            User user = userDao.read(review.getAuthor().getId());
            review.setAuthor(user);
        }
        return reviews;
    }

    @Override
    public boolean canVote(Long userId, Long tourId) {
        if(userId == null || tourId == null) {
            return false;
        }
        return reviewDao.canVote(userId, tourId);
    }

    @Override
    public Review findByPurchase(Long userId, Long tourId) {
        return reviewDao.findByPurchase(userId, tourId);
    }

    @Override
    public ReviewDao getBackingDao() {
        return reviewDao;
    }

    private void checkConstraintsOrThrow(Review review) {
        boolean ratingOk = review.getRating() > 0 && review.getRating() <= 5;
        if (!ratingOk) {
            throw new IllegalStateException("Rating should be > 0 and <= 5");
        }
    }
}
