package cn.hutool.cache.impl;

import cn.hutool.cache.GlobalPruneTimer;
import cn.hutool.core.lang.mutable.Mutable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/TimedCache.class */
public class TimedCache<K, V> extends StampedCache<K, V> {
    private static final long serialVersionUID = 1;
    private ScheduledFuture<?> pruneJobFuture;

    public TimedCache(long timeout) {
        this(timeout, new HashMap());
    }

    public TimedCache(long timeout, Map<Mutable<K>, CacheObj<K, V>> map) {
        this.capacity = 0;
        this.timeout = timeout;
        this.cacheMap = map;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.cache.impl.AbstractCache
    public int pruneCache() {
        int count = 0;
        Iterator<CacheObj<K, V>> values = cacheObjIter();
        while (values.hasNext()) {
            CacheObj<K, V> co = values.next();
            if (co.isExpired()) {
                values.remove();
                onRemove(co.key, co.obj);
                count++;
            }
        }
        return count;
    }

    public void schedulePrune(long delay) {
        this.pruneJobFuture = GlobalPruneTimer.INSTANCE.schedule(this::prune, delay);
    }

    public void cancelPruneSchedule() {
        if (null != this.pruneJobFuture) {
            this.pruneJobFuture.cancel(true);
        }
    }
}
