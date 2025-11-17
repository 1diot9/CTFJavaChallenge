package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectSeekStep2.class */
public interface SelectSeekStep2<R extends Record, T1, T2> extends SelectLimitStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(T1 t1, T2 t2);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(Field<T1> field, Field<T2> field2);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(T1 t1, T2 t2);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(Field<T1> field, Field<T2> field2);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(T1 t1, T2 t2);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(Field<T1> field, Field<T2> field2);
}
