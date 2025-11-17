package org.jooq.impl;

import java.lang.Throwable;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: package-info.java */
@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThrowingRunnable.class */
public interface ThrowingRunnable<E extends Throwable> {
    void run() throws Throwable;
}
