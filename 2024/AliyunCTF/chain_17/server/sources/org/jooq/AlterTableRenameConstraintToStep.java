package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTableRenameConstraintToStep.class */
public interface AlterTableRenameConstraintToStep {
    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep to(Constraint constraint);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep to(Name name);

    @Support({SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep to(String str);
}
