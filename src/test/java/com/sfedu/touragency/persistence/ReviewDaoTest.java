package com.sfedu.touragency.persistence;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.domain.Review;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.persistence.dao.PurchaseDao;
import com.sfedu.touragency.persistence.dao.ReviewDao;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.persistence.dao.jdbc.PurchaseJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.ReviewJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.TourJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.UserJdbcDao;
import com.sfedu.touragency.util.ResourcesUtil;
import org.junit.*;

import java.math.*;
import java.util.*;

import static org.junit.Assert.*;

public class ReviewDaoTest {
    private ConnectionManager connectionManager;

    private ReviewDao reviewDao;

    private TourDao tourDao;

    private UserDao userDao;

    private PurchaseDao purchaseDao;

    private TestData.ReviewTestData data;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.init("database.sql");
        reviewDao = new ReviewJdbcDao(connectionManager);
        tourDao = new TourJdbcDao(connectionManager);
        userDao = new UserJdbcDao(connectionManager);
        purchaseDao = new PurchaseJdbcDao(connectionManager);
        data = TestData.getReviewTestData(userDao, tourDao, purchaseDao);
    }

    @After
    public void tearDown() throws Exception {
        new JdbcTemplate(connectionManager).executeSqlFile(
                ResourcesUtil.getResourceFile("clear-database.sql"));
    }

    @Test
    public void testCreateRead() {
        Review review = TestData.getReview(data.user1, data.tour);
        Long id = reviewDao.create(review);

        assertNotNull(id);

        review.setId(id);

        Review dbReview = reviewDao.read(id);
        assertWeakEquals(review, dbReview);
    }

    @Test
    public void testUpdate() {
        Review review = TestData.getReview(data.user1, data.tour);
        Long id = reviewDao.create(review);
        assertNotNull(id);

        review.setId(id);
        review.setRating(2);

        reviewDao.update(review);

        Review dbReview = reviewDao.read(id);
        assertWeakEquals(review, dbReview);
    }

    @Test
    public void testFindAll() {
        Review review1 = TestData.getReview(data.user1, data.tour);
        Review review2 = TestData.getReview(data.user2, data.tour);

        Long id1 = reviewDao.create(review1);
        Long id2 = reviewDao.create(review2);

        assertNotNull(id1);
        assertNotNull(id2);

        review1.setId(id1);
        review2.setId(id2);

        List<Review> reviews = reviewDao.findAll();
        assertWeakEquals(review1, reviews.get(1));
        assertWeakEquals(review2, reviews.get(0));
    }

    @Test
    public void testFindById() {
        Tour anotherTour = TestData.getExcursionTour();
        Long tourId = tourDao.create(anotherTour);
        anotherTour.setId(tourId);

        Review review1 = TestData.getReview(data.user1, data.tour);
        Review review2 = TestData.getReview(data.user2, data.tour);
        Review review3 = TestData.getReview(data.user1, anotherTour);

        Long id1 = reviewDao.create(review1);
        Long id2 = reviewDao.create(review2);
        Long id3 = reviewDao.create(review3);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);

        review1.setId(id1);
        review2.setId(id2);
        review3.setId(id3);

        List<Review> reviews1 = reviewDao.findByTour(data.tour.getId());
        List<Review> reviews2 = reviewDao.findByTour(anotherTour.getId());

        assertEquals(2, reviews1.size());
        assertEquals(1, reviews2.size());

        assertWeakEquals(review2, reviews1.get(0));
        assertWeakEquals(review1, reviews1.get(1));
        assertWeakEquals(review3, reviews2.get(0));
    }

    @Test
    public void testCanVoteGivenPurchase() {
        Purchase purchase = new Purchase(data.user1, data.tour, new BigDecimal(0));
        purchaseDao.create(purchase);

        assertTrue(reviewDao.canVote(data.user1.getId(), data.tour.getId()));
    }

    @Test
    public void testCanVoteGivenReview() {
        Review review = TestData.getReview(data.user1, data.tour);
        reviewDao.create(review);

        assertFalse(reviewDao.canVote(data.user1.getId(), data.tour.getId()));
    }

    private static void assertWeakEquals(Review expected, Review actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getRating(), actual.getRating());
        assertEquals(expected.getAuthor().getId(), actual.getAuthor().getId());
        assertEquals(expected.getTour().getId(), actual.getTour().getId());
    }


}
