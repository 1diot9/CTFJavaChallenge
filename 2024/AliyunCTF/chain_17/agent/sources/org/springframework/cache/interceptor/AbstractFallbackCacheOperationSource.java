package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/AbstractFallbackCacheOperationSource.class */
public abstract class AbstractFallbackCacheOperationSource implements CacheOperationSource {
    private static final Collection<CacheOperation> NULL_CACHING_ATTRIBUTE = Collections.emptyList();
    protected final Log logger = LogFactory.getLog(getClass());
    private final Map<Object, Collection<CacheOperation>> attributeCache = new ConcurrentHashMap(1024);

    @Nullable
    protected abstract Collection<CacheOperation> findCacheOperations(Class<?> clazz);

    @Nullable
    protected abstract Collection<CacheOperation> findCacheOperations(Method method);

    @Override // org.springframework.cache.interceptor.CacheOperationSource
    @Nullable
    public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        Object cacheKey = getCacheKey(method, targetClass);
        Collection<CacheOperation> cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            if (cached != NULL_CACHING_ATTRIBUTE) {
                return cached;
            }
            return null;
        }
        Collection<CacheOperation> cacheOps = computeCacheOperations(method, targetClass);
        if (cacheOps != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
            }
            this.attributeCache.put(cacheKey, cacheOps);
        } else {
            this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
        }
        return cacheOps;
    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    @Nullable
    private Collection<CacheOperation> computeCacheOperations(Method method, @Nullable Class<?> targetClass) {
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        Collection<CacheOperation> opDef = findCacheOperations(specificMethod);
        if (opDef != null) {
            return opDef;
        }
        Collection<CacheOperation> opDef2 = findCacheOperations(specificMethod.getDeclaringClass());
        if (opDef2 != null && ClassUtils.isUserLevelMethod(method)) {
            return opDef2;
        }
        if (specificMethod != method) {
            Collection<CacheOperation> opDef3 = findCacheOperations(method);
            if (opDef3 != null) {
                return opDef3;
            }
            Collection<CacheOperation> opDef4 = findCacheOperations(method.getDeclaringClass());
            if (opDef4 != null && ClassUtils.isUserLevelMethod(method)) {
                return opDef4;
            }
            return null;
        }
        return null;
    }

    protected boolean allowPublicMethodsOnly() {
        return false;
    }
}
