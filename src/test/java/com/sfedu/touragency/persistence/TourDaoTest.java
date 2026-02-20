package com.sfedu.touragency.persistence;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.jdbc.TourJdbcDao;
import com.sfedu.touragency.util.ResourcesUtil;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class TourDaoTest {
    private ConnectionManager connectionManager;
    private TourDao tourDao;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.init("database.sql");
        tourDao = new TourJdbcDao(connectionManager);
    }

    @After
    public void tearDown() throws Exception {
        new JdbcTemplate(connectionManager).executeSqlFile(
                ResourcesUtil.getResourceFile("clear-database.sql"));
    }

    @Test
    public void testCreateReadById() {
        Tour tour1 = TestData.getExcursionTour();
        Long id = tourDao.create(tour1);

        assertNotNull(id);

        tour1.setId(id);
        Tour tour2 = tourDao.read(id);
        assertEquals(tour1, tour2);
    }

    @Test
    public void testUpdate() {
        Tour tour1 = TestData.getExcursionTour();

        Long id = tourDao.create(tour1);
        tour1.setId(id);

        String old = tour1.getDescription();
        tour1.setDescription("SOME DESCRIPTION");

        tourDao.update(tour1);

        Tour tour2 = tourDao.read(tour1.getId());
        assertEquals(tour1, tour2);
    }

    @Test
    public void testFindAll() {
        Tour tour1 = TestData.getExcursionTour();
        Tour tour2 = TestData.getShoppingTour();

        Long id1 = tourDao.create(tour1);
        Long id2 = tourDao.create(tour2);
        tour1.setId(id1);
        tour2.setId(id2);

        assertEquals(Arrays.asList(tour1, tour2), tourDao.findAll());
    }

    @Test
    public void testDelete() {
        Tour tour1 = TestData.getExcursionTour();
        Long id = tourDao.create(tour1);
        tourDao.delete(id);
        Tour tour2 = tourDao.read(id);

        assertNull(tour2);
    }

    @Test
    public void findRandomHotSingleton() {
        Tour tour = TestData.getExcursionTour();
        tour.setHot(true);

        Long id = tourDao.create(tour);
        tour.setId(id);

        Tour randomTour = tourDao.findRandomHot();
        assertEquals(tour, randomTour);
    }

    @Test
    public void findRandomNotHotSingleton() {
        Tour tour = TestData.getExcursionTour();

        Long id = tourDao.create(tour);
        tour.setId(id);

        Tour randomTour = tourDao.findRandomHot();
        assertNull(randomTour);
    }

    @Test
    public void findRandomHotOfTwo() {
        Tour tour1 = TestData.getExcursionTour();
        Tour tour2 = TestData.getExcursionTour();
        tour1.setHot(true);
        tour2.setHot(true);

        Long id = tourDao.create(tour1);
        tour1.setId(id);

        id = tourDao.create(tour2);
        tour2.setId(id);

        Tour randomTour = tourDao.findRandomHot();

        assertNotNull(randomTour);
        assertTrue(randomTour.equals(tour1) || randomTour.equals(tour2));
    }

    // TODO: disabled; getToursSliceByCriteria

}
