package org.jooq;

import java.util.function.Supplier;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Function0.class */
public interface Function0<R> extends Supplier<R> {
    R apply();

    @Override // java.util.function.Supplier
    default R get() {
        return apply();
    }
}
