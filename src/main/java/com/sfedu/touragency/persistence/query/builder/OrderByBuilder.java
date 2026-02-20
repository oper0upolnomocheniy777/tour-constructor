package com.sfedu.touragency.persistence.query.builder;

import com.sfedu.touragency.persistence.query.SelectQuery;
import com.sfedu.touragency.persistence.query.condition.OrderByCondition;
import com.sfedu.touragency.util.SortDir;

import java.util.*;

public class OrderByBuilder extends LimitBuilder {
    OrderByBuilder(SelectQuery query) {
        super(query);
    }

    public OrderByBuilder orderBy(OrderByCondition... conditions) {
        return new OrderByBuilder(query.orderBy(Arrays.asList(conditions)));
    }

    public OrderByBuilder orderBy(String col, SortDir sortDir) {
        return orderBy(new OrderByCondition(col, sortDir));
    }
}
