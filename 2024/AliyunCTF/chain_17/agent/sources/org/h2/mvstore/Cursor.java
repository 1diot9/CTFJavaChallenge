package org.h2.mvstore;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/Cursor.class */
public final class Cursor<K, V> implements Iterator<K> {
    private final boolean reverse;
    private final K to;
    private CursorPos<K, V> cursorPos;
    private CursorPos<K, V> keeper;
    private K current;
    private K last;
    private V lastValue;
    private Page<K, V> lastPage;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Cursor.class.desiredAssertionStatus();
    }

    public Cursor(RootReference<K, V> rootReference, K k, K k2) {
        this(rootReference, k, k2, false);
    }

    public Cursor(RootReference<K, V> rootReference, K k, K k2, boolean z) {
        this.lastPage = rootReference.root;
        this.cursorPos = traverseDown(this.lastPage, k, z);
        this.to = k2;
        this.reverse = z;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        K key;
        if (this.cursorPos != null) {
            int i = this.reverse ? -1 : 1;
            while (this.current == null) {
                Page<K, V> page = this.cursorPos.page;
                int i2 = this.cursorPos.index;
                if (!this.reverse ? i2 >= upperBound(page) : i2 < 0) {
                    CursorPos<K, V> cursorPos = this.cursorPos;
                    this.cursorPos = this.cursorPos.parent;
                    if (this.cursorPos == null) {
                        return false;
                    }
                    cursorPos.parent = this.keeper;
                    this.keeper = cursorPos;
                } else {
                    while (!page.isLeaf()) {
                        page = page.getChildPage(i2);
                        i2 = this.reverse ? upperBound(page) - 1 : 0;
                        if (this.keeper == null) {
                            this.cursorPos = new CursorPos<>(page, i2, this.cursorPos);
                        } else {
                            CursorPos<K, V> cursorPos2 = this.keeper;
                            this.keeper = this.keeper.parent;
                            cursorPos2.parent = this.cursorPos;
                            cursorPos2.page = page;
                            cursorPos2.index = i2;
                            this.cursorPos = cursorPos2;
                        }
                    }
                    if (this.reverse) {
                        if (i2 < 0) {
                            continue;
                        }
                        key = page.getKey(i2);
                        if (this.to == null && Integer.signum(page.map.getKeyType().compare(key, this.to)) == i) {
                            return false;
                        }
                        this.last = key;
                        this.current = key;
                        this.lastValue = page.getValue(i2);
                        this.lastPage = page;
                    } else {
                        if (i2 >= page.getKeyCount()) {
                            continue;
                        }
                        key = page.getKey(i2);
                        if (this.to == null) {
                        }
                        this.last = key;
                        this.current = key;
                        this.lastValue = page.getValue(i2);
                        this.lastPage = page;
                    }
                }
                this.cursorPos.index += i;
            }
        }
        return this.current != null;
    }

    @Override // java.util.Iterator
    public K next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        this.current = null;
        return this.last;
    }

    public K getKey() {
        return this.last;
    }

    public V getValue() {
        return this.lastValue;
    }

    Page<K, V> getPage() {
        return this.lastPage;
    }

    public void skip(long j) {
        CursorPos<K, V> cursorPos;
        if (j >= 10) {
            if (hasNext()) {
                if (!$assertionsDisabled && this.cursorPos == null) {
                    throw new AssertionError();
                }
                CursorPos<K, V> cursorPos2 = this.cursorPos;
                while (true) {
                    cursorPos = cursorPos2;
                    CursorPos<K, V> cursorPos3 = cursorPos.parent;
                    if (cursorPos3 == null) {
                        break;
                    } else {
                        cursorPos2 = cursorPos3;
                    }
                }
                Page<K, V> page = cursorPos.page;
                MVMap<K, V> mVMap = page.map;
                this.last = mVMap.getKey(mVMap.getKeyIndex(next()) + (this.reverse ? -j : j));
                this.cursorPos = traverseDown(page, this.last, this.reverse);
                return;
            }
            return;
        }
        while (true) {
            long j2 = j;
            j = j2 - 1;
            if (j2 > 0 && hasNext()) {
                next();
            } else {
                return;
            }
        }
    }

    static <K, V> CursorPos<K, V> traverseDown(Page<K, V> page, K k, boolean z) {
        CursorPos<K, V> appendCursorPos;
        if (k != null) {
            appendCursorPos = CursorPos.traverseDown(page, k);
        } else {
            appendCursorPos = z ? page.getAppendCursorPos(null) : page.getPrependCursorPos(null);
        }
        CursorPos<K, V> cursorPos = appendCursorPos;
        int i = cursorPos.index;
        if (i < 0) {
            int i2 = i ^ (-1);
            if (z) {
                i2--;
            }
            cursorPos.index = i2;
        }
        return cursorPos;
    }

    private static <K, V> int upperBound(Page<K, V> page) {
        return page.isLeaf() ? page.getKeyCount() : page.map.getChildPageCount(page);
    }
}
