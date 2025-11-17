package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONArrayNullStep.class */
public interface JSONArrayNullStep<T> extends JSONArrayReturningStep<T> {
    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    JSONArrayReturningStep<T> nullOnNull();

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    JSONArrayReturningStep<T> absentOnNull();
}
