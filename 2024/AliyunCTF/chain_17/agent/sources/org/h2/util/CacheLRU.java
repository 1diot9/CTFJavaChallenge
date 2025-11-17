package org.h2.util;

import ch.qos.logback.core.util.FileSize;
import java.util.ArrayList;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/CacheLRU.class */
public class CacheLRU implements Cache {
    static final String TYPE_NAME = "LRU";
    private final CacheWriter writer;
    private final boolean fifo;
    private final CacheObject head = new CacheHead();
    private final int mask;
    private CacheObject[] values;
    private int recordCount;
    private final int len;
    private long maxMemory;
    private long memory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheLRU(CacheWriter cacheWriter, int i, boolean z) {
        this.writer = cacheWriter;
        this.fifo = z;
        setMaxMemory(i);
        try {
            long j = this.maxMemory / 64;
            if (j > 2147483647L) {
                throw new IllegalArgumentException();
            }
            this.len = MathUtils.nextPowerOf2((int) j);
            this.mask = this.len - 1;
            clear();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("This much cache memory is not supported: " + i + "kb", e);
        }
    }

    public static Cache getCache(CacheWriter cacheWriter, String str, int i) {
        Cache cacheTQ;
        SoftValuesHashMap softValuesHashMap = null;
        if (str.startsWith("SOFT_")) {
            softValuesHashMap = new SoftValuesHashMap();
            str = str.substring("SOFT_".length());
        }
        if ("LRU".equals(str)) {
            cacheTQ = new CacheLRU(cacheWriter, i, false);
        } else if ("TQ".equals(str)) {
            cacheTQ = new CacheTQ(cacheWriter, i);
        } else {
            throw DbException.getInvalidValueException("CACHE_TYPE", str);
        }
        if (softValuesHashMap != null) {
            cacheTQ = new CacheSecondLevel(cacheTQ, softValuesHashMap);
        }
        return cacheTQ;
    }

    @Override // org.h2.util.Cache
    public void clear() {
        CacheObject cacheObject = this.head;
        CacheObject cacheObject2 = this.head;
        CacheObject cacheObject3 = this.head;
        cacheObject2.cachePrevious = cacheObject3;
        cacheObject.cacheNext = cacheObject3;
        this.values = null;
        this.values = new CacheObject[this.len];
        this.recordCount = 0;
        this.memory = this.len * 8;
    }

    @Override // org.h2.util.Cache
    public void put(CacheObject cacheObject) {
        if (SysProperties.CHECK) {
            int pos = cacheObject.getPos();
            if (find(pos) != null) {
                throw DbException.getInternalError("try to add a record twice at pos " + pos);
            }
        }
        int pos2 = cacheObject.getPos() & this.mask;
        cacheObject.cacheChained = this.values[pos2];
        this.values[pos2] = cacheObject;
        this.recordCount++;
        this.memory += cacheObject.getMemory();
        addToFront(cacheObject);
        removeOldIfRequired();
    }

    @Override // org.h2.util.Cache
    public CacheObject update(int i, CacheObject cacheObject) {
        CacheObject find = find(i);
        if (find == null) {
            put(cacheObject);
        } else {
            if (find != cacheObject) {
                throw DbException.getInternalError("old!=record pos:" + i + " old:" + find + " new:" + cacheObject);
            }
            if (!this.fifo) {
                removeFromLinkedList(cacheObject);
                addToFront(cacheObject);
            }
        }
        return find;
    }

