package com.sfedu.touragency.persistence.query.condition;

import com.sfedu.touragency.persistence.query.BoolCondition;
import com.sfedu.touragency.persistence.query.Condition;

public class AndCondition implements BoolCondition {
    private Condition left;
    private Condition right;

    public AndCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String getSQL() {
        return String.format("(%s) AND (%s)", left.getSQL(), right.getSQL());
    }
}
