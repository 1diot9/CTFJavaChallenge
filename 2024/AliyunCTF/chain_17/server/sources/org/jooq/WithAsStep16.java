package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WithAsStep16.class */
public interface WithAsStep16 {
    @Support
    @CheckReturnValue
    @NotNull
    WithStep as(ResultQuery<? extends Record16<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> resultQuery);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep asMaterialized(ResultQuery<? extends Record16<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> resultQuery);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep asNotMaterialized(ResultQuery<? extends Record16<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>> resultQuery);
}
