package org.jooq;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Node;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Node.class */
public interface Node<N extends Node<N>> extends Scope {
    @NotNull
    String id();

    @Nullable
    String message();

    @NotNull
    N root();

    @NotNull
    List<N> parents();
}
