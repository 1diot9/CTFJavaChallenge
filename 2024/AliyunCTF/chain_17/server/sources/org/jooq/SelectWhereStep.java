package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectWhereStep.class */
public interface SelectWhereStep<R extends Record> extends SelectConnectByStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(Condition... conditionArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(Collection<? extends Condition> collection);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> where(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> whereExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    SelectConditionStep<R> whereNotExists(Select<?> select);
}
