package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep5.class */
public interface InsertValuesStep5<R extends Record, T1, T2, T3, T4, T5> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> values(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> values(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> values(Row5<T1, T2, T3, T4, T5> row5);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> values(Record5<T1, T2, T3, T4, T5> record5);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> valuesOfRows(Row5<T1, T2, T3, T4, T5>... row5Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> valuesOfRows(Collection<? extends Row5<T1, T2, T3, T4, T5>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> valuesOfRecords(Record5<T1, T2, T3, T4, T5>... record5Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep5<R, T1, T2, T3, T4, T5> valuesOfRecords(Collection<? extends Record5<T1, T2, T3, T4, T5>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record5<T1, T2, T3, T4, T5>> select);
}
