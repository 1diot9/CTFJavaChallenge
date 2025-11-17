package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectOnConditionStep.class */
public interface SelectOnConditionStep<R extends Record> extends SelectJoinStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> and(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> and(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> and(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> and(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> and(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> and(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> andNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> andNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> andExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> andNotExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> or(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> or(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> or(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> or(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> or(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> or(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> orNot(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> orNot(Field<Boolean> field);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> orExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> orNotExists(Select<?> select);
}
