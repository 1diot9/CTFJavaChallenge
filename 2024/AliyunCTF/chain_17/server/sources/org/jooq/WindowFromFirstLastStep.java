package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WindowFromFirstLastStep.class */
public interface WindowFromFirstLastStep<T> extends WindowIgnoreNullsStep<T> {
    @Support({SQLDialect.FIREBIRD, SQLDialect.H2})
    @NotNull
    WindowIgnoreNullsStep<T> fromFirst();

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2})
    @NotNull
    WindowIgnoreNullsStep<T> fromLast();
}
