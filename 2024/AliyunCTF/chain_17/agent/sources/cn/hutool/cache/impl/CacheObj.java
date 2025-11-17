package cn.hutool.cache.impl;

import cn.hutool.core.date.DateUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/CacheObj.class */
public class CacheObj<K, V> implements Serializable {
    private static final long serialVersionUID = 1;
    protected final K key;
    protected final V obj;
    protected final long ttl;
    protected AtomicLong accessCount = new AtomicLong();
    protected volatile long lastAccess = System.currentTimeMillis();

    /* JADX INFO: Access modifiers changed from: protected */
    public CacheObj(K key, V obj, long ttl) {
        this.key = key;
        this.obj = obj;
        this.ttl = ttl;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.obj;
    }

    public long getTtl() {
        return this.ttl;
    }

    public Date getExpiredTime() {
        if (this.ttl > 0) {
            return DateUtil.date(this.lastAccess + this.ttl);
        }
        return null;
    }

    public long getLastAccess() {
        return this.lastAccess;
    }

    public String toString() {
        return "CacheObj [key=" + this.key + ", obj=" + this.obj + ", lastAccess=" + this.lastAccess + ", accessCount=" + this.accessCount + ", ttl=" + this.ttl + "]";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isExpired() {
        return this.ttl > 0 && System.currentTimeMillis() - this.lastAccess > this.ttl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public V get(boolean isUpdateLastAccess) {
        if (isUpdateLastAccess) {
            this.lastAccess = System.currentTimeMillis();
        }
        this.accessCount.getAndIncrement();
        return this.obj;
    }
}
