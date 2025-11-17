package org.h2.util;

import java.util.HashMap;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SmallMap.class */
public class SmallMap {
    private final HashMap<Integer, Object> map = new HashMap<>();
    private Object cache;
    private int cacheId;
    private int lastId;
    private final int maxElements;

    public SmallMap(int i) {
        this.maxElements = i;
    }

    public int addObject(int i, Object obj) {
        if (this.map.size() > this.maxElements * 2) {
            Iterator<Integer> it = this.map.keySet().iterator();
            while (it.hasNext()) {
                if (it.next().intValue() + this.maxElements < this.lastId) {
                    it.remove();
                }
            }
        }
        if (i > this.lastId) {
            this.lastId = i;
        }
        this.map.put(Integer.valueOf(i), obj);
        this.cacheId = i;
        this.cache = obj;
        return i;
    }

    public void freeObject(int i) {
        if (this.cacheId == i) {
            this.cacheId = -1;
            this.cache = null;
        }
        this.map.remove(Integer.valueOf(i));
    }

    public Object getObject(int i, boolean z) {
        if (i == this.cacheId) {
            return this.cache;
        }
        Object obj = this.map.get(Integer.valueOf(i));
        if (obj == null && !z) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        return obj;
    }
}
