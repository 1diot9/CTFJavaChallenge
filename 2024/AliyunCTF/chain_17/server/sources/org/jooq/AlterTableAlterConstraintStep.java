package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterTableAlterConstraintStep.class */
public interface AlterTableAlterConstraintStep {
    @Support({SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep enforced();

    @Support({SQLDialect.MYSQL})
    @CheckReturnValue
    @NotNull
    AlterTableFinalStep notEnforced();
}
