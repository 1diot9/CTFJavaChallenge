package org.jooq;

import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MigrationContext.class */
public interface MigrationContext extends Scope {
    @NotNull
    Set<Schema> migratedSchemas();

    @NotNull
    Commit migrationFrom();

    @NotNull
    Commit migrationTo();

    @NotNull
    Queries migrationQueries();

    @NotNull
    Commit queriesFrom();

    @NotNull
    Commit queriesTo();

    @NotNull
    Queries queries();

    @NotNull
    Query query();
}
