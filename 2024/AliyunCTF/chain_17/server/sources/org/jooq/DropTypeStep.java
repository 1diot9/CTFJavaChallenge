package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DropTypeStep.class */
public interface DropTypeStep extends DropTypeFinalStep {
    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeFinalStep cascade();

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    DropTypeFinalStep restrict();
}
