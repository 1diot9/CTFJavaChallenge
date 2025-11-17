package org.jooq.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jooq.CacheContext;
import org.jooq.CacheProvider;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultCacheProvider.class */
final class DefaultCacheProvider implements CacheProvider {
    @Override // org.jooq.CacheProvider
    public Map<Object, Object> provide(CacheContext ctx) {
        switch (ctx.cacheType()) {
            case CACHE_PARSING_CONNECTION:
                return Collections.synchronizedMap(new LRUCache(((Integer) StringUtils.defaultIfNull(Tools.settings(ctx.configuration()).getCacheParsingConnectionLRUCacheSize(), 8912)).intValue()));
            default:
                return new ConcurrentHashMap();
        }
    }
}
