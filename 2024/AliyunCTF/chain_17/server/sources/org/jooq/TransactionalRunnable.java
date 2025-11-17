package org.jooq;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TransactionalRunnable.class */
public interface TransactionalRunnable {
    void run(Configuration configuration) throws Throwable;

    static TransactionalRunnable of(TransactionalRunnable... runnables) {
        return of(Arrays.asList(runnables));
    }

    static TransactionalRunnable of(Collection<? extends TransactionalRunnable> runnables) {
        return configuration -> {
            Iterator it = runnables.iterator();
            while (it.hasNext()) {
                TransactionalRunnable runnable = (TransactionalRunnable) it.next();
                configuration.dsl().transaction(runnable);
            }
        };
    }
}
