package org.jooq.impl;

import java.lang.Throwable;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingFunction.class */
public interface ThrowingFunction<T, R, E extends Throwable> {
    R apply(T t) throws Throwable;
}
