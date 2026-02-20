package com.sfedu.touragency.util;

/**
 * Helper class for abstracting of the underlying object fail status
 * @param <T>
 */
public class WithStatus<T> {
    private T payload;
    private boolean ok;

    private WithStatus(T payload, boolean ok) {
        this.payload = payload;
        this.ok = ok;
    }

    public T getPayload() {
        return payload;
    }

    public boolean isOk() {
        return ok;
    }

    public boolean isBad() {
        return !isOk();
    }

    public static <T> WithStatus<T> ok(T t) {
        return new WithStatus<T>(t, true);
    }

    public static <T> WithStatus<T> bad(T t) {
        return new WithStatus<T>(t, false);
    }
}
