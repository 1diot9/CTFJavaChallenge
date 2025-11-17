package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectOnStep.class */
public interface SelectOnStep<R extends Record> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> on(Condition condition);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> on(Condition... conditionArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> on(Field<Boolean> field);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> on(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> on(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> on(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> on(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> onKey() throws DataAccessException;

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> onKey(TableField<?, ?>... tableFieldArr) throws DataAccessException;

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnConditionStep<R> onKey(ForeignKey<?, ?> foreignKey);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> using(Field<?>... fieldArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> using(Collection<? extends Field<?>> collection);
}
