package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheOperationSourcePointcut.class */
public class CacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

    @Nullable
    private CacheOperationSource cacheOperationSource;

    public CacheOperationSourcePointcut() {
        setClassFilter(new CacheOperationSourceClassFilter());
    }

    public void setCacheOperationSource(@Nullable CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        return this.cacheOperationSource == null || !CollectionUtils.isEmpty(this.cacheOperationSource.getCacheOperations(method, targetClass));
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof CacheOperationSourcePointcut) {
                CacheOperationSourcePointcut that = (CacheOperationSourcePointcut) other;
                if (ObjectUtils.nullSafeEquals(this.cacheOperationSource, that.cacheOperationSource)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return CacheOperationSourcePointcut.class.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + this.cacheOperationSource;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheOperationSourcePointcut$CacheOperationSourceClassFilter.class */
    private class CacheOperationSourceClassFilter implements ClassFilter {
        private CacheOperationSourceClassFilter() {
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            if (CacheManager.class.isAssignableFrom(clazz)) {
                return false;
            }
            return CacheOperationSourcePointcut.this.cacheOperationSource == null || CacheOperationSourcePointcut.this.cacheOperationSource.isCandidateClass(clazz);
        }

        private CacheOperationSource getCacheOperationSource() {
            return CacheOperationSourcePointcut.this.cacheOperationSource;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof CacheOperationSourceClassFilter) {
                    CacheOperationSourceClassFilter that = (CacheOperationSourceClassFilter) other;
                    if (ObjectUtils.nullSafeEquals(CacheOperationSourcePointcut.this.cacheOperationSource, that.getCacheOperationSource())) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return CacheOperationSourceClassFilter.class.hashCode();
        }

        public String toString() {
            return CacheOperationSourceClassFilter.class.getName() + ": " + CacheOperationSourcePointcut.this.cacheOperationSource;
        }
    }
}
