package org.h2.store.fs.niomem;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/niomem/FilePathNioMem.class */
public class FilePathNioMem extends FilePath {
    private static final TreeMap<String, FileNioMemData> MEMORY_FILES = new TreeMap<>();
    float compressLaterCachePercent = 1.0f;

    @Override // org.h2.store.fs.FilePath
    public FilePathNioMem getPath(String str) {
        FilePathNioMem filePathNioMem = new FilePathNioMem();
        filePathNioMem.name = getCanonicalPath(str);
        return filePathNioMem;
    }

    @Override // org.h2.store.fs.FilePath
    public long size() {
        return getMemoryFile().length();
    }

    @Override // org.h2.store.fs.FilePath
    public void moveTo(FilePath filePath, boolean z) {
        synchronized (MEMORY_FILES) {
            if (!z) {
                if (!this.name.equals(filePath.name) && MEMORY_FILES.containsKey(filePath.name)) {
                    throw DbException.get(ErrorCode.FILE_RENAME_FAILED_2, this.name, filePath + " (exists)");
                }
            }
            FileNioMemData memoryFile = getMemoryFile();
            memoryFile.setName(filePath.name);
            MEMORY_FILES.remove(this.name);
            MEMORY_FILES.put(filePath.name, memoryFile);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public boolean createFile() {
        synchronized (MEMORY_FILES) {
            if (exists()) {
                return false;
            }
            getMemoryFile();
            return true;
        }
    }

    @Override // org.h2.store.fs.FilePath
    public boolean exists() {
        boolean z;
        if (isRoot()) {
            return true;
        }
        synchronized (MEMORY_FILES) {
            z = MEMORY_FILES.get(this.name) != null;
        }
        return z;
    }

    @Override // org.h2.store.fs.FilePath
    public void delete() {
        if (isRoot()) {
            return;
        }
        synchronized (MEMORY_FILES) {
            MEMORY_FILES.remove(this.name);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public List<FilePath> newDirectoryStream() {
        ArrayList arrayList = new ArrayList();
        synchronized (MEMORY_FILES) {
            for (String str : MEMORY_FILES.tailMap(this.name).keySet()) {
                if (!str.startsWith(this.name)) {
                    break;
                }
                arrayList.add(getPath(str));
            }
        }
        return arrayList;
    }

    @Override // org.h2.store.fs.FilePath
    public boolean setReadOnly() {
        return getMemoryFile().setReadOnly();
    }

    @Override // org.h2.store.fs.FilePath
    public boolean canWrite() {
        return getMemoryFile().canWrite();
    }

    @Override // org.h2.store.fs.FilePath
    public FilePathNioMem getParent() {
        int lastIndexOf = this.name.lastIndexOf(47);
        if (lastIndexOf < 0) {
            return null;
        }
        return getPath(this.name.substring(0, lastIndexOf));
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isDirectory() {
        boolean z;
        if (isRoot()) {
            return true;
        }
        synchronized (MEMORY_FILES) {
            z = MEMORY_FILES.get(this.name) == null;
        }
        return z;
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isRegularFile() {
        boolean z;
        if (isRoot()) {
            return false;
        }
        synchronized (MEMORY_FILES) {
            z = MEMORY_FILES.get(this.name) != null;
        }
        return z;
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isAbsolute() {
        return true;
    }

    @Override // org.h2.store.fs.FilePath
    public FilePathNioMem toRealPath() {
        return this;
    }

    @Override // org.h2.store.fs.FilePath
    public long lastModified() {
        return getMemoryFile().getLastModified();
    }

    @Override // org.h2.store.fs.FilePath
    public void createDirectory() {
        if (exists() && isDirectory()) {
            throw DbException.get(ErrorCode.FILE_CREATION_FAILED_1, this.name + " (a file with this name already exists)");
        }
    }

    @Override // org.h2.store.fs.FilePath
    public FileChannel open(String str) {
        return new FileNioMem(getMemoryFile(), "r".equals(str));
    }

    private FileNioMemData getMemoryFile() {
        FileNioMemData fileNioMemData;
        synchronized (MEMORY_FILES) {
            FileNioMemData fileNioMemData2 = MEMORY_FILES.get(this.name);
            if (fileNioMemData2 == null) {
                fileNioMemData2 = new FileNioMemData(this.name, compressed(), this.compressLaterCachePercent);
                MEMORY_FILES.put(this.name, fileNioMemData2);
            }
            fileNioMemData = fileNioMemData2;
        }
        return fileNioMemData;
    }

    protected boolean isRoot() {
        return this.name.equals(getScheme() + ":");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getCanonicalPath(String str) {
        String replace = str.replace('\\', '/');
        int lastIndexOf = replace.lastIndexOf(58) + 1;
        if (replace.length() > lastIndexOf && replace.charAt(lastIndexOf) != '/') {
            replace = replace.substring(0, lastIndexOf) + "/" + replace.substring(lastIndexOf);
        }
        return replace;
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return "nioMemFS";
    }

    boolean compressed() {
        return false;
    }
}
