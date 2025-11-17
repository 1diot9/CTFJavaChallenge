package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertValuesStep1.class */
public interface InsertValuesStep1<R extends Record, T1> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> values(T1 t1);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> values(Field<T1> field);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> values(Collection<?> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> values(Row1<T1> row1);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> values(Record1<T1> record1);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> valuesOfRows(Row1<T1>... row1Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> valuesOfRows(Collection<? extends Row1<T1>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> valuesOfRecords(Record1<T1>... record1Arr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertValuesStep1<R, T1> valuesOfRecords(Collection<? extends Record1<T1>> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateStep<R> select(Select<? extends Record1<T1>> select);
}
