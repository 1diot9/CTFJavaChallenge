package org.jooq;

import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/StoreQuery.class */
public interface StoreQuery<R extends Record> extends RowCountQuery {
    @Support
    void setRecord(R r);

    @Support
    <T> void addValue(Field<T> field, T t);

    @Support
    <T> void addValue(Field<T> field, Field<T> field2);

    @Support
    void addValues(Map<?, ?> map);

    @Support
    void setReturning();

    @Support
    void setReturning(Identity<R, ?> identity);

    @Support
    void setReturning(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support
    void setReturning(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support
    @Nullable
    R getReturnedRecord();

    @Support
    @NotNull
    Result<R> getReturnedRecords();

    @Support
    @NotNull
    Result<?> getResult();
}
