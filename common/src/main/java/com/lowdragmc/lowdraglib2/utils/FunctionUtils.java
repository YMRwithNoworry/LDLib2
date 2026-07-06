package com.lowdragmc.lowdraglib2.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FunctionUtils {
    private FunctionUtils() {
    }

    public static <T> Consumer<T> noopConsumer() {
        return ignored -> {
        };
    }

    public static <T> Supplier<T> nullSupplier() {
        return () -> null;
    }
}
