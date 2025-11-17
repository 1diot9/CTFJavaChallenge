package org.jooq.impl;

import java.lang.Throwable;

/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingIntFunction.class */
interface ThrowingIntFunction<R, E extends Throwable> {
    R apply(int i) throws Throwable;
}
