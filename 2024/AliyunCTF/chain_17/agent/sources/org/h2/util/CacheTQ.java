package org.h2.util;

import java.util.ArrayList;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/CacheTQ.class */
public class CacheTQ implements Cache {
    static final String TYPE_NAME = "TQ";
    private final Cache lru;
    private final Cache fifo;
    private final SmallLRUCache<Integer, Object> recentlyUsed = SmallLRUCache.newInstance(1024);
    private int lastUsed = -1;
    private int maxMemory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheTQ(CacheWriter cacheWriter, int i) {
        this.maxMemory = i;
        this.lru = new CacheLRU(cacheWriter, (int) (i * 0.8d), false);
        this.fifo = new CacheLRU(cacheWriter, (int) (i * 0.2d), true);
        setMaxMemory(4 * i);
    }

    @Override // org.h2.util.Cache
    public void clear() {
        this.lru.clear();
        this.fifo.clear();
        this.recentlyUsed.clear();
        this.lastUsed = -1;
    }

    @Override // org.h2.util.Cache
    public CacheObject find(int i) {
        CacheObject find = this.lru.find(i);
        if (find == null) {
            find = this.fifo.find(i);
        }
        return find;
    }

    @Override // org.h2.util.Cache
    public CacheObject get(int i) {
        CacheObject find = this.lru.find(i);
        if (find != null) {
            return find;
        }
        CacheObject find2 = this.fifo.find(i);
        if (find2 != null && !find2.isStream()) {
            if (this.recentlyUsed.get(Integer.valueOf(i)) != null) {
                if (this.lastUsed != i) {
                    this.fifo.remove(i);
                    this.lru.put(find2);
                }
            } else {
                this.recentlyUsed.put(Integer.valueOf(i), this);
            }
            this.lastUsed = i;
        }
        return find2;
    }

    @Override // org.h2.util.Cache
    public ArrayList<CacheObject> getAllChanged() {
        ArrayList<CacheObject> allChanged = this.lru.getAllChanged();
        ArrayList<CacheObject> allChanged2 = this.fifo.getAllChanged();
        ArrayList<CacheObject> arrayList = new ArrayList<>(allChanged.size() + allChanged2.size());
        arrayList.addAll(allChanged);
        arrayList.addAll(allChanged2);
        return arrayList;
    }

    @Override // org.h2.util.Cache
    public int getMaxMemory() {
        return this.maxMemory;
    }

    @Override // org.h2.util.Cache
    public int getMemory() {
        return this.lru.getMemory() + this.fifo.getMemory();
    }

    @Override // org.h2.util.Cache
    public void put(CacheObject cacheObject) {
        if (cacheObject.isStream()) {
            this.fifo.put(cacheObject);
        } else if (this.recentlyUsed.get(Integer.valueOf(cacheObject.getPos())) != null) {
            this.lru.put(cacheObject);
        } else {
            this.fifo.put(cacheObject);
            this.lastUsed = cacheObject.getPos();
        }
    }

    @Override // org.h2.util.Cache
    public boolean remove(int i) {
        boolean remove = this.lru.remove(i);
        if (!remove) {
            remove = this.fifo.remove(i);
        }
        this.recentlyUsed.remove(Integer.valueOf(i));
        return remove;
    }

    @Override // org.h2.util.Cache
    public void setMaxMemory(int i) {
        this.maxMemory = i;
        this.lru.setMaxMemory((int) (i * 0.8d));
        this.fifo.setMaxMemory((int) (i * 0.2d));
        this.recentlyUsed.setMaxSize(4 * i);
    }

    @Override // org.h2.util.Cache
    public CacheObject update(int i, CacheObject cacheObject) {
        if (this.lru.find(i) != null) {
            return this.lru.update(i, cacheObject);
        }
        return this.fifo.update(i, cacheObject);
    }
}