    private void removeOldIfRequired() {
        if (this.memory >= this.maxMemory) {
            removeOld();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0068  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00be  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00b8 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void removeOld() {
        /*
            Method dump skipped, instructions count: 402
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.util.CacheLRU.removeOld():void");
    }

    private void addToFront(CacheObject cacheObject) {
        if (cacheObject == this.head) {
            throw DbException.getInternalError("try to move head");
        }
        cacheObject.cacheNext = this.head;
        cacheObject.cachePrevious = this.head.cachePrevious;
        cacheObject.cachePrevious.cacheNext = cacheObject;
        this.head.cachePrevious = cacheObject;
    }

    private void removeFromLinkedList(CacheObject cacheObject) {
        if (cacheObject == this.head) {
            throw DbException.getInternalError("try to remove head");
        }
        cacheObject.cachePrevious.cacheNext = cacheObject.cacheNext;
        cacheObject.cacheNext.cachePrevious = cacheObject.cachePrevious;
        cacheObject.cacheNext = null;
        cacheObject.cachePrevious = null;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x006b  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0094 A[ORIG_RETURN, RETURN] */
    @Override // org.h2.util.Cache
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean remove(int r7) {
        /*
            r6 = this;
            r0 = r7
            r1 = r6
            int r1 = r1.mask
            r0 = r0 & r1
            r8 = r0
            r0 = r6
            org.h2.util.CacheObject[] r0 = r0.values
            r1 = r8
            r0 = r0[r1]
            r9 = r0
            r0 = r9
            if (r0 != 0) goto L14
            r0 = 0
            return r0
        L14:
            r0 = r9
            int r0 = r0.getPos()
            r1 = r7
            if (r0 != r1) goto L29
            r0 = r6
            org.h2.util.CacheObject[] r0 = r0.values
            r1 = r8
            r2 = r9
            org.h2.util.CacheObject r2 = r2.cacheChained
            r0[r1] = r2
            goto L48
        L29:
            r0 = r9
            r10 = r0
            r0 = r9
            org.h2.util.CacheObject r0 = r0.cacheChained
            r9 = r0
            r0 = r9
            if (r0 != 0) goto L37
            r0 = 0
            return r0
        L37:
            r0 = r9
            int r0 = r0.getPos()
            r1 = r7
            if (r0 != r1) goto L29
            r0 = r10
            r1 = r9
            org.h2.util.CacheObject r1 = r1.cacheChained
            r0.cacheChained = r1
        L48:
            r0 = r6
            r1 = r0
            int r1 = r1.recordCount
            r2 = 1
            int r1 = r1 - r2
            r0.recordCount = r1
            r0 = r6
            r1 = r0
            long r1 = r1.memory
            r2 = r9
            int r2 = r2.getMemory()
            long r2 = (long) r2
            long r1 = r1 - r2
            r0.memory = r1
            r0 = r6
            r1 = r9
            r0.removeFromLinkedList(r1)
            boolean r0 = org.h2.engine.SysProperties.CHECK
            if (r0 == 0) goto L94
            r0 = r9
            r1 = 0
            r0.cacheChained = r1
            r0 = r6
            r1 = r7
            org.h2.util.CacheObject r0 = r0.find(r1)
            r10 = r0
            r0 = r10
            if (r0 == 0) goto L94
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = r0
            r1.<init>()
            java.lang.String r1 = "not removed: "
            java.lang.StringBuilder r0 = r0.append(r1)
            r1 = r10
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.RuntimeException r0 = org.h2.message.DbException.getInternalError(r0)
            throw r0
        L94:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.util.CacheLRU.remove(int):boolean");
    }

    @Override // org.h2.util.Cache
    public CacheObject find(int i) {
        CacheObject cacheObject;
        CacheObject cacheObject2 = this.values[i & this.mask];
        while (true) {
            cacheObject = cacheObject2;
            if (cacheObject == null || cacheObject.getPos() == i) {
                break;
            }
            cacheObject2 = cacheObject.cacheChained;
        }
        return cacheObject;
    }

    @Override // org.h2.util.Cache
    public CacheObject get(int i) {
        CacheObject find = find(i);
        if (find != null && !this.fifo) {
            removeFromLinkedList(find);
            addToFront(find);
        }
        return find;
    }

    @Override // org.h2.util.Cache
    public ArrayList<CacheObject> getAllChanged() {
        ArrayList<CacheObject> arrayList = new ArrayList<>();
        CacheObject cacheObject = this.head.cacheNext;
        while (true) {
            CacheObject cacheObject2 = cacheObject;
            if (cacheObject2 != this.head) {
                if (cacheObject2.isChanged()) {
                    arrayList.add(cacheObject2);
                }
                cacheObject = cacheObject2.cacheNext;
            } else {
                return arrayList;
            }
        }
    }

    @Override // org.h2.util.Cache
    public void setMaxMemory(int i) {
        long j = (i * FileSize.KB_COEFFICIENT) / 4;
        this.maxMemory = j < 0 ? 0L : j;
        removeOldIfRequired();
    }

    @Override // org.h2.util.Cache
    public int getMaxMemory() {
        return (int) ((this.maxMemory * 4) / FileSize.KB_COEFFICIENT);
    }

    @Override // org.h2.util.Cache
    public int getMemory() {
        return (int) ((this.memory * 4) / FileSize.KB_COEFFICIENT);
    }
}
