package com.sfedu.touragency.service;

import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.H2Db;
import com.sfedu.touragency.persistence.JdbcTemplate;
import com.sfedu.touragency.persistence.TestData;
import com.sfedu.touragency.persistence.dao.PurchaseDao;
import com.sfedu.touragency.persistence.dao.ReviewDao;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.persistence.dao.jdbc.PurchaseJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.ReviewJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.TourJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.UserJdbcDao;
import com.sfedu.touragency.service.impl.ReviewServiceImpl;
import com.sfedu.touragency.util.ResourcesUtil;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class TourServiceTest {
    private ConnectionManager connectionManager;

    private ReviewService reviewService;

    private TourDao tourDao;

    private UserDao userDao;

    private PurchaseDao purchaseDao;

    private TestData.ReviewTestData data;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.init("database.sql");
        ReviewDao reviewDao = new ReviewJdbcDao(connectionManager);
        tourDao = new TourJdbcDao(connectionManager);
        userDao = new UserJdbcDao(connectionManager);
        purchaseDao = new PurchaseJdbcDao(connectionManager);
        reviewService = new ReviewServiceImpl(reviewDao, userDao, tourDao,
                connectionManager);
        data = TestData.getReviewTestData(userDao, tourDao,
                new PurchaseJdbcDao(connectionManager));
    }

    @After
    public void tearDown() throws Exception {
        new JdbcTemplate(connectionManager).executeSqlFile(
                ResourcesUtil.getResourceFile("clear-database.sql"));
    }

    @Test
    public void testCreateRead() {
        Review review = TestData.getReview(data.user1, data.tour);
        reviewService.create(review);

        Review dbReview = reviewService.read(review.getId());
        assertWeakEquals(review, dbReview);

        Tour tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(4.0), tour.getAvgRating());
        assertEquals(1, tour.getVotesCount());
    }

    @Test
    public void testConsistency() {
        Review review1 = TestData.getReview(data.user1, data.tour);
        review1.setRating(4);
        Review review2 = TestData.getReview(data.user2, data.tour);
        review2.setRating(3);

        reviewService.create(review1);
        reviewService.create(review2);

        Tour tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(3.5), tour.getAvgRating());
        assertEquals(2, tour.getVotesCount());
    }

    @Test
    public void testUpdate() {
        Review review = TestData.getReview(data.user1, data.tour);
        reviewService.create(review);

        review.setRating(2);

        Tour tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(4.0), tour.getAvgRating());
        assertEquals(1, tour.getVotesCount());

        reviewService.update(review);

        Review dbReview = reviewService.read(review.getId());
        assertWeakEquals(review, dbReview);

        tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(2.0), tour.getAvgRating());
        assertEquals(1, tour.getVotesCount());
    }

    @Test
    public void testUpdateGivenTwoRecords() {
        Review review1 = TestData.getReview(data.user1, data.tour);
        Review review2 = TestData.getReview(data.user2, data.tour);
        review2.setRating(5);

        reviewService.create(review1);
        reviewService.create(review2);
        review1.setRating(2);

        Tour tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(4.5), tour.getAvgRating());
        assertEquals(2, tour.getVotesCount());

        review1.setRating(1);
        reviewService.update(review1);

        tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(3.0), tour.getAvgRating());
        assertEquals(2, tour.getVotesCount());
    }

    @Test
    public void testDeleteGivenOneRecord() {
        Review review = TestData.getReview(data.user1, data.tour);

        reviewService.create(review);

        Tour tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(4.0), tour.getAvgRating());
        assertEquals(1, tour.getVotesCount());

        reviewService.delete(review.getId());

        tour = tourDao.read(data.tour.getId());
        assertNull(tour.getAvgRating());
        assertEquals(0, tour.getVotesCount());
    }

    @Test
    public void testDeleteGivenTwoRecords() {
        Review review1 = TestData.getReview(data.user1, data.tour);

        reviewService.create(review1);

        Review review2 = TestData.getReview(data.user2, data.tour);
        review2.setRating(1);
        reviewService.create(review2);

        Tour tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(2.5), tour.getAvgRating());
        assertEquals(2, tour.getVotesCount());

        reviewService.delete(review1.getId());

        tour = tourDao.read(data.tour.getId());
        assertEquals(new Double(1), tour.getAvgRating());
        assertEquals(1, tour.getVotesCount());

        reviewService.delete(review2.getId());
        tour = tourDao.read(data.tour.getId());
        assertNull(tour.getAvgRating());
        assertEquals(0, tour.getVotesCount());
    }

    @Test
    public void testFindAll() {
        Review review1 = TestData.getReview(data.user1, data.tour);
        Review review2 = TestData.getReview(data.user2, data.tour);

        reviewService.create(review1);
        reviewService.create(review2);

        List<Review> reviews = reviewService.findAll();
        assertWeakEquals(review1, reviews.get(1));
        assertWeakEquals(review2, reviews.get(0));
    }

    private void assertWeakEquals(Review expected, Review actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getRating(), actual.getRating());
        assertEquals(expected.getAuthor().getId(), actual.getAuthor().getId());
        assertEquals(expected.getTour().getId(), actual.getTour().getId());
    }
}
