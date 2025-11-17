package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTableAddStep.class */
public interface AlterTableAddStep extends AlterTableFinalStep {
    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep first();

    @Support({SQLDialect.H2, SQLDialect.HSQLDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep before(String str);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep before(Name name);

    @Support({SQLDialect.H2, SQLDialect.HSQLDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep before(Field<?> field);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep after(String str);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep after(Name name);

    @Support({SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep after(Field<?> field);
}
