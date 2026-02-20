package com.sfedu.touragency.persistence.transaction;

import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.H2Db;
import com.sfedu.touragency.persistence.JdbcTemplate;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class TransactionTest {
    private static final String SQL_SELECT_ALL = "SELECT * FROM test";

    private ConnectionManager connectionManager;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        connectionManager = H2Db.initWithTx("test.sql");
        jdbcTemplate = new JdbcTemplate(connectionManager);
    }

    @Test
    public void testTxImplicitRollbackSecondOp() {
        try {
            Transaction.tx(connectionManager, () -> {
                JdbcTemplate txTemplate = new JdbcTemplate(connectionManager);
                txTemplate.startTransaction();

                Long id1 = txTemplate.insert("INSERT INTO test(col) VALUES(?)", "alpha");
                assertEquals(Long.valueOf(4), id1);

                Long id2 = txTemplate.insert("INSERT INTO test(col, id) VALUES(?, ?)", null, 1);

                txTemplate.commit();
            });
        } catch (Exception e) {}

        List<String> cols = jdbcTemplate.queryObjects(SQL_SELECT_ALL,
                rs -> rs.getString("col"));
        assertEquals(Arrays.asList("x", "y", "z"), cols);
    }

    @Test
    public void testTxImplicitRollbackFirstOp() {
        try {
            Transaction.tx(connectionManager, () -> {
                JdbcTemplate txTemplate = new JdbcTemplate(connectionManager);
                txTemplate.startTransaction();

                txTemplate.insert("INSERT INTO test(col, id) VALUES(?, ?)", null, 1);
                txTemplate.insert("INSERT INTO test(col) VALUES(?)", "alpha");
            });
        } catch (Exception e) {}

        List<String> cols = jdbcTemplate.queryObjects(SQL_SELECT_ALL, rs -> rs.getString("col")
        );
//        assertEquals(Arrays.asList("x", "y", "z"), cols);
    }

    @Test
    public void testTxImplicitRollbackFirstOpWithoutExplicitJdbcTemplateTransaction() {
        try {
            Transaction.tx(connectionManager, () -> {
                JdbcTemplate txTemplate = new JdbcTemplate(connectionManager);

                Long id1 = txTemplate.insert("INSERT INTO test(col, id) VALUES(?, ?)", null, 1);
                Long id2 = txTemplate.insert("INSERT INTO test(col) VALUES(?)", "alpha");
            });
        } catch (Exception e) {}

        List<String> cols = jdbcTemplate.queryObjects(SQL_SELECT_ALL,
                rs -> rs.getString("col"));
        assertEquals(Arrays.asList("x", "y", "z"), cols);
    }

    @Test
    public void testTxExplicitRollback() {
        Transaction.tx(connectionManager, () -> {
            JdbcTemplate txTemplate = new JdbcTemplate(connectionManager);
            txTemplate.startTransaction();

            txTemplate.insert("INSERT INTO test(col) VALUES(?)", "alpha");

            txTemplate.rollback();
            txTemplate.insert("INSERT INTO test(col) VALUES(?)", "beta");
            txTemplate.commit();
        });

        List<String> cols = jdbcTemplate.queryObjects(SQL_SELECT_ALL,
                rs -> rs.getString("col"));
        assertEquals(Arrays.asList("x", "y", "z", "beta"), cols);
    }

    @Test
    public void testNestedTransactionOk() {
        Transaction.tx(connectionManager, () -> {
            JdbcTemplate txTemplate = new JdbcTemplate(connectionManager);
            txTemplate.startTransaction();

             txTemplate.insert("INSERT INTO test(col) VALUES(?)", "alpha");

            Transaction.tx(connectionManager, () -> {
                txTemplate.insert("INSERT INTO test(col) VALUES(?)", "beta");
            });
        });

        List<String> cols = jdbcTemplate.queryObjects(SQL_SELECT_ALL,
                rs -> rs.getString("col"));
        assertEquals(Arrays.asList("x", "y", "z","alpha", "beta"), cols);
    }

    @Test
    public void testNestedTransactionWhenNestedFails() {
        connectionManager.clean();
        try {
            Transaction.tx(connectionManager, () -> {
                JdbcTemplate txTemplate = new JdbcTemplate(connectionManager);

                txTemplate.insert("INSERT INTO test(col) VALUES(?)", "alpha");

                Transaction.tx(connectionManager, () -> {
                    txTemplate.insert("INSERT INTO test(col) VALUES(?, ?)", "beta");
                });
            });
        } catch (Exception e) {}

        List<String> cols = jdbcTemplate.queryObjects(SQL_SELECT_ALL,
                rs -> rs.getString("col"));
        assertEquals(Arrays.asList("x", "y", "z"), cols);
    }

    @Test
    public void testNestedTransactionWhenOuterFails() {
        try {
            Transaction.tx(connectionManager, () -> {
                JdbcTemplate txTemplate = new JdbcTemplate(connectionManager);
                txTemplate.startTransaction();

                txTemplate.insert("INSERT INTO test(col) VALUES(?, ?)", "alpha");

                Transaction.tx(connectionManager, () -> {
                    txTemplate.insert("INSERT INTO test(col) VALUES(?)", "beta");
                });
            });
        } catch (Exception e) {}

        List<String> cols = jdbcTemplate.queryObjects(SQL_SELECT_ALL,
                rs -> rs.getString("col"));
        assertEquals(Arrays.asList("x", "y", "z"), cols);
    }
}
