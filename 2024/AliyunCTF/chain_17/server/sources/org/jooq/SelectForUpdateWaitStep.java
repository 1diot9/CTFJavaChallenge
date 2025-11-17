package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectForUpdateWaitStep.class */
public interface SelectForUpdateWaitStep<R extends Record> extends SelectForStep<R> {
    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    SelectForStep<R> wait(int i);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForStep<R> noWait();

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForStep<R> skipLocked();
}
