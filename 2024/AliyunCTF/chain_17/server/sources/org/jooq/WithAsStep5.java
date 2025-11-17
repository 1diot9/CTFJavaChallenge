package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/WithAsStep5.class */
public interface WithAsStep5 {
    @Support
    @CheckReturnValue
    @NotNull
    WithStep as(ResultQuery<? extends Record5<?, ?, ?, ?, ?>> resultQuery);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep asMaterialized(ResultQuery<? extends Record5<?, ?, ?, ?, ?>> resultQuery);

    @Support
    @CheckReturnValue
    @NotNull
    WithStep asNotMaterialized(ResultQuery<? extends Record5<?, ?, ?, ?, ?>> resultQuery);
}
