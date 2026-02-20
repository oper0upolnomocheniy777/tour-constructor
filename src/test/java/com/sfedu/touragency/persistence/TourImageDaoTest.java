package com.sfedu.touragency.persistence;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.TourImage;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.TourImageDao;
import com.sfedu.touragency.persistence.dao.jdbc.TourImageJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.TourJdbcDao;
import com.sfedu.touragency.util.ResourcesUtil;
import org.junit.*;

import java.net.*;
import java.util.*;

import static org.junit.Assert.*;

public class TourImageDaoTest {
    private ConnectionManager connectionManager;
    private TourImageDao tourImageDao;
    private TourDao tourDao;
    private Tour tour;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.init("database.sql");
        tourImageDao = new TourImageJdbcDao(connectionManager);
        tourDao = new TourJdbcDao(connectionManager);

        tour = TestData.getExcursionTour();
        Long id = tourDao.create(tour);
        tour.setId(id);
    }

    @After
    public void tearDown() throws Exception {
        new JdbcTemplate(connectionManager).executeSqlFile(
                ResourcesUtil.getResourceFile("clear-database.sql"));
    }

    @Test
    public void testCreateRead() {
        TourImage tourImage = TestData.getTourImage(tour);

        Long id = tourImageDao.create(tourImage);

        assertNotNull(id);
        tourImage.setId(id);

        TourImage dbTourImage = tourImageDao.read(id);

        assertWeakEquals(tourImage, dbTourImage);
    }

    @Test
    public void testUpdate() throws MalformedURLException {
        TourImage tourImage = TestData.getTourImage(tour);

        Long id = tourImageDao.create(tourImage);

        assertNotNull(id);
        tourImage.setId(id);

        tourImage.setImageUrl(new URL("http://128"));

        tourImageDao.update(tourImage);

        TourImage dbTourImage = tourImageDao.read(id);

        assertWeakEquals(tourImage, dbTourImage);
    }

    @Test
    public void testFindAll() {
        TourImage tourImage1 = TestData.getTourImage(tour);
        TourImage tourImage2 = TestData.getTourImage(tour);
        tourImage2.setThumbnailUrl(null);

        Long id = tourImageDao.create(tourImage1);
        tourImage1.setId(id);
        id = tourImageDao.create(tourImage2);
        tourImage2.setId(id);

        List<TourImage> tourImages = tourImageDao.findAll();

        assertEquals(2, tourImages.size());
        assertWeakEquals(tourImage1, tourImages.get(0));
        assertWeakEquals(tourImage2, tourImages.get(1));
    }

    @Test
    public void findByTour() {
        TourImage tourImage1 = TestData.getTourImage(tour);
        TourImage tourImage2 = TestData.getTourImage(tour);
        Tour anotherTour = TestData.getExcursionTour();
        Long id = tourDao.create(anotherTour);
        anotherTour.setId(id);
        TourImage tourImage3 = TestData.getTourImage(anotherTour);

        id = tourImageDao.create(tourImage1);
        assertNotNull(id);
        tourImage1.setId(id);

        id = tourImageDao.create(tourImage2);
        assertNotNull(id);
        tourImage2.setId(id);

        id = tourImageDao.create(tourImage3);
        assertNotNull(id);

        List<TourImage> tours = tourImageDao.findByTour(tour.getId());
        assertEquals(2, tours.size());
        assertWeakEquals(tourImage1, tours.get(0));
        assertWeakEquals(tourImage2, tours.get(1));
    }

    @Test
    public void testDelete() {
        TourImage tourImage = TestData.getTourImage(tour);

        Long id = tourImageDao.create(tourImage);
        tourImage.setId(id);

        tourImageDao.delete(id);

        TourImage dbTourImage = tourImageDao.read(id);

        assertNull(dbTourImage);
    }

    private void assertWeakEquals(TourImage expected, TourImage actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTour().getId(), actual.getTour().getId());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
        assertEquals(expected.getThumbnailUrl(), actual.getThumbnailUrl());
    }


}
