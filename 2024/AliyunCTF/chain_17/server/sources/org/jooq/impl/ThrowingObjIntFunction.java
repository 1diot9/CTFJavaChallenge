package org.jooq.impl;

import java.lang.Throwable;

/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingObjIntFunction.class */
interface ThrowingObjIntFunction<T, R, E extends Throwable> {
    R apply(T t, int i) throws Throwable;
}
