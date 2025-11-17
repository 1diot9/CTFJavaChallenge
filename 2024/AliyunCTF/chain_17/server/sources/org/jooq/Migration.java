package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataMigrationException;
import org.jooq.exception.DataMigrationVerificationException;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Migration.class */
public interface Migration extends Scope {
    Commit from();

    Commit to();

    @NotNull
    Queries queries();

    void verify() throws DataMigrationVerificationException;

    @Blocking
    void execute() throws DataMigrationException;
}
