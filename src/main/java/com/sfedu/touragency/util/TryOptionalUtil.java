package com.sfedu.touragency.util;

import java.util.*;

public class TryOptionalUtil {
    /**
     * Helper methods for wrapping the result of an operation that is accepted to fail
     */
    public static <R> Optional<R> of(FailableSupplier<R> f) {
        try {
            return Optional.ofNullable(f.get());
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    public interface FailableSupplier<R> {
        R get() throws Throwable;
    }
}
