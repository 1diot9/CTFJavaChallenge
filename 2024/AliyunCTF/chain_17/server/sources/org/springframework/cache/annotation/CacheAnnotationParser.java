package org.springframework.cache.annotation;

import java.lang.reflect.Method;
import java.util.Collection;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/CacheAnnotationParser.class */
public interface CacheAnnotationParser {
    @Nullable
    Collection<CacheOperation> parseCacheAnnotations(Class<?> type);

    @Nullable
    Collection<CacheOperation> parseCacheAnnotations(Method method);

    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }
}
