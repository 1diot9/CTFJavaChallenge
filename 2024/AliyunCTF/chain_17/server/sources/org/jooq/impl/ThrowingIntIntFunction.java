package org.jooq.impl;

import java.lang.Throwable;

/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingIntIntFunction.class */
interface ThrowingIntIntFunction<R, E extends Throwable> {
    R apply(int i, int i2) throws Throwable;
}
