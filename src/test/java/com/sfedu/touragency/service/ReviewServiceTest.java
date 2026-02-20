package com.sfedu.touragency.service;

import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.TourType;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.H2Db;
import com.sfedu.touragency.persistence.JdbcTemplate;
import com.sfedu.touragency.persistence.TestData;
import com.sfedu.touragency.persistence.dao.ToursDynamicFilter;
import com.sfedu.touragency.persistence.dao.jdbc.TourJdbcDao;
import com.sfedu.touragency.service.impl.TourServiceImpl;
import com.sfedu.touragency.util.ResourcesUtil;
import org.junit.*;

import java.math.*;
import java.util.*;

import static org.junit.Assert.*;

public class ReviewServiceTest {
    private ConnectionManager connectionManager;

    private TourService tourService;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.init("database.sql");
        tourService = new TourServiceImpl(new TourJdbcDao(connectionManager));
    }

    @After
    public void tearDown() throws Exception {
        new JdbcTemplate(connectionManager).executeSqlFile(
                ResourcesUtil.getResourceFile("clear-database.sql"));
    }

    @Test
    public void testCreateReadById() {
        Tour tour1 = TestData.getExcursionTour();
        tourService.create(tour1);

        Tour tour2 = tourService.read(tour1.getId());
        assertEquals(tour1, tour2);
    }

    @Test
    public void testUpdate() {
        Tour tour1 = TestData.getExcursionTour();

        tourService.create(tour1);

        String old = tour1.getDescription();
        tour1.setDescription("SOME DESCRIPTION");

        tourService.update(tour1);

        Tour tour2 = tourService.read(tour1.getId());
        assertEquals(tour1, tour2);
    }

    @Test
    public void testFindAll() {
        Tour tour1 = TestData.getExcursionTour();
        Tour tour2 = TestData.getShoppingTour();

        tourService.create(tour1);
        tourService.create(tour2);

        assertEquals(Arrays.asList(tour1, tour2), tourService.findAll());
    }

    @Test
    public void testFindRandomTours() {
        Tour tour1 = TestData.getExcursionTour();
        tour1.setHot(true);
        Tour tour2 = TestData.getShoppingTour();

        tourService.create(tour1);
        tourService.create(tour2);

        Set<Tour> hots = tourService.findRandomHotTours(1);
        assertEquals(1, hots.size());
        assertTrue(hots.contains(tour1));
    }

    @Test
    public void testToggleEnabled() {
        Tour tour = TestData.getExcursionTour();
        tour.setHot(true);
        tourService.create(tour);
        tourService.toggleEnabled(tour.getId());

        Tour dbTour = tourService.read(tour.getId());
        tour.setHot(false);
        assertEquals(tour, dbTour);
    }

    @Test
    public void testExecuteDynamicFilter() {
        ToursDynamicFilter filter = new ToursDynamicFilter();
        filter.setPriceLow(10);
        filter.setTourTypes(TourType.RECREATION);

        Tour tour1 = TestData.getRecreationTour();
        tour1.setPrice(new BigDecimal(1));
        Tour tour2 = TestData.getExcursionTour();
        tour2.setPrice(new BigDecimal(10));
        Tour tour3 = TestData.getRecreationTour();
        tour3.setPrice(new BigDecimal(100));

        tourService.create(tour1);
        tourService.create(tour2);
        tourService.create(tour3);

        List<Tour> tours = tourService.executeDynamicFilter(filter);
        assertEquals(Arrays.asList(tour3), tours);
    }

    @Test
    public void testFindRandomToursWhenLessThenNeeded() {
        Tour tour1 = TestData.getExcursionTour();
        tour1.setHot(true);
        Tour tour2 = TestData.getShoppingTour();

        tourService.create(tour1);
        tourService.create(tour2);

        Set<Tour> hots = tourService.findRandomHotTours(42);
        assertEquals(1, hots.size());
        assertTrue(hots.contains(tour1));
    }
    @Test
    public void testDelete() {
        Tour tour1 = TestData.getExcursionTour();
        tourService.create(tour1);
        tourService.delete(tour1.getId());
        Tour tour2 = tourService.read(tour1.getId());

        assertNull(tour2);
    }

}
