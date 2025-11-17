package org.jooq;

import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertSetMoreStep.class */
public interface InsertSetMoreStep<R extends Record> extends InsertOnDuplicateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    <T> InsertSetMoreStep<R> set(Field<T> field, T t);

    @Support
    @CheckReturnValue
    @NotNull
    <T> InsertSetMoreStep<R> set(Field<T> field, Field<T> field2);

    @Support
    @CheckReturnValue
    @NotNull
    <T> InsertSetMoreStep<R> set(Field<T> field, Select<? extends Record1<T>> select);

    @Support
    @CheckReturnValue
    @NotNull
    <T> InsertSetMoreStep<R> setNull(Field<T> field);

    @Support
    @CheckReturnValue
    @NotNull
    InsertSetMoreStep<R> set(Map<?, ?> map);

    @Support
    @CheckReturnValue
    @NotNull
    InsertSetMoreStep<R> set(Record record);

    @Support
    @CheckReturnValue
    @NotNull
    InsertSetMoreStep<R> set(Record... recordArr);

    @Support
    @CheckReturnValue
    @NotNull
    InsertSetMoreStep<R> set(Collection<? extends Record> collection);

    @Support
    @CheckReturnValue
    @NotNull
    InsertSetStep<R> newRecord();
}
