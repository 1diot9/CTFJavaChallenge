package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectHavingStep.class */
public interface SelectHavingStep<R extends Record> extends SelectWindowStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(Condition... conditionArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(Collection<? extends Condition> collection);

    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectHavingConditionStep<R> having(String str, QueryPart... queryPartArr);
}
