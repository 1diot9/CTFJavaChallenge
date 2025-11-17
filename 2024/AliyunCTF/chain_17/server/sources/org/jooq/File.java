package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/File.class */
public interface File {
    @ApiStatus.Experimental
    @NotNull
    String path();

    @ApiStatus.Experimental
    @NotNull
    String name();

    @ApiStatus.Experimental
    @Nullable
    String content();

    @ApiStatus.Experimental
    @NotNull
    ContentType type();
}
