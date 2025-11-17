package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONTableColumnPathStep.class */
public interface JSONTableColumnPathStep extends JSONTableColumnForOrdinalityStep, JSONTableColumnsStep {
    @Support({SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @NotNull
    JSONTableColumnsStep path(String str);
}
