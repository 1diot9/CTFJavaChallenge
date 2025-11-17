package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SelectForUpdateStep.class */
public interface SelectForUpdateStep<R extends Record> extends SelectForStep<R> {
    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateOfStep<R> forUpdate();

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateOfStep<R> forNoKeyUpdate();

    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateOfStep<R> forShare();

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectForUpdateOfStep<R> forKeyShare();

    @Support({SQLDialect.FIREBIRD, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    SelectFinalStep<R> withCheckOption();

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    SelectFinalStep<R> withReadOnly();
}
