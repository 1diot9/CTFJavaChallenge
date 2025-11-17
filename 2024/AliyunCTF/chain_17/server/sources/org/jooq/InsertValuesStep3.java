package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep3.class */
public interface InsertValuesStep3<R extends Record, T1, T2, T3> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> values(T1 t1, T2 t2, T3 t3);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> values(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> values(Row3<T1, T2, T3> row3);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> values(Record3<T1, T2, T3> record3);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> valuesOfRows(Row3<T1, T2, T3>... row3Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> valuesOfRows(Collection<? extends Row3<T1, T2, T3>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> valuesOfRecords(Record3<T1, T2, T3>... record3Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep3<R, T1, T2, T3> valuesOfRecords(Collection<? extends Record3<T1, T2, T3>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record3<T1, T2, T3>> select);
}
