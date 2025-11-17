package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UpdateConditionStep.class */
public interface UpdateConditionStep<R extends Record> extends UpdateOrderByStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> and(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> and(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> and(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> and(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> and(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> and(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> andNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> andNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> andExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> andNotExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> or(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> or(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> or(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> or(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> or(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> or(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> orNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> orNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> orExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> orNotExists(Select<?> select);
}
