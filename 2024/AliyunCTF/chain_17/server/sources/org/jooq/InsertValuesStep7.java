package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep7.class */
public interface InsertValuesStep7<R extends Record, T1, T2, T3, T4, T5, T6, T7> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> values(Row7<T1, T2, T3, T4, T5, T6, T7> row7);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> values(Record7<T1, T2, T3, T4, T5, T6, T7> record7);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> valuesOfRows(Row7<T1, T2, T3, T4, T5, T6, T7>... row7Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> valuesOfRows(Collection<? extends Row7<T1, T2, T3, T4, T5, T6, T7>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> valuesOfRecords(Record7<T1, T2, T3, T4, T5, T6, T7>... record7Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> valuesOfRecords(Collection<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select);
}
