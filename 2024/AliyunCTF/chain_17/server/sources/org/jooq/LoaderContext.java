package org.jooq;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderContext.class */
public interface LoaderContext {
    @CheckReturnValue
    @NotNull
    LoaderContext row(Object[] objArr);

    @CheckReturnValue
    @Nullable
    Object[] row();

    @CheckReturnValue
    @NotNull
    List<LoaderError> errors();

    int processed();

    int executed();

    int ignored();

    int stored();
}
