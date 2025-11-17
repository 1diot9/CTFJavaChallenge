package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONArrayAggNullStep.class */
public interface JSONArrayAggNullStep<T> extends JSONArrayAggReturningStep<T> {
    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONArrayAggReturningStep<T> nullOnNull();

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONArrayAggReturningStep<T> absentOnNull();
}
