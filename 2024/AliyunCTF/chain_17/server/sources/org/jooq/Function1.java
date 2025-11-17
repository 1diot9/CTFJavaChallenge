package org.jooq;

import java.util.function.Function;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Function1.class */
public interface Function1<T1, R> extends Function<T1, R> {
    @Override // java.util.function.Function
    R apply(T1 t1);
}
