package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableOnStep.class */
public interface TableOnStep<R extends Record> {
    @Support
    @NotNull
    TableOnConditionStep<R> on(Condition condition);

    @Support
    @NotNull
    TableOnConditionStep<R> on(Condition... conditionArr);

    @Support
    @NotNull
    TableOnConditionStep<R> on(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> on(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> on(String str);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> on(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TableOnConditionStep<R> on(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    Table<Record> using(Field<?>... fieldArr);

    @Support
    @NotNull
    Table<Record> using(Collection<? extends Field<?>> collection);

    @Support
    @NotNull
    TableOnConditionStep<R> onKey() throws DataAccessException;

    @Support
    @NotNull
    TableOnConditionStep<R> onKey(TableField<?, ?>... tableFieldArr) throws DataAccessException;

    @Support
    @NotNull
    TableOnConditionStep<R> onKey(ForeignKey<?, ?> foreignKey);
}
