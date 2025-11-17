package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectSeekLimitStep.class */
public interface SelectSeekLimitStep<R extends Record> extends SelectForUpdateStep<R> {
    @Support
    @CheckReturnValue
    @NotNull
    SelectForUpdateStep<R> limit(Number number);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateStep<R> limit(Field<? extends Number> field);
}
