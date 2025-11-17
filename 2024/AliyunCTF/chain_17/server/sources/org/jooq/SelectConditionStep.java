package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectConditionStep.class */
public interface SelectConditionStep<R extends Record> extends SelectConnectByStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> and(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> and(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> and(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> and(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> and(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> and(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> andNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> andNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> andExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> andNotExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> or(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> or(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> or(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> or(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> or(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> or(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> orNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> orNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> orExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> orNotExists(Select<?> select);
}
