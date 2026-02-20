package com.sfedu.touragency.persistence;

import com.sfedu.touragency.domain.Purchase;
import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.persistence.dao.PurchaseDao;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.persistence.dao.jdbc.PurchaseJdbcDao;
import com.sfedu.touragency.persistence.dao.jdbc.UserJdbcDao;
import com.sfedu.touragency.util.ResourcesUtil;
import org.junit.*;

import java.math.*;
import java.util.*;

import static org.junit.Assert.*;

public class UserDaoTest {
    private ConnectionManager connectionManager;
    private UserDao userDao;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.init("database.sql");
        userDao = new UserJdbcDao(connectionManager);
    }

    @After
    public void tearDown() throws Exception {
        new JdbcTemplate(connectionManager).executeSqlFile(
                ResourcesUtil.getResourceFile("clear-database.sql"));
    }

    @Test
    public void testCreateReadById() {
        User user1 = TestData.getUser();
        Long id = userDao.create(user1);

        assertNotNull(id);

        user1.setId(id);
        User user2 = userDao.read(id);
        assertEquals(user1, user2);
    }

    @Test
    public void testCreateReadManyRoles() {
        User user1 = TestData.getPrivilegedUser();
        Long id = userDao.create(user1);

        assertNotNull(id);

        user1.setId(id);
        User user2 = userDao.read(user1.getId());
        assertEquals(user1, user2);
    }

    @Test
    public void testCreateReadByUsername() {
        User user1 = TestData.getPrivilegedUser();
        Long id = userDao.create(user1);

        assertNotNull(id);

        user1.setId(id);
        User user2 = userDao.read(user1.getUsername());
        assertEquals(user1, user2);
    }

    @Test
    public void testUpdate() {
        User user1 = TestData.getPrivilegedUser();

        Long id = userDao.create(user1);
        user1.setId(id);

        user1.setFirstName("Joe");

        userDao.update(user1);

        User user2 = userDao.read(user1.getId());
        assertEquals(user1, user2);
    }

    @Test
    public void testAddRoles() {
        User user1 = TestData.getUser();
        Long id = userDao.create(user1);
        user1.setId(id);

        userDao.addRoles(id, Arrays.asList(Role.TOUR_AGENT));

        User user2 = userDao.read(id);
        assertEquals(Arrays.asList(Role.CUSTOMER, Role.TOUR_AGENT), user2.getRoles());
    }

    @Test
    public void testDeleteRoles() {
        User user1 = TestData.getPrivilegedUser();
        Long id = userDao.create(user1);
        user1.setId(id);

        userDao.deleteRoles(id);

        User user2 = userDao.read(id);
        assertTrue(user2.getRoles().isEmpty());
    }

    @Test
    public void testDelete() {
        User user1 = TestData.getUser();
        Long id = userDao.create(user1);
        userDao.delete(id);
        User user2 = userDao.read(id);

        assertNull(user2);
    }

    @Test
    public void testUpdateRoles() {
        User user1 = TestData.getPrivilegedUser();
        Long id = userDao.create(user1);
        user1.setId(id);

        userDao.updateRoles(id, Arrays.asList(Role.CUSTOMER));

        User user2 = userDao.read(id);
        assertEquals(Arrays.asList(Role.CUSTOMER), user2.getRoles());
    }

    @Test
    public void testReadRoles() {
        User user = TestData.getPrivilegedUser();
        Long id = userDao.create(user);

        assertEquals(Arrays.asList(Role.CUSTOMER, Role.TOUR_AGENT), userDao.readRoles(id));
    }

    @Test
    public void testFindAll() {
        User user1 = TestData.getUser();
        User user2 = TestData.getPrivilegedUser();

        Long id1 = userDao.create(user1);
        Long id2 = userDao.create(user2);
        user1.setId(id1);
        user2.setId(id2);

        assertEquals(Arrays.asList(user1, user2), userDao.findAll());
    }

    @Test
    public void testCountPurchases() {
        PurchaseDao purchaseDao = new PurchaseJdbcDao(connectionManager);
        TestData.UserTestData data = TestData.getUserTestData(connectionManager);

        purchaseDao.create(new Purchase(data.user, data.tour1, null));
        purchaseDao.create(new Purchase(data.user, data.tour2, null));

        assertEquals(2, userDao.countPurchases(data.user.getId()));
    }

    @Test
    public void testComputePurchasesTotalPrice() {
        PurchaseDao purchaseDao = new PurchaseJdbcDao(connectionManager);
        TestData.UserTestData data = TestData.getUserTestData(connectionManager);

        purchaseDao.create(new Purchase(data.user, data.tour1, new BigDecimal(80)));
        purchaseDao.create(new Purchase(data.user, data.tour2, new BigDecimal(40)));

        assertEquals(120, userDao.computePurchasesTotalPrice(data.user.getId()).intValue());
    }


}
