package org.h2.store.fs.mem;

import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/mem/FilePathMem.class */
public class FilePathMem extends FilePath {
    private static final TreeMap<String, FileMemData> MEMORY_FILES = new TreeMap<>();
    private static final FileMemData DIRECTORY = new FileMemData("", false);

    @Override // org.h2.store.fs.FilePath
    public FilePathMem getPath(String str) {
        FilePathMem filePathMem = new FilePathMem();
        filePathMem.name = getCanonicalPath(str);
        return filePathMem;
    }

    @Override // org.h2.store.fs.FilePath
    public long size() {
        return getMemoryFile().length();
    }

    @Override // org.h2.store.fs.FilePath
    public void moveTo(FilePath filePath, boolean z) {
        synchronized (MEMORY_FILES) {
            if (!z) {
                if (!filePath.name.equals(this.name) && MEMORY_FILES.containsKey(filePath.name)) {
                    throw DbException.get(ErrorCode.FILE_RENAME_FAILED_2, this.name, filePath + " (exists)");
                }
            }
            FileMemData memoryFile = getMemoryFile();
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
            FileMemData remove = MEMORY_FILES.remove(this.name);
            if (remove != null) {
                remove.truncate(0L);
            }
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
                if (!str.equals(this.name) && str.indexOf(47, this.name.length() + 1) < 0) {
                    arrayList.add(getPath(str));
                }
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
    public FilePathMem getParent() {
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
            z = MEMORY_FILES.get(this.name) == DIRECTORY;
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
            FileMemData fileMemData = MEMORY_FILES.get(this.name);
            z = (fileMemData == null || fileMemData == DIRECTORY) ? false : true;
        }
        return z;
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isAbsolute() {
        return true;
    }

    @Override // org.h2.store.fs.FilePath
    public FilePathMem toRealPath() {
        return this;
    }

    @Override // org.h2.store.fs.FilePath
    public long lastModified() {
        return getMemoryFile().getLastModified();
    }

    @Override // org.h2.store.fs.FilePath
    public void createDirectory() {
        if (exists()) {
            throw DbException.get(ErrorCode.FILE_CREATION_FAILED_1, this.name + " (a file with this name already exists)");
        }
        synchronized (MEMORY_FILES) {
            MEMORY_FILES.put(this.name, DIRECTORY);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public FileChannel open(String str) {
        return new FileMem(getMemoryFile(), "r".equals(str));
    }

    private FileMemData getMemoryFile() {
        FileMemData fileMemData;
        synchronized (MEMORY_FILES) {
            FileMemData fileMemData2 = MEMORY_FILES.get(this.name);
            if (fileMemData2 == DIRECTORY) {
                throw DbException.get(ErrorCode.FILE_CREATION_FAILED_1, this.name + " (a directory with this name already exists)");
            }
            if (fileMemData2 == null) {
                fileMemData2 = new FileMemData(this.name, compressed());
                MEMORY_FILES.put(this.name, fileMemData2);
            }
            fileMemData = fileMemData2;
        }
        return fileMemData;
    }

    private boolean isRoot() {
        return this.name.equals(getScheme() + ":");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getCanonicalPath(String str) {
        String replace = str.replace('\\', '/');
        int indexOf = replace.indexOf(58) + 1;
        if (replace.length() > indexOf && replace.charAt(indexOf) != '/') {
            replace = replace.substring(0, indexOf) + "/" + replace.substring(indexOf);
        }
        return replace;
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return "memFS";
    }

    boolean compressed() {
        return false;
    }
}
