package com.sfedu.touragency.persistence;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.persistence.dao.PurchaseDao;
import com.sfedu.touragency.persistence.dao.jdbc.PurchaseJdbcDao;
import com.sfedu.touragency.util.ResourcesUtil;
import org.junit.*;

import java.math.*;
import java.util.*;

import static org.junit.Assert.*;

public class PurchaseDaoTest {
    private ConnectionManager connectionManager;
    private PurchaseDao purchaseDao;
    private TestData.UserTestData data;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.init("database.sql");
        purchaseDao = new PurchaseJdbcDao(connectionManager);
        data = TestData.getUserTestData(connectionManager);
    }

    @After
    public void tearDown() throws Exception {
        new JdbcTemplate(connectionManager).executeSqlFile(
                ResourcesUtil.getResourceFile("clear-database.sql"));
    }

    @Test
    public void testCreateReadById() {
        Purchase purchase1 = new Purchase(data.user, data.tour1, null);
        Long id = purchaseDao.create(purchase1);
        purchase1.setId(id);

        Purchase purchase2 = purchaseDao.read(id);

        assertWeakEquals(purchase1, purchase2);
    }


    @Test
    public void testUpdate() {
        Purchase purchase1 = new Purchase(data.user, data.tour1, null);
        Long id = purchaseDao.create(purchase1);
        purchase1.setId(id);

        purchase1.getPrice();
        purchase1.setPrice(new BigDecimal(42));
        purchaseDao.update(purchase1);

        Purchase purchase2 = purchaseDao.read(id);
        assertWeakEquals(purchase1, purchase2);
    }

    @Test
    public void testFindAll() {
        Purchase purchase1 = new Purchase(data.user, data.tour1, null);
        Purchase purchase2 = new Purchase(data.user, data.tour2, null);

        Long id = purchaseDao.create(purchase1);
        purchase1.setId(id);
        id = purchaseDao.create(purchase2);
        purchase2.setId(id);

        List<Purchase> purchases = purchaseDao.findAll();
        assertEquals(2, purchases.size());
        assertWeakEquals(purchase1, purchases.get(1));
        assertWeakEquals(purchase2, purchases.get(0));
    }

    @Test
    public void testDelete() {
        Purchase purchase = new Purchase(data.user, data.tour1, null);
        Long id = purchaseDao.create(purchase);

        purchaseDao.delete(id);
        Purchase dbPurchase = purchaseDao.read(id);

        assertNull(dbPurchase);
    }

//    @Test
//    public void testDeepen() {
//        Purchase purchase = new Purchase(data.user, data.tour1, BigDecimal.ZERO);
//        Long id = purchaseDao.create(purchase);
//        purchase.setId(id);
//
//        Purchase dbPurchase = purchaseDao.read(id);
//        purchaseDao.deepen(dbPurchase);
//
//        dbPurchase.setDate(null);
//        purchase.getUser().setRoles(Collections.EMPTY_LIST);
//
//        assertEquals(purchase, dbPurchase);
//    }

    @Test
    public void testFindByUser() {
        TestData.UserTestData anotherData =
                TestData.getUserTestData(connectionManager, "_2");

        Purchase purchase11 = new Purchase(data.user, data.tour1, null);
        Purchase purchase12 = new Purchase(data.user, data.tour2, null);
        Purchase purchase21 = new Purchase(anotherData.user, anotherData.tour1, null);
        Purchase purchase22 = new Purchase(anotherData.user, anotherData.tour2, null);

        Long id = purchaseDao.create(purchase11);
        purchase11.setId(id);
        id = purchaseDao.create(purchase12);
        purchase12.setId(id);
        id = purchaseDao.create(purchase21);
        purchase12.setId(id);
        id = purchaseDao.create(purchase22);
        purchase12.setId(id);

        List<Purchase> purchases = purchaseDao.findByUser(data.user.getId());
        assertEquals(2, purchases.size());
        assertWeakEquals(purchase11, purchases.get(0));
        assertWeakEquals(purchase12, purchases.get(1));

        purchases = purchaseDao.findByUser(anotherData.user.getId());
        assertEquals(2, purchases.size());
        assertWeakEquals(purchase21, purchases.get(0));
        assertWeakEquals(purchase22, purchases.get(1));
    }

    private static void assertWeakEquals(Purchase purchase1, Purchase purchase2) {
        assertEquals(purchase1.getTour().getId(), purchase2.getTour().getId());
        assertEquals(purchase1.getUser().getId(), purchase2.getUser().getId());

        if(purchase1.getPrice() != null)
            assertEquals(0, purchase1.getPrice().compareTo(purchase2.getPrice()));
    }
}
