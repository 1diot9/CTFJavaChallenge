package org.springframework.cache;

import java.util.Collection;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/CacheManager.class */
public interface CacheManager {
    @Nullable
    Cache getCache(String name);

    Collection<String> getCacheNames();
}
