package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectQualifyConditionStep.class */
public interface SelectQualifyConditionStep<R extends Record> extends SelectOrderByStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> and(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> and(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> and(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> and(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> and(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> and(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> andNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> andNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> andExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> andNotExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> or(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> or(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> or(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> or(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> or(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> or(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> orNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> orNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> orExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectQualifyConditionStep<R> orNotExists(Select<?> select);
}
