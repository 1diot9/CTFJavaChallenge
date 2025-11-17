package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep10.class */
public interface InsertValuesStep10<R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row10);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> values(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> record10);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> valuesOfRows(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... row10Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> valuesOfRows(Collection<? extends Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> valuesOfRecords(Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>... record10Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> valuesOfRecords(Collection<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select);
}
