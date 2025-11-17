package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WindowExcludeStep.class */
public interface WindowExcludeStep<T> extends WindowFinalStep<T> {
    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowFinalStep<T> excludeCurrentRow();

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowFinalStep<T> excludeGroup();

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowFinalStep<T> excludeTies();

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowFinalStep<T> excludeNoOthers();
}
