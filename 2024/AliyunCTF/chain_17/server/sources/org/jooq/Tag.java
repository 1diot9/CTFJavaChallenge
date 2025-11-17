package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Tag.class */
public interface Tag {
    @NotNull
    String id();

    @Nullable
    String message();
}
