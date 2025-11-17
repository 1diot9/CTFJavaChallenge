package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectJoinStep.class */
public interface SelectJoinStep<R extends Record> extends SelectWhereStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> join(TableLike<?> tableLike, JoinType joinType);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> join(TableLike<?> tableLike, JoinType joinType, QOM.JoinHint joinHint);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> join(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> join(Path<?> path);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> join(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> join(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> join(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> join(String str, QueryPart... queryPartArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> join(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> innerJoin(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> innerJoin(Path<?> path);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> innerJoin(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> innerJoin(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> innerJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> innerJoin(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> innerJoin(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossJoin(String str);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossJoin(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftJoin(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> leftJoin(Path<?> path);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftJoin(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftJoin(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftJoin(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftJoin(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftOuterJoin(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> leftOuterJoin(Path<?> path);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftOuterJoin(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftOuterJoin(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftOuterJoin(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> leftOuterJoin(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightJoin(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> rightJoin(Path<?> path);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightJoin(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightJoin(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightJoin(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightJoin(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightOuterJoin(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> rightOuterJoin(Path<?> path);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightOuterJoin(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightOuterJoin(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightOuterJoin(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinPartitionByStep<R> rightOuterJoin(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullJoin(TableLike<?> tableLike);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> fullJoin(Path<?> path);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullJoin(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullJoin(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullOuterJoin(TableLike<?> tableLike);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> fullOuterJoin(Path<?> path);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullOuterJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullOuterJoin(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullOuterJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> fullOuterJoin(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalJoin(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalJoin(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalJoin(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalJoin(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalLeftOuterJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalLeftOuterJoin(SQL sql);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalLeftOuterJoin(String str);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalLeftOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalLeftOuterJoin(String str, QueryPart... queryPartArr);

    @Support
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalLeftOuterJoin(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalRightOuterJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalRightOuterJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalRightOuterJoin(String str);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalRightOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalRightOuterJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalRightOuterJoin(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalFullOuterJoin(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalFullOuterJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalFullOuterJoin(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalFullOuterJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalFullOuterJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> naturalFullOuterJoin(Name name);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> leftSemiJoin(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> leftSemiJoin(Path<?> path);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> leftAntiJoin(TableLike<?> tableLike);

    @Support
    @CheckReturnValue
    @NotNull
    SelectOptionalOnStep<R> leftAntiJoin(Path<?> path);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossApply(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossApply(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossApply(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossApply(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossApply(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> crossApply(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> outerApply(TableLike<?> tableLike);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> outerApply(SQL sql);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> outerApply(String str);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> outerApply(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> outerApply(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectJoinStep<R> outerApply(Name name);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> straightJoin(TableLike<?> tableLike);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> straightJoin(Path<?> path);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> straightJoin(SQL sql);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> straightJoin(String str);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> straightJoin(String str, Object... objArr);

    @PlainSQL
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> straightJoin(String str, QueryPart... queryPartArr);

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    SelectOnStep<R> straightJoin(Name name);
}
