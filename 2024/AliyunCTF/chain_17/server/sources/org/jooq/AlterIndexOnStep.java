package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterIndexOnStep.class */
public interface AlterIndexOnStep extends AlterIndexStep {
    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexStep on(String str);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexStep on(Name name);

    @Support({SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterIndexStep on(Table<?> table);
}
