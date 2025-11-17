package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DerivedColumnList.class */
public interface DerivedColumnList extends QueryPart {
    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    <R extends Record> CommonTableExpression<R> as(ResultQuery<R> resultQuery);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    <R extends Record> CommonTableExpression<R> asMaterialized(ResultQuery<R> resultQuery);

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    <R extends Record> CommonTableExpression<R> asNotMaterialized(ResultQuery<R> resultQuery);

    @ApiStatus.Experimental
    @NotNull
    Name $tableName();

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends Name> $columnNames();
}
