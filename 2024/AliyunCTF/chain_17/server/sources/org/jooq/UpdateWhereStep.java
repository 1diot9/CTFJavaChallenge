package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UpdateWhereStep.class */
public interface UpdateWhereStep<R extends Record> extends UpdateOrderByStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(Condition... conditionArr);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(Collection<? extends Condition> collection);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> where(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> whereExists(Select<?> select);

    @Support
    @CheckReturnValue
    @NotNull
    UpdateConditionStep<R> whereNotExists(Select<?> select);
}
