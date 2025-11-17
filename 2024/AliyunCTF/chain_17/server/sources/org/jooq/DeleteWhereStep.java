package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DeleteWhereStep.class */
public interface DeleteWhereStep<R extends Record> extends DeleteOrderByStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(Condition... conditionArr);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(Collection<? extends Condition> collection);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> where(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> whereExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> whereNotExists(Select<?> select);
}
