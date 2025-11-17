package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTableDropStep.class */
public interface AlterTableDropStep extends AlterTableFinalStep {
    @Support({SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep cascade();

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep restrict();
}
