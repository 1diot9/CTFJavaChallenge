package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CommitProvider.class */
public interface CommitProvider {
    @NotNull
    Commits provide();
}
