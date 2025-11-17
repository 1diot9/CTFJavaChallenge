package org.jooq.impl;

import java.lang.Throwable;

/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingBiFunction.class */
interface ThrowingBiFunction<T1, T2, R, E extends Throwable> {
    R apply(T1 t1, T2 t2) throws Throwable;
}
