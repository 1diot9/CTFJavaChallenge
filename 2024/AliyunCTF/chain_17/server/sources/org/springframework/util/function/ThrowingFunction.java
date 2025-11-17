package org.springframework.util.function;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/function/ThrowingFunction.class */
public interface ThrowingFunction<T, R> extends Function<T, R> {
    R applyWithException(T t) throws Exception;

    @Override // java.util.function.Function
    default R apply(T t) {
        return apply(t, (v1, v2) -> {
            return new RuntimeException(v1, v2);
        });
    }

    default R apply(T t, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        try {
            return applyWithException(t);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw exceptionWrapper.apply(ex2.getMessage(), ex2);
        }
    }

    default ThrowingFunction<T, R> throwing(final BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return new ThrowingFunction<T, R>() { // from class: org.springframework.util.function.ThrowingFunction.1
            @Override // org.springframework.util.function.ThrowingFunction
            public R applyWithException(T t) throws Exception {
                return (R) ThrowingFunction.this.applyWithException(t);
            }

            @Override // org.springframework.util.function.ThrowingFunction, java.util.function.Function
            public R apply(T t) {
                return apply(t, exceptionWrapper);
            }
        };
    }

    static <T, R> ThrowingFunction<T, R> of(ThrowingFunction<T, R> function) {
        return function;
    }

    static <T, R> ThrowingFunction<T, R> of(ThrowingFunction<T, R> function, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return function.throwing(exceptionWrapper);
    }
}
