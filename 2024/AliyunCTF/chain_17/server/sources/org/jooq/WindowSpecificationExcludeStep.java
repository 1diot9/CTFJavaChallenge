package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WindowSpecificationExcludeStep.class */
public interface WindowSpecificationExcludeStep extends WindowSpecificationFinalStep {
    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowSpecificationFinalStep excludeCurrentRow();

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowSpecificationFinalStep excludeGroup();

    @Support({SQLDialect.H2, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowSpecificationFinalStep excludeTies();

    @Support({SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    @NotNull
    WindowSpecificationFinalStep excludeNoOthers();
}
