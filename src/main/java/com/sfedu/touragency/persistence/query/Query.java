package com.sfedu.touragency.persistence.query;

/**
 * This interface represent an object that can be turned
 * int a SQL query
 *
 * @see SelectQuery
 */
public interface  Query {
    String getSQL();
}
