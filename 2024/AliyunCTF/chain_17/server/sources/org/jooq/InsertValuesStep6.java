package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep6.class */
public interface InsertValuesStep6<R extends Record, T1, T2, T3, T4, T5, T6> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> values(Row6<T1, T2, T3, T4, T5, T6> row6);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> values(Record6<T1, T2, T3, T4, T5, T6> record6);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> valuesOfRows(Row6<T1, T2, T3, T4, T5, T6>... row6Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> valuesOfRows(Collection<? extends Row6<T1, T2, T3, T4, T5, T6>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> valuesOfRecords(Record6<T1, T2, T3, T4, T5, T6>... record6Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> valuesOfRecords(Collection<? extends Record6<T1, T2, T3, T4, T5, T6>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select);
}
