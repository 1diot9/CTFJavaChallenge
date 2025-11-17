package org.jooq.impl;

import java.util.function.Supplier;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Lazy.class */
public final class Lazy<T> {
    private final Supplier<? extends T> supplier;
    private volatile T value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Lazy<T> of(Supplier<? extends T> supplier) {
        return new Lazy<>(supplier);
    }

    private Lazy(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public T get() {
        if (this.value == null) {
            synchronized (this) {
                if (this.value == null) {
                    this.value = this.supplier.get();
                }
            }
        }
        return this.value;
    }
}
