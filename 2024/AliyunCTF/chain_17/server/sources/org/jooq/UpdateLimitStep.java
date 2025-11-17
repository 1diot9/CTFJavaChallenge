package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UpdateLimitStep.class */
public interface UpdateLimitStep<R extends Record> extends UpdateReturningStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    UpdateReturningStep<R> limit(Number number);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateReturningStep<R> limit(Field<? extends Number> field);
}
