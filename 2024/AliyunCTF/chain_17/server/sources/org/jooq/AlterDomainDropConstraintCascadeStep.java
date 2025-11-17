package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterDomainDropConstraintCascadeStep.class */
public interface AlterDomainDropConstraintCascadeStep extends AlterDomainFinalStep {
    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep cascade();

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep restrict();
}
