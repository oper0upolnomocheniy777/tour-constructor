package com.sfedu.touragency.persistence.query;


import com.sfedu.touragency.persistence.query.condition.OrderByCondition;
import com.sfedu.touragency.persistence.query.condition.RelationCondition;
import org.junit.*;

import static com.sfedu.touragency.persistence.query.Ordering.*;
import static com.sfedu.touragency.util.SortDir.*;
import static org.junit.Assert.*;

public class SelectQueryTest {
    @Test
    public void testConstructor() {
        SelectQuery query = new SelectQuery("test");
        assertEquals("SELECT * FROM test".toLowerCase(), query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testSetTable() {
        SelectQuery query = new SelectQuery("test");
        query = query.setTable("xyz");
        assertEquals("SELECT * FROM xyz".toLowerCase(), query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testSetColumns() {
        SelectQuery query = new SelectQuery("test");
        query = query.setColumns("col");
        assertEquals("SELECT col FROM test".toLowerCase(), query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testSetLimit() {
        SelectQuery query = new SelectQuery("test");
        query = query.setLimit(1);
        assertEquals("SELECT * FROM test LIMIT 1".toLowerCase(),
                query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testOrderBy() {
        SelectQuery query = new SelectQuery("test");
        query = query.orderBy(new OrderByCondition("col1", ASC));
        assertEquals("SELECT * FROM test ORDER BY col1 ASC".toLowerCase(),
                query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testMultipleOrderBy() {
        SelectQuery query = new SelectQuery("test");
        query = query.orderBy(new OrderByCondition("col1", DESC))
                     .orderBy(new OrderByCondition("col2", ASC));
        assertEquals("SELECT * FROM test ORDER BY col1 DESC, col2 ASC".toLowerCase(),
                query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testWhere() {
        SelectQuery query = new SelectQuery("test");
        query = query.where(new RelationCondition("col", GREATER, "1"));
        assertEquals("SELECT * FROM test WHERE col > 1".toLowerCase(),
                query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testMultipleWhere() {
        SelectQuery query = new SelectQuery("test");
        query = query.where(new RelationCondition("col", GREATER, "1"))
                     .where(new RelationCondition("x", LESS, "y"));
        assertEquals("SELECT * FROM test WHERE x < y".toLowerCase(),
                query.getSQL().trim().toLowerCase());
    }

    @Test
    public void testComplexQuery() {
        SelectQuery query = new SelectQuery("test");
        query = query.setTable("user")
                     .where(new RelationCondition("id", EQ, 42))
                     .setColumns("username")
                     .setLimit(1)
                     .orderBy(new OrderByCondition("username", ASC));
        assertEquals(("SELECT username FROM user WHERE id = 42 ORDER BY username ASC LIMIT 1")
                .toLowerCase(), query.getSQL().replaceAll("\\s+", " ").toLowerCase());
    }


}
