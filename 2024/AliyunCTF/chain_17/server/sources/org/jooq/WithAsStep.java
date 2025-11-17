package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WithAsStep.class */
public interface WithAsStep {
    @Support
    @CheckReturnValue
    @NotNull
    WithStep as(ResultQuery<?> resultQuery);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep asMaterialized(ResultQuery<?> resultQuery);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep asNotMaterialized(ResultQuery<?> resultQuery);
}
