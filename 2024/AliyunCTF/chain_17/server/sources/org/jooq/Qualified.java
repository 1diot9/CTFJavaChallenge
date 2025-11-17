package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Qualified.class */
public interface Qualified extends Named {
    @Nullable
    Catalog getCatalog();

    @Nullable
    Schema getSchema();

    @ApiStatus.Experimental
    @Nullable
    Schema $schema();
}
