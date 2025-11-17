package cn.hutool.cache.impl;

import cn.hutool.cache.CacheListener;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.map.WeakConcurrentMap;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/WeakCache.class */
public class WeakCache<K, V> extends TimedCache<K, V> {
    private static final long serialVersionUID = 1;

    public WeakCache(long timeout) {
        super(timeout, new WeakConcurrentMap());
    }

    @Override // cn.hutool.cache.impl.AbstractCache, cn.hutool.cache.Cache
    public WeakCache<K, V> setListener(CacheListener<K, V> listener) {
        super.setListener((CacheListener) listener);
        WeakConcurrentMap<Mutable<K>, CacheObj<K, V>> map = (WeakConcurrentMap) this.cacheMap;
        map.setPurgeListener((key, value) -> {
            listener.onRemove(Opt.ofNullable(key).map((v0) -> {
                return v0.get();
            }).map((v0) -> {
                return v0.get2();
            }).get(), value.getValue());
        });
        return this;
    }
}
