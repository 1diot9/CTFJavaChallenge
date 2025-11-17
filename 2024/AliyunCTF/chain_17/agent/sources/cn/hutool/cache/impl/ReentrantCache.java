package cn.hutool.cache.impl;

import cn.hutool.core.collection.CopiedIter;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/ReentrantCache.class */
public abstract class ReentrantCache<K, V> extends AbstractCache<K, V> {
    private static final long serialVersionUID = 1;
    protected final ReentrantLock lock = new ReentrantLock();

    @Override // cn.hutool.cache.Cache
    public void put(K key, V object, long timeout) {
        this.lock.lock();
        try {
            putWithoutLock(key, object, timeout);
            this.lock.unlock();
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    @Override // cn.hutool.cache.Cache
    public boolean containsKey(K key) {
        this.lock.lock();
        try {
            CacheObj<K, V> co = getWithoutLock(key);
            if (co == null) {
                return false;
            }
            if (false == co.isExpired()) {
                this.lock.unlock();
                return true;
            }
            this.lock.unlock();
            remove(key, true);
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // cn.hutool.cache.Cache
    public V get(K key, boolean isUpdateLastAccess) {
        this.lock.lock();
        try {
            CacheObj<K, V> co = getWithoutLock(key);
            this.lock.unlock();
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
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    @Override // cn.hutool.cache.Cache
    public Iterator<CacheObj<K, V>> cacheObjIterator() {
        this.lock.lock();
        try {
            CopiedIter<CacheObj<K, V>> copiedIterator = CopiedIter.copyOf(cacheObjIter());
            return new CacheObjIterator(copiedIterator);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // cn.hutool.cache.Cache
    public final int prune() {
        this.lock.lock();
        try {
            return pruneCache();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // cn.hutool.cache.Cache
    public void remove(K key) {
        remove(key, false);
    }

    @Override // cn.hutool.cache.Cache
    public void clear() {
        this.lock.lock();
        try {
            this.cacheMap.clear();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // cn.hutool.cache.impl.AbstractCache
    public String toString() {
        this.lock.lock();
        try {
            return super.toString();
        } finally {
            this.lock.unlock();
        }
    }

    private void remove(K key, boolean withMissCount) {
        this.lock.lock();
        try {
            CacheObj<K, V> co = removeWithoutLock(key, withMissCount);
            this.lock.unlock();
            if (null != co) {
                onRemove(co.key, co.obj);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }
}
