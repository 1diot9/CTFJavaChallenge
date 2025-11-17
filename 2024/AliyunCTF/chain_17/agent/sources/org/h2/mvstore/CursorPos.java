package org.h2.mvstore;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/CursorPos.class */
public final class CursorPos<K, V> {
    public Page<K, V> page;
    public int index;
    public CursorPos<K, V> parent;

    public CursorPos(Page<K, V> page, int i, CursorPos<K, V> cursorPos) {
        this.page = page;
        this.index = i;
        this.parent = cursorPos;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> CursorPos<K, V> traverseDown(Page<K, V> page, K k) {
        CursorPos cursorPos = null;
        while (!page.isLeaf()) {
            int binarySearch = page.binarySearch(k) + 1;
            if (binarySearch < 0) {
                binarySearch = -binarySearch;
            }
            cursorPos = new CursorPos(page, binarySearch, cursorPos);
            page = page.getChildPage(binarySearch);
        }
        return new CursorPos<>(page, page.binarySearch(k), cursorPos);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int processRemovalInfo(long j) {
        int i = 0;
        CursorPos<K, V> cursorPos = this;
        while (true) {
            CursorPos<K, V> cursorPos2 = cursorPos;
            if (cursorPos2 != null) {
                i += cursorPos2.page.removePage(j);
                cursorPos = cursorPos2.parent;
            } else {
                return i;
            }
        }
    }

    public String toString() {
        return "CursorPos{page=" + this.page + ", index=" + this.index + ", parent=" + this.parent + '}';
    }
}
