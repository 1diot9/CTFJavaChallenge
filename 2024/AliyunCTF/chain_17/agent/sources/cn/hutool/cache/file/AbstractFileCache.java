package cn.hutool.cache.file;

import cn.hutool.cache.Cache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import java.io.File;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/file/AbstractFileCache.class */
public abstract class AbstractFileCache implements Serializable {
    private static final long serialVersionUID = 1;
    protected final int capacity;
    protected final int maxFileSize;
    protected final long timeout;
    protected final Cache<File, byte[]> cache = initCache();
    protected int usedSize;

    protected abstract Cache<File, byte[]> initCache();

    public AbstractFileCache(int capacity, int maxFileSize, long timeout) {
        this.capacity = capacity;
        this.maxFileSize = maxFileSize;
        this.timeout = timeout;
    }

    public int capacity() {
        return this.capacity;
    }

    public int getUsedSize() {
        return this.usedSize;
    }

    public int maxFileSize() {
        return this.maxFileSize;
    }

    public int getCachedFilesCount() {
        return this.cache.size();
    }

    public long timeout() {
        return this.timeout;
    }

    public void clear() {
        this.cache.clear();
        this.usedSize = 0;
    }

    public byte[] getFileBytes(String path) throws IORuntimeException {
        return getFileBytes(new File(path));
    }

    public byte[] getFileBytes(File file) throws IORuntimeException {
        byte[] bytes = this.cache.get(file);
        if (bytes != null) {
            return bytes;
        }
        byte[] bytes2 = FileUtil.readBytes(file);
        if (this.maxFileSize != 0 && file.length() > this.maxFileSize) {
            return bytes2;
        }
        this.usedSize += bytes2.length;
        this.cache.put(file, bytes2);
        return bytes2;
    }
}
