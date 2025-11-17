package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep12.class */
public interface InsertValuesStep12<R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> values(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row12);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> values(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> record12);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> valuesOfRows(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... row12Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> valuesOfRows(Collection<? extends Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> valuesOfRecords(Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>... record12Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> valuesOfRecords(Collection<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select);
}
