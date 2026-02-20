package com.sfedu.touragency.persistence;

import com.sfedu.touragency.domain.*;
import com.sfedu.touragency.persistence.dao.PurchaseDao;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.persistence.dao.jdbc.TourJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.UserJdbcDao;
import org.apache.logging.log4j.*;

import java.math.*;
import java.net.*;
import java.util.*;

public class TestData {
    private static final Logger LOGGER = LogManager.getLogger(TestData.class);

    public static User getUser() {
        User user = new User();
        user.setPassword("passw");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john");
        user.setDiscount(20);
        user.setTelephone("380123456789");
        user.setRoles(Arrays.asList(Role.CUSTOMER));
        return user;
    }

    public static User getPrivilegedUser() {
        User user = getUser();
        user.setRoles(Arrays.asList(Role.CUSTOMER, Role.TOUR_AGENT));
        return user;
    }

    public static Tour getExcursionTour() {
        Tour tour = new Tour();
        tour.setEnabled(true);
        tour.setHot(false);
        tour.setPrice(new BigDecimal(100));
        tour.setDescription("NO DESC");
        tour.setTitle("Tour 1");
        tour.setDestination("Dest");
        tour.setType(TourType.EXCURSION);
        return tour;
    }

    public static Tour getShoppingTour() {
        Tour tour = new Tour();
        tour.setEnabled(true);
        tour.setHot(false);
        tour.setPrice(new BigDecimal(10));
        tour.setDescription("NO DESC");
        tour.setTitle("Tour 2");
        tour.setDestination("Dest");
        tour.setType(TourType.SHOPPING);
        return tour;
    }

    public static Tour getRecreationTour() {
        Tour tour = new Tour();
        tour.setEnabled(true);
        tour.setHot(false);
        tour.setPrice(new BigDecimal(50));
        tour.setDescription("NO DESC");
        tour.setTitle("Tour 3");
        tour.setDestination("Dest");
        tour.setType(TourType.RECREATION);
        return tour;
    }

    public static TourImage getTourImage(Tour tour) {
        TourImage tourImage = new TourImage();
        tourImage.setTour(tour);
        try {
            tourImage.setImageUrl(new URL("http://127.1/hi.jpeg"));
            tourImage.setThumbnailUrl(new URL("http://127.1/hi-thumb.jpeg"));
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed test URL");
        }
        return tourImage;
    }

    public static UserTestData getUserTestData(ConnectionManager cm) {
        return getUserTestData(cm, "");
    }

    public static UserTestData getUserTestData(ConnectionManager cm, String seed) {
        TourDao tourDao = new TourJdbcDao(cm);
        UserDao userDao = new UserJdbcDao(cm);

        Tour tour1 = TestData.getExcursionTour();
        tour1.setTitle(tour1.getTitle() + seed);
        Long id = tourDao.create(tour1);
        tour1.setId(id);

        Tour tour2 = TestData.getRecreationTour();
        tour2.setTitle(tour2.getTitle() + seed);
        id = tourDao.create(tour2);
        tour2.setId(id);

        User user = TestData.getUser();
        user.setUsername(user.getUsername() + seed);
        id = userDao.create(user);
        user.setId(id);

        return new UserTestData(tour1, tour2, user);
    }

    public static ReviewTestData getReviewTestData(UserDao userDao, TourDao tourDao,
                                                   PurchaseDao purchaseDao) {
        User user1 = new User();
        user1.setFirstName("u1");
        user1.setUsername("u1");

        User user2 = new User();
        user2.setFirstName("u2");
        user1.setUsername("u2");

        Long id = userDao.create(user1);
        user1.setId(id);
        id = userDao.create(user2);
        user2.setId(id);

        Tour tour = getExcursionTour();
        id = tourDao.create(tour);
        tour.setId(id);

        if (purchaseDao != null) {
            purchaseDao.create(new Purchase(user1, tour, BigDecimal.ZERO));
            purchaseDao.create(new Purchase(user2, tour, BigDecimal.ZERO));
        }

        return new ReviewTestData(tour, user1, user2);
    }

    public static Review getReview(User user1, Tour tour) {
        Review review = new Review();

        review.setAuthor(user1);
        review.setTour(tour);
        review.setRating(4);
        review.setText("Some text_"+ user1.getFirstName() + "_" + tour.getTitle());

        return review;
    }

    public static class ReviewTestData {
        public Tour tour;
        public User user1;
        public User user2;

        public ReviewTestData(Tour tour, User user1, User user2) {
            this.tour = tour;
            this.user1 = user1;
            this.user2 = user2;
        }
    }

    public static class UserTestData {
        public Tour tour1;
        public Tour tour2;
        public User user;

        public UserTestData(Tour tour1, Tour tour2, User user) {
            this.tour1 = tour1;
            this.tour2 = tour2;
            this.user = user;
        }
    }
}
