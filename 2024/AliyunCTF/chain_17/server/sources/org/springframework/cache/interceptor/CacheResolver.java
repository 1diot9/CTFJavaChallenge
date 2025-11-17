package org.springframework.cache.interceptor;

import java.util.Collection;
import org.springframework.cache.Cache;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheResolver.class */
public interface CacheResolver {
    Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context);
}
