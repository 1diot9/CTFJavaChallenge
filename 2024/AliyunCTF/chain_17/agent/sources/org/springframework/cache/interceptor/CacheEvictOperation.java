package org.springframework.cache.interceptor;

import org.springframework.cache.interceptor.CacheOperation;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheEvictOperation.class */
public class CacheEvictOperation extends CacheOperation {
    private final boolean cacheWide;
    private final boolean beforeInvocation;

    public CacheEvictOperation(Builder b) {
        super(b);
        this.cacheWide = b.cacheWide;
        this.beforeInvocation = b.beforeInvocation;
    }

    public boolean isCacheWide() {
        return this.cacheWide;
    }

    public boolean isBeforeInvocation() {
        return this.beforeInvocation;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheEvictOperation$Builder.class */
    public static class Builder extends CacheOperation.Builder {
        private boolean cacheWide = false;
        private boolean beforeInvocation = false;

        public void setCacheWide(boolean cacheWide) {
            this.cacheWide = cacheWide;
        }

        public void setBeforeInvocation(boolean beforeInvocation) {
            this.beforeInvocation = beforeInvocation;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.cache.interceptor.CacheOperation.Builder
        public StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(',');
            sb.append(this.cacheWide);
            sb.append(',');
            sb.append(this.beforeInvocation);
            return sb;
        }

        @Override // org.springframework.cache.interceptor.CacheOperation.Builder
        public CacheEvictOperation build() {
            return new CacheEvictOperation(this);
        }
    }
}
