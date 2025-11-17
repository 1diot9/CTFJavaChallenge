package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterDatabaseStep.class */
public interface AlterDatabaseStep {
    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseFinalStep renameTo(String str);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseFinalStep renameTo(Name name);

    @Support({SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDatabaseFinalStep renameTo(Catalog catalog);
}
