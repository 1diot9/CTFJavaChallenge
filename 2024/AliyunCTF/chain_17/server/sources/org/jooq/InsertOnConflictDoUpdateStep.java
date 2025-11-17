package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/InsertOnConflictDoUpdateStep.class */
public interface InsertOnConflictDoUpdateStep<R extends Record> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    InsertOnDuplicateSetStep<R> doUpdate();

    @Support
    @CheckReturnValue
    @NotNull
    InsertReturningStep<R> doNothing();
}
