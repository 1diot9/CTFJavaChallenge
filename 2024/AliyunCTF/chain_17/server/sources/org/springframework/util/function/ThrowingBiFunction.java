package org.springframework.util.function;

import java.util.function.BiFunction;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/function/ThrowingBiFunction.class */
public interface ThrowingBiFunction<T, U, R> extends BiFunction<T, U, R> {
    R applyWithException(T t, U u) throws Exception;

    @Override // java.util.function.BiFunction
    default R apply(T t, U u) {
        return apply(t, u, (v1, v2) -> {
            return new RuntimeException(v1, v2);
        });
    }

    default R apply(T t, U u, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        try {
            return applyWithException(t, u);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw exceptionWrapper.apply(ex2.getMessage(), ex2);
        }
    }

    default ThrowingBiFunction<T, U, R> throwing(final BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return new ThrowingBiFunction<T, U, R>() { // from class: org.springframework.util.function.ThrowingBiFunction.1
            @Override // org.springframework.util.function.ThrowingBiFunction
            public R applyWithException(T t, U u) throws Exception {
                return (R) ThrowingBiFunction.this.applyWithException(t, u);
            }

            @Override // org.springframework.util.function.ThrowingBiFunction, java.util.function.BiFunction
            public R apply(T t, U u) {
                return apply(t, u, exceptionWrapper);
            }
        };
    }

    static <T, U, R> ThrowingBiFunction<T, U, R> of(ThrowingBiFunction<T, U, R> function) {
        return function;
    }

    static <T, U, R> ThrowingBiFunction<T, U, R> of(ThrowingBiFunction<T, U, R> function, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return function.throwing(exceptionWrapper);
    }
}
