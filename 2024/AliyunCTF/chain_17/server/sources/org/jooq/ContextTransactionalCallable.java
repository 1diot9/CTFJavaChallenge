package org.jooq;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ContextTransactionalCallable.class */
public interface ContextTransactionalCallable<T> {
    T run() throws Throwable;
}
