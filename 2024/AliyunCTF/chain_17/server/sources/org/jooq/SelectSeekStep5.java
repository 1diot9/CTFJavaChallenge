package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectSeekStep5.class */
public interface SelectSeekStep5<R extends Record, T1, T2, T3, T4, T5> extends SelectLimitStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seek(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @CheckReturnValue
    @NotNull
    SelectSeekLimitStep<R> seekAfter(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @CheckReturnValue
    @Deprecated
    @NotNull
    SelectSeekLimitStep<R> seekBefore(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);
}
