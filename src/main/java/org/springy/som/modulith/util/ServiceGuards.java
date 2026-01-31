package org.springy.som.modulith.util;

import org.springframework.util.StringUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ServiceGuards {
    private ServiceGuards() {}

    public static <X extends RuntimeException> void requireText(String value, Supplier<X> exceptionSupplier) {
        if (!StringUtils.hasText(value)) {
            throw exceptionSupplier.get();
        }
    }

    public static <T, X extends RuntimeException> void requireNonNull(T value, Supplier<X> exceptionSupplier) {
        if (value == null) {
            throw exceptionSupplier.get();
        }
    }

    public static <T, X extends RuntimeException> void requireEntityWithId(T entity,
                                                                           Function<T, String> idGetter,
                                                                           Supplier<X> nullEntityException,
                                                                           Supplier<X> missingIdException) {
        requireNonNull(entity, nullEntityException);
        String id = idGetter.apply(entity);
        requireText(id, missingIdException);
    }

    public static <T> String safeId(T entity, Function<T, String> idGetter) {
        try {
            return entity == null ? null : idGetter.apply(entity);
        } catch (Exception ignored) {
            return null;
        }
    }
}
