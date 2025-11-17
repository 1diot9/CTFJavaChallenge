package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DropSchemaStep.class */
public interface DropSchemaStep extends DropSchemaFinalStep {
    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaFinalStep cascade();

    @Support({SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropSchemaFinalStep restrict();
}
