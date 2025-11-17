package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectFinalStep.class */
public interface SelectFinalStep<R extends Record> extends Select<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectQuery<R> getQuery();
}
