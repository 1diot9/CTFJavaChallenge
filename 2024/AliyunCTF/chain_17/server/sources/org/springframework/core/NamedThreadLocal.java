package org.springframework.core;

import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NamedThreadLocal.class */
public class NamedThreadLocal<T> extends ThreadLocal<T> {
    private final String name;

    public NamedThreadLocal(String name) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static <S> ThreadLocal<S> withInitial(String name, Supplier<? extends S> supplier) {
        return new SuppliedNamedThreadLocal(name, supplier);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NamedThreadLocal$SuppliedNamedThreadLocal.class */
    private static final class SuppliedNamedThreadLocal<T> extends NamedThreadLocal<T> {
        private final Supplier<? extends T> supplier;

        SuppliedNamedThreadLocal(String name, Supplier<? extends T> supplier) {
            super(name);
            this.supplier = (Supplier) Objects.requireNonNull(supplier);
        }

        @Override // java.lang.ThreadLocal
        protected T initialValue() {
            return this.supplier.get();
        }
    }
}
