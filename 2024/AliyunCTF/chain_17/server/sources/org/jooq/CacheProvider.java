package org.jooq;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CacheProvider.class */
public interface CacheProvider {
    @Nullable
    Map<Object, Object> provide(CacheContext cacheContext);
}
