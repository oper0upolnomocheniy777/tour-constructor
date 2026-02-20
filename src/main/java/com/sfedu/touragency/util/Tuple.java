package com.sfedu.touragency.util;

/**
 * A 2-tuple class for representing two values of different type
 */
public class Tuple<K, V> {
    private K first;
    private V second;

    public Tuple(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}
