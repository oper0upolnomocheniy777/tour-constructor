package com.sfedu.touragency.persistence.query;

import com.sfedu.touragency.persistence.query.builder.QueryBuilder;
import com.sfedu.touragency.util.SortDir;
import org.junit.*;

import static com.sfedu.touragency.persistence.query.Ordering.*;
import static com.sfedu.touragency.persistence.query.builder.WhereBuilder.*;
import static org.junit.Assert.*;

public class QueryBuilderTest {
    @Test
    public void testSimpleSelect() {
        QueryBuilder builder = QueryBuilder.select("test");
        assertFuzzyEquals("SELECT * FROM test", builder.build());
    }

    @Test
    public void testSimpleWhere() {
        QueryBuilder builder = QueryBuilder.select("test").where(rel("col", GREATER, 1));
        assertFuzzyEquals("SELECT * FROM test WHERE col > 1", builder.build());
    }

    @Test
    public void testCompositeWhere() {
        QueryBuilder builder = QueryBuilder.select("test").and(rel("col", GREATER, 1))
                .and(rel("col", Ordering.NEQ, 3));
        assertFuzzyEquals("SELECT * FROM test WHERE (col > 1) AND (col <> 3)",
                builder.build());
    }

    @Test
    public void testLimit() {
        QueryBuilder builder = QueryBuilder.select("test").limit(1);
        assertFuzzyEquals("SELECT * FROM test LIMIT 1", builder.build());
    }

    @Test
    public void testSimpleOrderBy() {
        QueryBuilder builder = QueryBuilder.select("test").orderBy("col", SortDir.DESC);
        assertFuzzyEquals("SELECT * FROM test ORDER BY col DESC", builder.build());
    }

    @Test
    public void testCompositeOrderBy() {
        QueryBuilder builder = QueryBuilder.select("test").orderBy("col1", SortDir.DESC)
                .orderBy("col2", SortDir.ASC);
        assertFuzzyEquals("SELECT * FROM test ORDER BY col1 DESC, col2 ASC",
                builder.build());
    }

    @Test
    public void testWhereLimit() {
        QueryBuilder builder = QueryBuilder.select("test").and(rel("col1", LESSEQ, 10))
                .and(rel("col2", GREATEREQ, 20)).limit(1);
        assertFuzzyEquals("SELECT * FROM test WHERE (col1 <= 10) AND (col2 >= 20) LIMIT 1",
                builder.build());
    }

    @Test
    public void testFullQuery() {
        QueryBuilder builder = QueryBuilder.select("test").and(rel("col1", LESSEQ, 10))
                .and(rel("col2", LESSEQ, 20)).orderBy("col1", SortDir.ASC)
                .orderBy("col2", SortDir.DESC).limit(1);
        assertFuzzyEquals("SELECT * FROM test WHERE (col1 <= 10) AND (col2 <= 20) " +
                "ORDER BY col1 ASC, col2 DESC LIMIT 1", builder.build());
    }

    @Test
    public void testWhereInCondition() {
        QueryBuilder builder = QueryBuilder.select("test")
                .and(rel("col1", LESSEQ, 10))
                .and(in("col2", false, "1", "2", "3"));
        assertFuzzyEquals("SELECT * FROM test WHERE (col1 <= 10) AND (col2 IN (1,2,3))",
                builder.build());
    }

    private static void assertFuzzyEquals(String expected, String actual) {
        assertEquals(expected.toLowerCase(), actual.replaceAll("\\s+", " ").trim().toLowerCase());
    }
}
