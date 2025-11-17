package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStepN.class */
public interface InsertValuesStepN<R extends Record> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> values(Object... objArr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> values(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> values(RowN rowN);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> values(Record record);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> valuesOfRows(RowN... rowNArr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> valuesOfRows(Collection<? extends RowN> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> valuesOfRecords(Record... recordArr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStepN<R> valuesOfRecords(Collection<? extends Record> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record> select);
}
