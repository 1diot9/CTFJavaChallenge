package org.jooq;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TransactionalPublishable.class */
public interface TransactionalPublishable<T> {
    @NotNull
    org.reactivestreams.Publisher<T> run(Configuration configuration);
}
