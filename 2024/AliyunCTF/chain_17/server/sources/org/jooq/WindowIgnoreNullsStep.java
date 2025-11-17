package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WindowIgnoreNullsStep.class */
public interface WindowIgnoreNullsStep<T> extends WindowOverStep<T> {
    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.TRINO})
    @NotNull
    WindowOverStep<T> ignoreNulls();

    @Support({SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.TRINO})
    @NotNull
    WindowOverStep<T> respectNulls();
}
