package com.sfedu.touragency.persistence.query.condition;

import com.sfedu.touragency.persistence.query.BoolCondition;

public class InCondition implements BoolCondition {
    private final String col;

    private String[] values;

    private boolean quote;

    public InCondition(String col, boolean quote, String... values) {
        this.quote = quote;
        this.values = values;
        this.col = col;
    }

    public InCondition(String col, String... values) {
        this(col, false, values);
    }

    @Override
    public String getSQL() {
        return col + " in " + tuple();
    }

    private String tuple() {
        String[] escaped;
        if(quote) {
            escaped = new String [values.length];
            for (int i = 0; i < values.length; i++) {
                escaped[i] = "'" + values[i] + "'";
            }
        } else {
            escaped = values;
        }
        return "(" + String.join(",", escaped) + ")";
    }
}
