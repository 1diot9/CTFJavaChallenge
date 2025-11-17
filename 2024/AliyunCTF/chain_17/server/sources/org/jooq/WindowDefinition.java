package org.jooq;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WindowDefinition.class */
public interface WindowDefinition extends WindowSpecificationOrderByStep {
    @ApiStatus.Experimental
    @NotNull
    Name $name();

    @ApiStatus.Experimental
    @Nullable
    WindowSpecification $windowSpecification();
}
