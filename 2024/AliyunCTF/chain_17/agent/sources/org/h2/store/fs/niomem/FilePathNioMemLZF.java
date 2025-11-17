package org.h2.store.fs.niomem;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/niomem/FilePathNioMemLZF.class */
public class FilePathNioMemLZF extends FilePathNioMem {
    @Override // org.h2.store.fs.niomem.FilePathNioMem
    boolean compressed() {
        return true;
    }

    @Override // org.h2.store.fs.niomem.FilePathNioMem, org.h2.store.fs.FilePath
    public FilePathNioMem getPath(String str) {
        if (!str.startsWith(getScheme())) {
            throw new IllegalArgumentException(str + " doesn't start with " + getScheme());
        }
        int indexOf = str.indexOf(58);
        int lastIndexOf = str.lastIndexOf(58);
        FilePathNioMemLZF filePathNioMemLZF = new FilePathNioMemLZF();
        if (indexOf != -1 && indexOf != lastIndexOf) {
            filePathNioMemLZF.compressLaterCachePercent = Float.parseFloat(str.substring(indexOf + 1, lastIndexOf));
        }
        filePathNioMemLZF.name = getCanonicalPath(str);
        return filePathNioMemLZF;
    }

    @Override // org.h2.store.fs.niomem.FilePathNioMem
    protected boolean isRoot() {
        return this.name.lastIndexOf(58) == this.name.length() - 1;
    }

    @Override // org.h2.store.fs.niomem.FilePathNioMem, org.h2.store.fs.FilePath
    public String getScheme() {
        return "nioMemLZF";
    }
}
