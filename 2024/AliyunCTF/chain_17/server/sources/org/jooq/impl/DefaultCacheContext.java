package org.jooq.impl;

import org.jooq.CacheContext;
import org.jooq.Configuration;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultCacheContext.class */
public final class DefaultCacheContext extends AbstractScope implements CacheContext {
    private final CacheType cacheType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultCacheContext(Configuration configuration, CacheType cacheType) {
        super(configuration);
        this.cacheType = cacheType;
    }

    @Override // org.jooq.CacheContext
    public final CacheType cacheType() {
        return this.cacheType;
    }
}
