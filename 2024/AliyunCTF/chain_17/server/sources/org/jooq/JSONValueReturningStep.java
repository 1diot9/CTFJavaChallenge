package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONValueReturningStep.class */
public interface JSONValueReturningStep<T> extends Field<T> {
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    Field<T> returning(DataType<?> dataType);
}
