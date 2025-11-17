package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Files.class */
public interface Files extends Iterable<File> {
    @NotNull
    @ApiStatus.Experimental
    Version from();

    @NotNull
    @ApiStatus.Experimental
    Version to();
}
