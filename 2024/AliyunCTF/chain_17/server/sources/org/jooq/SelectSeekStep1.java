package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectSeekStep1.class */
public interface SelectSeekStep1<R extends Record, T1> extends SelectLimitStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(T1 t1);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(Field<T1> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(T1 t1);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(Field<T1> field);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(T1 t1);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(Field<T1> field);
}
