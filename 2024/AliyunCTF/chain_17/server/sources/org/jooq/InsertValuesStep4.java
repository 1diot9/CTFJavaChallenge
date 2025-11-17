package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep4.class */
public interface InsertValuesStep4<R extends Record, T1, T2, T3, T4> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> values(T1 t1, T2 t2, T3 t3, T4 t4);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> values(Row4<T1, T2, T3, T4> row4);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> values(Record4<T1, T2, T3, T4> record4);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> valuesOfRows(Row4<T1, T2, T3, T4>... row4Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> valuesOfRows(Collection<? extends Row4<T1, T2, T3, T4>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> valuesOfRecords(Record4<T1, T2, T3, T4>... record4Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep4<R, T1, T2, T3, T4> valuesOfRecords(Collection<? extends Record4<T1, T2, T3, T4>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record4<T1, T2, T3, T4>> select);
}
