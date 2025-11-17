package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DeleteConditionStep.class */
public interface DeleteConditionStep<R extends Record> extends DeleteOrderByStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> and(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> and(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> and(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> and(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> and(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> and(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> andNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> andNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> andExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> andNotExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> or(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> or(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> or(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> or(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> or(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> or(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> orNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> orNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> orExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    DeleteConditionStep<R> orNotExists(Select<?> select);
}
