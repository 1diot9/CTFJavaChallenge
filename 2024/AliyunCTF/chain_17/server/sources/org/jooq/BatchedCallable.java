package org.jooq;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/BatchedCallable.class */
public interface BatchedCallable<T> {
    T run(Configuration configuration) throws Throwable;
}
