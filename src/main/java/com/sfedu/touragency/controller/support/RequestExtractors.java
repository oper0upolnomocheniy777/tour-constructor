package com.sfedu.touragency.controller.support;

import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.TourType;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.util.WithStatus;

import java.math.*;

/**
 * This class contains static methods for deserializing entities from HTML forms
 */
public class RequestExtractors {
    public static Review extractReview(RequestService reqService) {
        Review review = new Review();

        review.setId(reqService.getLong("id").orElse(null));
        review.setRating(reqService.getInt("rating").orElse(null));
        review.setTour(reqService.getLong("tourId").map(Tour::new).orElse(null));
        review.setAuthor(reqService.getUser().orElse(null));
        review.setText(reqService.getString("text"));

        return review;
    }

    public static User extractUser(RequestService reqService) {
        User user = new User();

        user.setUsername(reqService.getString("username"));
        user.setFirstName(reqService.getString("firstName"));
        user.setLastName(reqService.getString("lastName"));
        user.setPassword(reqService.getString("password"));
        user.setTelephone(reqService.getString("telephone").replaceAll(" ", ""));

        return user;
    }

    public static WithStatus<Tour> extractTourWithStatus(RequestService reqService) {
        Tour tour = new Tour();

        tour.setId(reqService.getLong("id").orElse(null));
        tour.setTitle(reqService.getString("title"));
        tour.setDescription(reqService.getString("description"));
        tour.setDestination(reqService.getString("destination"));
        tour.setType(TourType.values()[reqService.getInt("type").get()]);
        tour.setHot(reqService.getBool("hot").orElse(false));
        tour.setEnabled(reqService.getBool("enabled").orElse(true));
        tour.setDiscount(reqService.getInt("discount").orElse(0));

        try {
            tour.setPrice(new BigDecimal(reqService.getString("price")));
        } catch (NumberFormatException e) {
            return WithStatus.bad(tour);
        }

        return WithStatus.ok(tour);
    }
}
