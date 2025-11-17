package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterIndexStep.class */
public interface AlterIndexStep {
    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexFinalStep renameTo(String str);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexFinalStep renameTo(Name name);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexFinalStep renameTo(Index index);
}
