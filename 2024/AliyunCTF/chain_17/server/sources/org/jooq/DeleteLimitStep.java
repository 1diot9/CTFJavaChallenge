package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DeleteLimitStep.class */
public interface DeleteLimitStep<R extends Record> extends DeleteReturningStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    DeleteReturningStep<R> limit(Number number);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteReturningStep<R> limit(Field<? extends Number> field);
}
