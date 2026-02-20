package com.sfedu.touragency.persistence.query;

public enum Ordering {
    LESS("<"), EQ("="), GREATER(">"), NEQ("<>"), LESSEQ("<="), GREATEREQ(">=");

    private final String sign;

    Ordering(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
