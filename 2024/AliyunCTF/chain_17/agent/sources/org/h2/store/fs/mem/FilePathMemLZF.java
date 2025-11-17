package org.h2.store.fs.mem;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/mem/FilePathMemLZF.class */
public class FilePathMemLZF extends FilePathMem {
    @Override // org.h2.store.fs.mem.FilePathMem, org.h2.store.fs.FilePath
    public FilePathMem getPath(String str) {
        FilePathMemLZF filePathMemLZF = new FilePathMemLZF();
        filePathMemLZF.name = getCanonicalPath(str);
        return filePathMemLZF;
    }

    @Override // org.h2.store.fs.mem.FilePathMem
    boolean compressed() {
        return true;
    }

    @Override // org.h2.store.fs.mem.FilePathMem, org.h2.store.fs.FilePath
    public String getScheme() {
        return "memLZF";
    }
}
