package org.jooq.impl;

import java.lang.Throwable;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingPredicate.class */
public interface ThrowingPredicate<T, E extends Throwable> {
    boolean test(T t) throws Throwable;

    default ThrowingPredicate<T, E> negate() {
        return obj -> {
            return !test(obj);
        };
    }
}
