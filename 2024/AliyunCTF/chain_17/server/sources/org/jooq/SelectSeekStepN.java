package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectSeekStepN.class */
public interface SelectSeekStepN<R extends Record> extends SelectLimitStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(Object... objArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(Object... objArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(Object... objArr);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(Field<?>... fieldArr);
}
