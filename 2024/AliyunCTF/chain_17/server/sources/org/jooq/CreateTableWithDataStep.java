package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CreateTableWithDataStep.class */
public interface CreateTableWithDataStep extends CreateTableOnCommitStep {
    @Support({SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableOnCommitStep withData();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    CreateTableOnCommitStep withNoData();
}
