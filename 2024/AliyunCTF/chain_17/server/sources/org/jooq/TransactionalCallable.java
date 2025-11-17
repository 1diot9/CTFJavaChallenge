package org.jooq;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Stream;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TransactionalCallable.class */
public interface TransactionalCallable<T> {
    T run(Configuration configuration) throws Throwable;

    @SafeVarargs
    static <T> TransactionalCallable<T> of(TransactionalCallable<T>... callables) {
        return of(Arrays.asList(callables));
    }

    static <T> TransactionalCallable<T> of(Collection<? extends TransactionalCallable<T>> callables) {
        return configuration -> {
            Object obj = null;
            Iterator it = callables.iterator();
            while (it.hasNext()) {
                TransactionalCallable<T> callable = (TransactionalCallable) it.next();
                obj = configuration.dsl().transactionResult(callable);
            }
            return obj;
        };
    }

    static <T, R> TransactionalCallable<R> of(TransactionalCallable<T>[] callables, Collector<T, ?, R> collector) {
        return of(Arrays.asList(callables), collector);
    }

    static <T, R> TransactionalCallable<R> of(Collection<? extends TransactionalCallable<T>> callables, Collector<T, ?, R> collector) {
        return configuration -> {
            Stream stream = callables.stream();
            DSLContext dsl = configuration.dsl();
            Objects.requireNonNull(dsl);
            return stream.map(dsl::transactionResult).collect(collector);
        };
    }
}
