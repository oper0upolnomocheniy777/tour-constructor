package com.sfedu.touragency.persistence.query.condition;

import com.sfedu.touragency.persistence.query.BoolCondition;
import com.sfedu.touragency.persistence.query.Ordering;

public class RelationCondition implements BoolCondition {
    private final String value;
    private String col;
    private Ordering ord;

    public RelationCondition(String col, Ordering ord, Object value) {
        this.col = col;
        this.ord = ord;
        this.value = value.toString();
    }

    @Override
    public String getSQL() {
        return col + " " + ord.getSign() + " " + value;
    }
}
