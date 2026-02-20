package com.sfedu.touragency.persistence.query.builder;

import com.sfedu.touragency.persistence.query.SelectQuery;

/**
 * QueryBuilder that represent limit part of the query
 *
 * No further manipulation can be invoked so contains only
 * a #build method which retrieves the query
 */
public class LimitBuilder extends QueryBuilder {

    LimitBuilder(SelectQuery query) {
        super(query);
    }

    public QueryBuilder limit(Integer limit) {
        return new QueryBuilder(query.setLimit(limit));
    }

    public QueryBuilder limit(Integer limit, Integer offset) {
        return new QueryBuilder(query.setLimit(limit, offset));
    }
}
