package org.jooq;

import java.time.Instant;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.conf.Settings;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Scope.class */
public interface Scope {
    @NotNull
    Instant creationTime();

    @NotNull
    Configuration configuration();

    @NotNull
    DSLContext dsl();

    @NotNull
    Settings settings();

    @NotNull
    SQLDialect dialect();

    @NotNull
    SQLDialect family();

    @NotNull
    Map<Object, Object> data();

    @Nullable
    Object data(Object obj);

    @Nullable
    Object data(Object obj, Object obj2);
}
