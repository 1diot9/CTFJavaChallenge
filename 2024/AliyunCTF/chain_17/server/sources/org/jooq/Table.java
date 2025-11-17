package org.jooq;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Table.class */
public interface Table<R extends Record> extends TableLike<R>, RecordQualifier<R>, GroupField, SelectField<R> {
    @NotNull
    TableOptions.TableType getTableType();

    @NotNull
    TableOptions getOptions();

    @NotNull
    RecordType<R> recordType();

    @Nullable
    Identity<R, ?> getIdentity();

    @Nullable
    UniqueKey<R> getPrimaryKey();

    @Nullable
    TableField<R, ?> getRecordVersion();

    @Nullable
    TableField<R, ?> getRecordTimestamp();

    @NotNull
    List<Index> getIndexes();

    @NotNull
    List<UniqueKey<R>> getKeys();

    @NotNull
    List<UniqueKey<R>> getUniqueKeys();

    @NotNull
    <O extends Record> List<ForeignKey<O, R>> getReferencesFrom(Table<O> table);

    @NotNull
    List<ForeignKey<R, ?>> getReferences();

    @NotNull
    <O extends Record> List<ForeignKey<R, O>> getReferencesTo(Table<O> table);

    @NotNull
    List<Check<R>> getChecks();

    @Support
    @NotNull
    QualifiedAsterisk asterisk();

    @Override // org.jooq.SelectField
    @Support
    @NotNull
    Table<R> as(String str);

    @Support
    @NotNull
    Table<R> as(String str, String... strArr);

    @Support
    @NotNull
    Table<R> as(String str, Collection<? extends String> collection);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> as(String str, Function<? super Field<?>, ? extends String> function);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> as(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction);

    @Override // org.jooq.SelectField
    @Support
    @NotNull
    Table<R> as(Name name);

    @Support
    @NotNull
    Table<R> as(Name name, Name... nameArr);

    @Support
    @NotNull
    Table<R> as(Name name, Collection<? extends Name> collection);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> as(Name name, Function<? super Field<?>, ? extends Name> function);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> as(Name name, BiFunction<? super Field<?>, ? super Integer, ? extends Name> biFunction);

    @Support
    @NotNull
    Table<R> as(Table<?> table);

    @Support
    @NotNull
    Table<R> as(Table<?> table, Field<?>... fieldArr);

    @Support
    @NotNull
    Table<R> as(Table<?> table, Collection<? extends Field<?>> collection);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> as(Table<?> table, Function<? super Field<?>, ? extends Field<?>> function);

    @Support
    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    Table<R> as(Table<?> table, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> biFunction);

    @Support
    @NotNull
    Table<R> where(Condition condition);

    @Support
    @NotNull
    Table<R> where(Condition... conditionArr);

    @Support
    @NotNull
    Table<R> where(Collection<? extends Condition> collection);

