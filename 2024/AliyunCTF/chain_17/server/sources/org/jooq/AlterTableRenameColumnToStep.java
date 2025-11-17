package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTableRenameColumnToStep.class */
public interface AlterTableRenameColumnToStep {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep to(Field<?> field);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep to(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep to(String str);
}
