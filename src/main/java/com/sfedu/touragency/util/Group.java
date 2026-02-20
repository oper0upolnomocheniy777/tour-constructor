package com.sfedu.touragency.util;

import java.util.*;

/**
 * A helper tuple class for representing grouped data
 */
public class Group<K, E> {
    private K key;
    private Collection<E> elems;

    public Group(K key, Collection<E> elems) {
        this.key = key;
        this.elems = elems;
    }

    public Collection<E> getElems() {
        return elems;
    }

    public void setElems(Collection<E> elems) {
        this.elems = elems;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }
}
