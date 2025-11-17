package org.jooq.impl;

import java.lang.Throwable;

/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingConsumer.class */
interface ThrowingConsumer<T, E extends Throwable> {
    void accept(T t) throws Throwable;
}
