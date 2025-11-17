package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep8.class */
public interface InsertValuesStep8<R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> values(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row8);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> values(Record8<T1, T2, T3, T4, T5, T6, T7, T8> record8);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> valuesOfRows(Row8<T1, T2, T3, T4, T5, T6, T7, T8>... row8Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> valuesOfRows(Collection<? extends Row8<T1, T2, T3, T4, T5, T6, T7, T8>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> valuesOfRecords(Record8<T1, T2, T3, T4, T5, T6, T7, T8>... record8Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> valuesOfRecords(Collection<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select);
}
