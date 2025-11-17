package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheOperationSource.class */
public interface CacheOperationSource {
    @Nullable
    Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass);

    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }
}
