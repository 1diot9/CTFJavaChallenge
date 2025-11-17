package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONArrayAggReturningStep.class */
public interface JSONArrayAggReturningStep<T> extends AggregateFilterStep<T> {
    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    AggregateFilterStep<T> returning(DataType<?> dataType);
}
