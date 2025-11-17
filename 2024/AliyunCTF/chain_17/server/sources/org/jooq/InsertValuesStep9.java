package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep9.class */
public interface InsertValuesStep9<R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> values(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row9);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> values(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> record9);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> valuesOfRows(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... row9Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> valuesOfRows(Collection<? extends Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> valuesOfRecords(Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>... record9Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> valuesOfRecords(Collection<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select);
}
