package cn.hutool.cache.file;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LRUCache;
import java.io.File;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/file/LRUFileCache.class */
public class LRUFileCache extends AbstractFileCache {
    private static final long serialVersionUID = 1;

    public LRUFileCache(int capacity) {
        this(capacity, capacity / 2, 0L);
    }

    public LRUFileCache(int capacity, int maxFileSize) {
        this(capacity, maxFileSize, 0L);
    }

    public LRUFileCache(int capacity, int maxFileSize, long timeout) {
        super(capacity, maxFileSize, timeout);
    }

    @Override // cn.hutool.cache.file.AbstractFileCache
    protected Cache<File, byte[]> initCache() {
        return new LRUCache<File, byte[]>(this.capacity, this.timeout) { // from class: cn.hutool.cache.file.LRUFileCache.1
            private static final long serialVersionUID = 1;

            @Override // cn.hutool.cache.impl.AbstractCache, cn.hutool.cache.Cache
            public boolean isFull() {
                return LRUFileCache.this.usedSize > this.capacity;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // cn.hutool.cache.impl.AbstractCache
            public void onRemove(File key, byte[] cachedObject) {
                LRUFileCache.this.usedSize -= cachedObject.length;
            }
        };
    }
}
