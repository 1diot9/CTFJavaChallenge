package org.h2.util;

import java.util.ArrayList;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/CacheSecondLevel.class */
class CacheSecondLevel implements Cache {
    private final Cache baseCache;
    private final Map<Integer, CacheObject> map;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheSecondLevel(Cache cache, Map<Integer, CacheObject> map) {
        this.baseCache = cache;
        this.map = map;
    }

    @Override // org.h2.util.Cache
    public void clear() {
        this.map.clear();
        this.baseCache.clear();
    }

    @Override // org.h2.util.Cache
    public CacheObject find(int i) {
        CacheObject find = this.baseCache.find(i);
        if (find == null) {
            find = this.map.get(Integer.valueOf(i));
        }
        return find;
    }

    @Override // org.h2.util.Cache
    public CacheObject get(int i) {
        CacheObject cacheObject = this.baseCache.get(i);
        if (cacheObject == null) {
            cacheObject = this.map.get(Integer.valueOf(i));
        }
        return cacheObject;
    }

    @Override // org.h2.util.Cache
    public ArrayList<CacheObject> getAllChanged() {
        return this.baseCache.getAllChanged();
    }

    @Override // org.h2.util.Cache
    public int getMaxMemory() {
        return this.baseCache.getMaxMemory();
    }

    @Override // org.h2.util.Cache
    public int getMemory() {
        return this.baseCache.getMemory();
    }

    @Override // org.h2.util.Cache
    public void put(CacheObject cacheObject) {
        this.baseCache.put(cacheObject);
        this.map.put(Integer.valueOf(cacheObject.getPos()), cacheObject);
    }

    @Override // org.h2.util.Cache
    public boolean remove(int i) {
        return this.baseCache.remove(i) | (this.map.remove(Integer.valueOf(i)) != null);
    }

    @Override // org.h2.util.Cache
    public void setMaxMemory(int i) {
        this.baseCache.setMaxMemory(i);
    }

    @Override // org.h2.util.Cache
    public CacheObject update(int i, CacheObject cacheObject) {
        CacheObject update = this.baseCache.update(i, cacheObject);
        this.map.put(Integer.valueOf(i), cacheObject);
        return update;
    }
}
