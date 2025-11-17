package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectHavingConditionStep.class */
public interface SelectHavingConditionStep<R extends Record> extends SelectWindowStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> and(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> and(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> and(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> and(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> and(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> and(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> andNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> andNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> andExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> andNotExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> or(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> or(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> or(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> or(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> or(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> or(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> orNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> orNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> orExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> orNotExists(Select<?> select);
}
