package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConstraintEnforcementStep.class */
public interface ConstraintEnforcementStep extends ConstraintFinalStep {
    @Support({SQLDialect.MYSQL})
    @NotNull
    ConstraintFinalStep enforced();

    @Support({SQLDialect.MYSQL})
    @NotNull
    ConstraintFinalStep notEnforced();
}
