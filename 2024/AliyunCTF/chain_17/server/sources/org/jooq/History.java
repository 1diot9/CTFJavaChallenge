package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataMigrationVerificationException;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/History.class */
public interface History extends Iterable<Version>, Scope {
    @NotNull
    @ApiStatus.Experimental
    Version root() throws DataMigrationVerificationException;

    @NotNull
    @ApiStatus.Experimental
    Version current();

    @ApiStatus.Experimental
    void resolve(String str);
}
