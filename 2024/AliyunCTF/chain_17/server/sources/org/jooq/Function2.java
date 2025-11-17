package org.jooq;

import java.util.function.BiFunction;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Function2.class */
public interface Function2<T1, T2, R> extends BiFunction<T1, T2, R> {
    @Override // java.util.function.BiFunction
    R apply(T1 t1, T2 t2);
}
