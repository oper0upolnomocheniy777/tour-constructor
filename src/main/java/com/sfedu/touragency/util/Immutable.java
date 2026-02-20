package com.sfedu.touragency.util;

import java.util.*;

/**
 * Holds static helper methods for immutable operations
 */
public class Immutable {

    /**
     * Merges to iterable into one List
     */
    public static <E> List<E> cons(Iterable<E>... iters) {
        List<E> list = new ArrayList<E>();
        for(Iterable<E> iter: iters) {
            if(iter != null) {
                for (E e1 : iter) list.add(e1);
            }
        }
        return list;
    }
}
