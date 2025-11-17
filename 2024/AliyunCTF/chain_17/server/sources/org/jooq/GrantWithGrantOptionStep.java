package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/GrantWithGrantOptionStep.class */
public interface GrantWithGrantOptionStep extends GrantFinalStep {
    @Support({SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    GrantFinalStep withGrantOption();
}
