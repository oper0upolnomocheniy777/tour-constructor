package com.sfedu.touragency.util;

/**
 * Represent ordering relationship between elements
 * Used for specifying order by clause in various DAO methods
 */
public enum SortDir {
    ASC("ASC", "<"), DESC("DESC", ">");

    private final String name;
    private final String rel;

    SortDir(String name, String rel) {
        this.name = name;
        this.rel = rel;
    }

    public String getName() {
        return name;
    }

    public String getRel() {
        return rel;
    }
}
