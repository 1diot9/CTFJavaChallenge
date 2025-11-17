package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep2.class */
public interface InsertValuesStep2<R extends Record, T1, T2> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> values(T1 t1, T2 t2);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> values(Field<T1> field, Field<T2> field2);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> values(Row2<T1, T2> row2);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> values(Record2<T1, T2> record2);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> valuesOfRows(Row2<T1, T2>... row2Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> valuesOfRows(Collection<? extends Row2<T1, T2>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> valuesOfRecords(Record2<T1, T2>... record2Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep2<R, T1, T2> valuesOfRecords(Collection<? extends Record2<T1, T2>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record2<T1, T2>> select);
}
