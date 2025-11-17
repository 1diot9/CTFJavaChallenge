package org.jooq;

import java.util.Collection;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectQuery.class */
public interface SelectQuery<R extends Record> extends Select<R>, ConditionProvider {
    @Support
    void addSelect(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support
    void addSelect(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support
    void setDistinct(boolean z);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addDistinctOn(SelectFieldOrAsterisk... selectFieldOrAsteriskArr);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addDistinctOn(Collection<? extends SelectFieldOrAsterisk> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void setInto(Table<?> table);

    @Support
    void addFrom(TableLike<?> tableLike);

    @Support
    void addFrom(TableLike<?>... tableLikeArr);

    @Support
    void addFrom(Collection<? extends TableLike<?>> collection);

    @Support
    void addJoin(TableLike<?> tableLike, Condition condition);

    @Support
    void addJoin(TableLike<?> tableLike, Condition... conditionArr);

    @Support
    void addJoin(TableLike<?> tableLike, JoinType joinType, Condition condition);

    @Support
    void addJoin(TableLike<?> tableLike, JoinType joinType, Condition... conditionArr);

    @Support
    void addJoin(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint, Condition condition);

    @Support
    void addJoin(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint, Condition... conditionArr);

    @Support
    void addJoinUsing(TableLike<?> tableLike, Collection<? extends Field<?>> collection);

    @Support
    void addJoinUsing(TableLike<?> tableLike, JoinType joinType, Collection<? extends Field<?>> collection);

    @Support
    void addJoinUsing(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint, Collection<? extends Field<?>> collection);

    @Support
    void addJoinOnKey(TableLike<?> tableLike, JoinType joinType) throws DataAccessException;

    @Support
    void addJoinOnKey(TableLike<?> tableLike, JoinType joinType, TableField<?, ?>... tableFieldArr) throws DataAccessException;

    @Support
    void addJoinOnKey(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint) throws DataAccessException;

    @Support
    void addJoinOnKey(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint, TableField<?, ?>... tableFieldArr) throws DataAccessException;

    @Support
    void addJoinOnKey(TableLike<?> tableLike, JoinType joinType, ForeignKey<?, ?> foreignKey);

    @Support
    void addJoinOnKey(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint, ForeignKey<?, ?> foreignKey);

    @Support
    void addGroupBy(GroupField... groupFieldArr);

    @Support
    void addGroupBy(Collection<? extends GroupField> collection);

    @Support({SQLDialect.POSTGRES})
    void setGroupByDistinct(boolean z);

    @Support
    void addHaving(Condition condition);

    @Support
    void addHaving(Condition... conditionArr);

    @Support
    void addHaving(Collection<? extends Condition> collection);

    @Support
    void addHaving(Operator operator, Condition condition);

    @Support
    void addHaving(Operator operator, Condition... conditionArr);

    @Support
    void addHaving(Operator operator, Collection<? extends Condition> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addWindow(WindowDefinition... windowDefinitionArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addWindow(Collection<? extends WindowDefinition> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addQualify(Condition condition);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addQualify(Condition... conditionArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addQualify(Collection<? extends Condition> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addQualify(Operator operator, Condition condition);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addQualify(Operator operator, Condition... conditionArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addQualify(Operator operator, Collection<? extends Condition> collection);

    @Support
    void addHint(String str);

    @Support
    void addOption(String str);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Condition condition);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Condition... conditionArr);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Collection<? extends Condition> collection);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Operator operator, Condition condition);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Operator operator, Condition... conditionArr);

    @Override // org.jooq.ConditionProvider
    @Support
    void addConditions(Operator operator, Collection<? extends Condition> collection);

    @Support
    void addOrderBy(OrderField<?>... orderFieldArr);

    @Support
    void addOrderBy(Collection<? extends OrderField<?>> collection);

    @Support
    void addOrderBy(int... iArr);

    @Support
    void addSeekAfter(Field<?>... fieldArr);

    @Support
    void addSeekAfter(Collection<? extends Field<?>> collection);

    @Support
    @Deprecated
    void addSeekBefore(Field<?>... fieldArr);

    @Support
    @Deprecated
    void addSeekBefore(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addOffset(Number number);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void addOffset(Field<? extends Number> field);

    @Support
    void addLimit(Number number);

    @Support
    void addLimit(Field<? extends Number> field);

    @Support
    void addLimit(Number number, Number number2);

    @Support
    void addLimit(Field<? extends Number> field, Number number);

    @Support
    void addLimit(Number number, Field<? extends Number> field);

    @Support
    void addLimit(Field<? extends Number> field, Field<? extends Number> field2);

    @Support({SQLDialect.H2})
    void setLimitPercent(boolean z);

    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    void setWithTies(boolean z);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void setForUpdate(boolean z);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void setForNoKeyUpdate(boolean z);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB})
    @Deprecated(forRemoval = true, since = "3.14")
    void setForUpdateOf(Field<?>... fieldArr);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB})
    @Deprecated(forRemoval = true, since = "3.14")
    void setForUpdateOf(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    void setForUpdateOf(Table<?>... tableArr);

    @Support({SQLDialect.MARIADB})
    @Deprecated(forRemoval = true, since = "3.14")
    void setForUpdateWait(int i);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    void setForUpdateNoWait();

    @Support({SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @Deprecated(forRemoval = true, since = "3.14")
    void setForUpdateSkipLocked();

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void setForShare(boolean z);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void setForKeyShare(boolean z);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB})
    void setForLockModeOf(Field<?>... fieldArr);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB})
    void setForLockModeOf(Collection<? extends Field<?>> collection);

    @Support({SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void setForLockModeOf(Table<?>... tableArr);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    void setForLockModeWait(int i);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void setForLockModeNoWait();

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    void setForLockModeSkipLocked();

    @Support({SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    void setWithCheckOption();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    void setWithReadOnly();
}
