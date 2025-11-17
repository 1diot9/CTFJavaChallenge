package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Migrations.class */
public interface Migrations extends Scope {
    @ApiStatus.Experimental
    @NotNull
    File file(String str, String str2, ContentType contentType);

    @ApiStatus.Experimental
    @NotNull
    Version version(String str);

    @ApiStatus.Experimental
    @NotNull
    Commits commits();

    @ApiStatus.Experimental
    @NotNull
    History history();

    @ApiStatus.Experimental
    @NotNull
    Migration migrateTo(Commit commit);
}