    @Support
    @NotNull
    Table<R> where(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    Table<R> where(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    Table<R> where(String str);

    @PlainSQL
    @Support
    @NotNull
    Table<R> where(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    Table<R> where(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    Table<R> whereExists(Select<?> select);

    @Support
    @NotNull
    Table<R> whereNotExists(Select<?> select);

    @Support
    @NotNull
    TableOptionalOnStep<Record> join(TableLike<?> tableLike, JoinType joinType);

    @Support
    @NotNull
    TableOptionalOnStep<Record> join(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint);

    @Support
    @NotNull
    TableOnStep<Record> join(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<Record> join(Path<?> path);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> join(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> join(String str);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> join(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> join(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> join(Name name);

    @Support
    @NotNull
    TableOnStep<Record> innerJoin(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<Record> innerJoin(Path<?> path);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> innerJoin(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> innerJoin(String str);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> innerJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TableOnStep<Record> innerJoin(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    TableOnStep<Record> innerJoin(Name name);

    @Support
    @NotNull
    TablePartitionByStep<Record> leftJoin(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<Record> leftJoin(Path<?> path);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftJoin(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftJoin(String str);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftJoin(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    TablePartitionByStep<Record> leftJoin(Name name);

    @Support
    @NotNull
    TablePartitionByStep<Record> leftOuterJoin(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<Record> leftOuterJoin(Path<?> path);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftOuterJoin(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftOuterJoin(String str);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> leftOuterJoin(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    TablePartitionByStep<Record> leftOuterJoin(Name name);

    @Support
    @NotNull
    TablePartitionByStep<Record> rightJoin(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<Record> rightJoin(Path<?> path);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightJoin(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightJoin(String str);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightJoin(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    TablePartitionByStep<Record> rightJoin(Name name);

    @Support
    @NotNull
    TablePartitionByStep<Record> rightOuterJoin(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<Record> rightOuterJoin(Path<?> path);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightOuterJoin(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightOuterJoin(String str);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    TablePartitionByStep<Record> rightOuterJoin(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    TablePartitionByStep<Record> rightOuterJoin(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullJoin(TableLike<?> tableLike);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TableOptionalOnStep<Record> fullJoin(Path<?> path);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullJoin(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullJoin(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullOuterJoin(TableLike<?> tableLike);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TableOptionalOnStep<Record> fullOuterJoin(Path<?> path);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullOuterJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullOuterJoin(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullOuterJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    TablePartitionByStep<Record> fullOuterJoin(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossJoin(String str);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossJoin(Name name);

    @Support
    @NotNull
    Table<Record> naturalJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalJoin(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalJoin(String str);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalJoin(String str, Object... objArr);

    @Support
    @NotNull
    Table<Record> naturalJoin(Name name);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalJoin(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    Table<Record> naturalLeftOuterJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalLeftOuterJoin(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalLeftOuterJoin(String str);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalLeftOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalLeftOuterJoin(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Support
    @NotNull
    Table<Record> naturalLeftOuterJoin(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalRightOuterJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalRightOuterJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalRightOuterJoin(String str);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalRightOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalRightOuterJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalRightOuterJoin(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalFullOuterJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalFullOuterJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalFullOuterJoin(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalFullOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalFullOuterJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> naturalFullOuterJoin(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossApply(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossApply(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossApply(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossApply(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossApply(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> crossApply(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> outerApply(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> outerApply(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> outerApply(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> outerApply(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> outerApply(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> outerApply(Name name);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    TableOnStep<Record> straightJoin(TableLike<?> tableLike);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    TableOptionalOnStep<Record> straightJoin(Path<?> path);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    TableOnStep<Record> straightJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    TableOnStep<Record> straightJoin(String str);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    TableOnStep<Record> straightJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    TableOnStep<Record> straightJoin(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    TableOnStep<Record> straightJoin(Name name);

    @Support
    @NotNull
    Condition eq(Table<R> table);

    @Support
    @NotNull
    Condition equal(Table<R> table);

    @Support
    @NotNull
    Condition ne(Table<R> table);

    @Support
    @NotNull
    Condition notEqual(Table<R> table);

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    Field<RowId> rowid();

    @Override // org.jooq.QueryPart
    boolean equals(Object obj);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> useIndex(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> useIndexForJoin(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> useIndexForOrderBy(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> useIndexForGroupBy(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> ignoreIndex(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> ignoreIndexForJoin(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> ignoreIndexForOrderBy(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> ignoreIndexForGroupBy(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> forceIndex(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> forceIndexForJoin(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> forceIndexForOrderBy(String... strArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @NotNull
    Table<R> forceIndexForGroupBy(String... strArr);

    @Support({SQLDialect.CUBRID, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Table<Record> withOrdinality();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    @NotNull
    DivideByOnStep divideBy(Table<?> table);

    @Support
    @NotNull
    TableOnStep<R> leftSemiJoin(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<R> leftSemiJoin(Path<?> path);

    @Support
    @NotNull
    TableOnStep<R> leftAntiJoin(TableLike<?> tableLike);

    @Support
    @NotNull
    TableOptionalOnStep<R> leftAntiJoin(Path<?> path);

    @NotNull
    R from(Record record);
}
