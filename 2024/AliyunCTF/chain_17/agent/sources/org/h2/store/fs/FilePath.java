package org.h2.store.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.store.fs.disk.FilePathDisk;
import org.h2.util.MathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/FilePath.class */
public abstract class FilePath {
    private static final FilePath defaultProvider;
    private static final ConcurrentHashMap<String, FilePath> providers;
    private static String tempRandom;
    private static long tempSequence;
    public String name;

    public abstract long size();

    public abstract void moveTo(FilePath filePath, boolean z);

    public abstract boolean createFile();

    public abstract boolean exists();

    public abstract void delete();

    public abstract List<FilePath> newDirectoryStream();

    public abstract FilePath toRealPath();

    public abstract FilePath getParent();

    public abstract boolean isDirectory();

    public abstract boolean isRegularFile();

    public abstract boolean isAbsolute();

    public abstract long lastModified();

    public abstract boolean canWrite();

    public abstract void createDirectory();

    public abstract FileChannel open(String str) throws IOException;

    public abstract boolean setReadOnly();

    public abstract String getScheme();

    public abstract FilePath getPath(String str);

    static {
        ConcurrentHashMap<String, FilePath> concurrentHashMap = new ConcurrentHashMap<>();
        FilePathDisk filePathDisk = new FilePathDisk();
        concurrentHashMap.put(filePathDisk.getScheme(), filePathDisk);
        concurrentHashMap.put("nio", filePathDisk);
        defaultProvider = filePathDisk;
        for (String str : new String[]{"org.h2.store.fs.mem.FilePathMem", "org.h2.store.fs.mem.FilePathMemLZF", "org.h2.store.fs.niomem.FilePathNioMem", "org.h2.store.fs.niomem.FilePathNioMemLZF", "org.h2.store.fs.split.FilePathSplit", "org.h2.store.fs.niomapped.FilePathNioMapped", "org.h2.store.fs.async.FilePathAsync", "org.h2.store.fs.zip.FilePathZip", "org.h2.store.fs.retry.FilePathRetryOnInterrupt"}) {
            try {
                FilePath filePath = (FilePath) Class.forName(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                concurrentHashMap.put(filePath.getScheme(), filePath);
            } catch (Exception e) {
            }
        }
        providers = concurrentHashMap;
    }

    public static FilePath get(String str) {
        String replace = str.replace('\\', '/');
        int indexOf = replace.indexOf(58);
        if (indexOf < 2) {
            return defaultProvider.getPath(replace);
        }
        FilePath filePath = providers.get(replace.substring(0, indexOf));
        if (filePath == null) {
            filePath = defaultProvider;
        }
        return filePath.getPath(replace);
    }

    public static void register(FilePath filePath) {
        providers.put(filePath.getScheme(), filePath);
    }

    public static void unregister(FilePath filePath) {
        providers.remove(filePath.getScheme());
    }

    public String getName() {
        int max = Math.max(this.name.indexOf(58), this.name.lastIndexOf(47));
        return max < 0 ? this.name : this.name.substring(max + 1);
    }

    public OutputStream newOutputStream(boolean z) throws IOException {
        return newFileChannelOutputStream(open("rw"), z);
    }

    public static OutputStream newFileChannelOutputStream(FileChannel fileChannel, boolean z) throws IOException {
        if (z) {
            fileChannel.position(fileChannel.size());
        } else {
            fileChannel.position(0L);
            fileChannel.truncate(0L);
        }
        return Channels.newOutputStream(fileChannel);
    }

    public InputStream newInputStream() throws IOException {
        return Channels.newInputStream(open("r"));
    }

    public FilePath createTempFile(String str, boolean z) throws IOException {
        while (true) {
            FilePath path = getPath(this.name + getNextTempFileNamePart(false) + str);
            if (path.exists() || !path.createFile()) {
                getNextTempFileNamePart(true);
            } else {
                path.open("rw").close();
                return path;
            }
        }
    }

    private static synchronized String getNextTempFileNamePart(boolean z) {
        if (z || tempRandom == null) {
            tempRandom = MathUtils.randomInt(Integer.MAX_VALUE) + ".";
        }
        StringBuilder append = new StringBuilder().append(tempRandom);
        long j = tempSequence;
        tempSequence = j + 1;
        return append.append(j).toString();
    }

    public String toString() {
        return this.name;
    }

    public FilePath unwrap() {
        return this;
    }
}
