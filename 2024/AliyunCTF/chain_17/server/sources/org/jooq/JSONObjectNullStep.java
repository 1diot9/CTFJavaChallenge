package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONObjectNullStep.class */
public interface JSONObjectNullStep<T> extends JSONObjectReturningStep<T> {
    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    JSONObjectReturningStep<T> nullOnNull();

    @Support({SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    JSONObjectReturningStep<T> absentOnNull();
}
