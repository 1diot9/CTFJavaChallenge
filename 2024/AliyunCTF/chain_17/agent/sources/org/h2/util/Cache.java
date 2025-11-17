package org.h2.util;

import java.util.ArrayList;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/Cache.class */
public interface Cache {
    ArrayList<CacheObject> getAllChanged();

    void clear();

    CacheObject get(int i);

    void put(CacheObject cacheObject);

    CacheObject update(int i, CacheObject cacheObject);

    boolean remove(int i);

    CacheObject find(int i);

    void setMaxMemory(int i);

    int getMaxMemory();

    int getMemory();
}
