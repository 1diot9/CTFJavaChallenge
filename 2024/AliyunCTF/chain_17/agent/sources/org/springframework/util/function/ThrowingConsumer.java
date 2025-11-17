package org.springframework.util.function;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/function/ThrowingConsumer.class */
public interface ThrowingConsumer<T> extends Consumer<T> {
    void acceptWithException(T t) throws Exception;

    @Override // java.util.function.Consumer
    default void accept(T t) {
        accept(t, (v1, v2) -> {
            return new RuntimeException(v1, v2);
        });
    }

    default void accept(T t, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        try {
            acceptWithException(t);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw exceptionWrapper.apply(ex2.getMessage(), ex2);
        }
    }

    default ThrowingConsumer<T> throwing(final BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return new ThrowingConsumer<T>() { // from class: org.springframework.util.function.ThrowingConsumer.1
            @Override // org.springframework.util.function.ThrowingConsumer
            public void acceptWithException(T t) throws Exception {
                ThrowingConsumer.this.acceptWithException(t);
            }

            @Override // org.springframework.util.function.ThrowingConsumer, java.util.function.Consumer
            public void accept(T t) {
                accept(t, exceptionWrapper);
            }
        };
    }

    static <T> ThrowingConsumer<T> of(ThrowingConsumer<T> consumer) {
        return consumer;
    }

    static <T> ThrowingConsumer<T> of(ThrowingConsumer<T> consumer, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return consumer.throwing(exceptionWrapper);
    }
}
