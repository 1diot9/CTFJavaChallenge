package org.springframework.util.function;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/function/ThrowingSupplier.class */
public interface ThrowingSupplier<T> extends Supplier<T> {
    T getWithException() throws Exception;

    @Override // java.util.function.Supplier
    default T get() {
        return get((v1, v2) -> {
            return new RuntimeException(v1, v2);
        });
    }

    default T get(BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        try {
            return getWithException();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw exceptionWrapper.apply(ex2.getMessage(), ex2);
        }
    }

    default ThrowingSupplier<T> throwing(final BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return new ThrowingSupplier<T>() { // from class: org.springframework.util.function.ThrowingSupplier.1
            @Override // org.springframework.util.function.ThrowingSupplier
            public T getWithException() throws Exception {
                return (T) ThrowingSupplier.this.getWithException();
            }

            @Override // org.springframework.util.function.ThrowingSupplier, java.util.function.Supplier
            public T get() {
                return get(exceptionWrapper);
            }
        };
    }

    static <T> ThrowingSupplier<T> of(ThrowingSupplier<T> supplier) {
        return supplier;
    }

    static <T> ThrowingSupplier<T> of(ThrowingSupplier<T> supplier, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return supplier.throwing(exceptionWrapper);
    }
}
