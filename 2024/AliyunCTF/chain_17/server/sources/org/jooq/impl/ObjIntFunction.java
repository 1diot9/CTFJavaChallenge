package org.jooq.impl;

/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ObjIntFunction.class */
interface ObjIntFunction<T, R> {
    R apply(T t, int i);

    default <V> ObjIntFunction<T, V> andThen(ObjIntFunction<? super R, ? extends V> after) {
        return (obj, i) -> {
            return after.apply(apply(obj, i), i);
        };
    }
}
