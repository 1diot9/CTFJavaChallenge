package org.h2.util;

import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/CacheObject.class */
public abstract class CacheObject implements Comparable<CacheObject> {
    public CacheObject cachePrevious;
    public CacheObject cacheNext;
    public CacheObject cacheChained;
    private int pos;
    private boolean changed;

    public abstract boolean canRemove();

    public abstract int getMemory();

    public void setPos(int i) {
        if (this.cachePrevious != null || this.cacheNext != null || this.cacheChained != null) {
            throw DbException.getInternalError("setPos too late");
        }
        this.pos = i;
    }

    public int getPos() {
        return this.pos;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean z) {
        this.changed = z;
    }

    @Override // java.lang.Comparable
    public int compareTo(CacheObject cacheObject) {
        return Integer.compare(getPos(), cacheObject.getPos());
    }

    public boolean isStream() {
        return false;
    }
}
