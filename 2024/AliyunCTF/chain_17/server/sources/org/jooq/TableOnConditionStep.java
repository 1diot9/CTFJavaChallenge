package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableOnConditionStep.class */
public interface TableOnConditionStep<R extends Record> extends Table<R> {
    @Support
    @NotNull
    TableOnConditionStep<R> and(Condition condition);

    @Support
    @NotNull
    TableOnConditionStep<R> and(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> and(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> and(String str);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> and(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> and(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    TableOnConditionStep<R> andNot(Condition condition);

    @Support
    @NotNull
    TableOnConditionStep<R> andNot(Field<Boolean> field);

    @Support
    @NotNull
    TableOnConditionStep<R> andExists(Select<?> select);

    @Support
    @NotNull
    TableOnConditionStep<R> andNotExists(Select<?> select);

    @Support
    @NotNull
    TableOnConditionStep<R> or(Condition condition);

    @Support
    @NotNull
    TableOnConditionStep<R> or(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> or(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> or(String str);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> or(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> or(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    TableOnConditionStep<R> orNot(Condition condition);

    @Support
    @NotNull
    TableOnConditionStep<R> orNot(Field<Boolean> field);

    @Support
    @NotNull
    TableOnConditionStep<R> orExists(Select<?> select);

    @Support
    @NotNull
    TableOnConditionStep<R> orNotExists(Select<?> select);
}
