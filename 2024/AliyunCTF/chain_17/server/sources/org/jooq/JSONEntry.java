package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONEntry.class */
public interface JSONEntry<T> extends QueryPart {
    @NotNull
    Field<String> key();

    @NotNull
    Field<T> value();

    @ApiStatus.Experimental
    @NotNull
    Field<String> $key();

    @ApiStatus.Experimental
    @NotNull
    Field<?> $value();
}
