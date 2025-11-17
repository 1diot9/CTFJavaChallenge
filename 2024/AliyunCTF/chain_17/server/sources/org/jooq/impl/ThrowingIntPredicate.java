package org.jooq.impl;

import java.lang.Throwable;

/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingIntPredicate.class */
interface ThrowingIntPredicate<T, E extends Throwable> {
    boolean test(T t, int i) throws Throwable;

    default ThrowingIntPredicate<T, E> negate() {
        return (obj, i) -> {
            return !test(obj, i);
        };
    }
}
