package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterDomainRenameConstraintStep.class */
public interface AlterDomainRenameConstraintStep {
    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep to(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep to(Name name);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep to(Constraint constraint);
}
