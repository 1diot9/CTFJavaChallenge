package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DropIndexCascadeStep.class */
public interface DropIndexCascadeStep extends DropIndexFinalStep {
    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexFinalStep cascade();

    @Support({SQLDialect.DUCKDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropIndexFinalStep restrict();
}
