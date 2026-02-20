package com.sfedu.touragency.persistence.query.condition;

import com.sfedu.touragency.persistence.query.BoolCondition;

public class LikeCondition implements BoolCondition {
    private final String col;

    private String term;

    public LikeCondition(String col, String term) {
        this.col = col;
        this.term = term;
    }

    private String escapedTerm() {
//        if (term.replace("%", "").equals("?")) {
            return term;
//        }

//        return "'" + term + "'";
    }

    @Override
    public String getSQL() {
        return col + " LIKE " + escapedTerm();
    }
}
