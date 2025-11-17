package org.h2.util;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/CacheHead.class */
public class CacheHead extends CacheObject {
    @Override // org.h2.util.CacheObject
    public boolean canRemove() {
        return false;
    }

    @Override // org.h2.util.CacheObject
    public int getMemory() {
        return 0;
    }
}
