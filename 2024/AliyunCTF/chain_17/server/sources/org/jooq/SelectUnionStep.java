package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectUnionStep.class */
public interface SelectUnionStep<R extends Record> extends SelectCorrelatedSubqueryStep<R> {
    @Override // org.jooq.Select
    @Support
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> union(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> unionDistinct(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> unionAll(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> except(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> exceptDistinct(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> exceptAll(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> intersect(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> intersectDistinct(Select<? extends R> select);

    @Override // org.jooq.Select
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectOrderByStep<R> intersectAll(Select<? extends R> select);
}
