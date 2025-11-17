package cn.hutool.cache.impl;

import cn.hutool.core.collection.CopiedIter;
import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/StampedCache.class */
public abstract class StampedCache<K, V> extends AbstractCache<K, V> {
    private static final long serialVersionUID = 1;
    protected final StampedLock lock = new StampedLock();

    @Override // cn.hutool.cache.Cache
    public void put(K key, V object, long timeout) {
        long stamp = this.lock.writeLock();
        try {
            putWithoutLock(key, object, timeout);
            this.lock.unlockWrite(stamp);
        } catch (Throwable th) {
            this.lock.unlockWrite(stamp);
            throw th;
        }
    }

    @Override // cn.hutool.cache.Cache
    public boolean containsKey(K key) {
        long stamp = this.lock.readLock();
        try {
            CacheObj<K, V> co = getWithoutLock(key);
            if (co == null) {
                return false;
            }
            if (false == co.isExpired()) {
                this.lock.unlockRead(stamp);
                return true;
            }
            this.lock.unlockRead(stamp);
            remove(key, true);
            return false;
        } finally {
            this.lock.unlockRead(stamp);
        }
    }

    @Override // cn.hutool.cache.Cache
    public V get(K key, boolean isUpdateLastAccess) {
        long stamp = this.lock.tryOptimisticRead();
        CacheObj<K, V> co = getWithoutLock(key);
        if (false == this.lock.validate(stamp)) {
            long stamp2 = this.lock.readLock();
            try {
                co = getWithoutLock(key);
                this.lock.unlockRead(stamp2);
            } catch (Throwable th) {
                this.lock.unlockRead(stamp2);
                throw th;
            }
        }
        if (null == co) {
            this.missCount.increment();
            return null;
        }
        if (false == co.isExpired()) {
            this.hitCount.increment();
            return co.get(isUpdateLastAccess);
        }
        remove(key, true);
        return null;
    }

    @Override // cn.hutool.cache.Cache
    public Iterator<CacheObj<K, V>> cacheObjIterator() {
        long stamp = this.lock.readLock();
        try {
            CopiedIter<CacheObj<K, V>> copiedIterator = CopiedIter.copyOf(cacheObjIter());
            this.lock.unlockRead(stamp);
            return new CacheObjIterator(copiedIterator);
        } catch (Throwable th) {
            this.lock.unlockRead(stamp);
            throw th;
        }
    }

    @Override // cn.hutool.cache.Cache
    public final int prune() {
        long stamp = this.lock.writeLock();
        try {
            int pruneCache = pruneCache();
            this.lock.unlockWrite(stamp);
            return pruneCache;
        } catch (Throwable th) {
            this.lock.unlockWrite(stamp);
            throw th;
        }
    }

    @Override // cn.hutool.cache.Cache
    public void remove(K key) {
        remove(key, false);
    }

    @Override // cn.hutool.cache.Cache
    public void clear() {
        long stamp = this.lock.writeLock();
        try {
            this.cacheMap.clear();
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    private void remove(K key, boolean withMissCount) {
        long stamp = this.lock.writeLock();
        try {
            CacheObj<K, V> co = removeWithoutLock(key, withMissCount);
            this.lock.unlockWrite(stamp);
            if (null != co) {
                onRemove(co.key, co.obj);
            }
        } catch (Throwable th) {
            this.lock.unlockWrite(stamp);
            throw th;
        }
    }
}
