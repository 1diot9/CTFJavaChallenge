package org.h2.store.fs.zip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.disk.FilePathDisk;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/zip/FilePathZip.class */
public class FilePathZip extends FilePath {
    @Override // org.h2.store.fs.FilePath
    public FilePathZip getPath(String str) {
        FilePathZip filePathZip = new FilePathZip();
        filePathZip.name = str;
        return filePathZip;
    }

    @Override // org.h2.store.fs.FilePath
    public void createDirectory() {
    }

    @Override // org.h2.store.fs.FilePath
    public boolean createFile() {
        throw DbException.getUnsupportedException("write");
    }

    @Override // org.h2.store.fs.FilePath
    public void delete() {
        throw DbException.getUnsupportedException("write");
    }

    @Override // org.h2.store.fs.FilePath
    public boolean exists() {
        try {
            String entryName = getEntryName();
            if (entryName.isEmpty()) {
                return true;
            }
            ZipFile openZipFile = openZipFile();
            Throwable th = null;
            try {
                try {
                    boolean z = openZipFile.getEntry(entryName) != null;
                    if (openZipFile != null) {
                        if (0 != 0) {
                            try {
                                openZipFile.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            openZipFile.close();
                        }
                    }
                    return z;
                } finally {
                }
            } finally {
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override // org.h2.store.fs.FilePath
    public long lastModified() {
        return 0L;
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath getParent() {
        int lastIndexOf = this.name.lastIndexOf(47);
        if (lastIndexOf < 0) {
            return null;
        }
        return getPath(this.name.substring(0, lastIndexOf));
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isAbsolute() {
        return FilePath.get(translateFileName(this.name)).isAbsolute();
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath unwrap() {
        return FilePath.get(this.name.substring(getScheme().length() + 1));
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isDirectory() {
        return isRegularOrDirectory(true);
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isRegularFile() {
        return isRegularOrDirectory(false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x00ab, code lost:            if (r0 == null) goto L40;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00b0, code lost:            if (0 == 0) goto L39;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00c6, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00b3, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00ba, code lost:            r13 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00bc, code lost:            r8.addSuppressed(r13);     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0048, code lost:            if (r0.isDirectory() != r5) goto L15;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x004b, code lost:            r0 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0050, code lost:            r12 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0053, code lost:            if (r0 == null) goto L24;     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0058, code lost:            if (0 == 0) goto L23;     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x006e, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x005b, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0062, code lost:            r13 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0064, code lost:            r8.addSuppressed(r13);     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x004f, code lost:            r0 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00d1, code lost:            if (r0 == null) goto L65;     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00d6, code lost:            if (0 == 0) goto L50;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00ec, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:61:?, code lost:            return false;     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00d9, code lost:            r0.close();     */
    /* JADX WARN: Code restructure failed: missing block: B:65:?, code lost:            return false;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00e0, code lost:            r9 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00e2, code lost:            r8.addSuppressed(r9);     */
    /* JADX WARN: Code restructure failed: missing block: B:68:?, code lost:            return false;     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x0121, code lost:            return false;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean isRegularOrDirectory(boolean r5) {
        /*
            Method dump skipped, instructions count: 294
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.store.fs.zip.FilePathZip.isRegularOrDirectory(boolean):boolean");
    }

    @Override // org.h2.store.fs.FilePath
    public boolean canWrite() {
        return false;
    }

    @Override // org.h2.store.fs.FilePath
    public boolean setReadOnly() {
        return true;
    }

    @Override // org.h2.store.fs.FilePath
    public long size() {
        try {
            ZipFile openZipFile = openZipFile();
            Throwable th = null;
            try {
                ZipEntry entry = openZipFile.getEntry(getEntryName());
                return entry == null ? 0L : entry.getSize();
            } finally {
                if (openZipFile != null) {
                    if (0 != 0) {
                        try {
                            openZipFile.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        openZipFile.close();
                    }
                }
            }
        } catch (IOException e) {
            return 0L;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.h2.store.fs.FilePath
    public ArrayList<FilePath> newDirectoryStream() {
        String str = this.name;
        ArrayList<FilePath> arrayList = new ArrayList<>();
        try {
            if (str.indexOf(33) < 0) {
                str = str + "!";
            }
            if (!str.endsWith("/")) {
                str = str + "/";
            }
            ZipFile openZipFile = openZipFile();
            Throwable th = null;
            try {
                String entryName = getEntryName();
                String substring = str.substring(0, str.length() - entryName.length());
                Enumeration<? extends ZipEntry> entries = openZipFile.entries();
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.startsWith(entryName) && name.length() > entryName.length()) {
                        int indexOf = name.indexOf(47, entryName.length());
                        if (indexOf < 0 || indexOf >= name.length() - 1) {
                            arrayList.add(getPath(substring + name));
                        }
                    }
                }
                if (openZipFile != null) {
                    if (0 != 0) {
                        try {
                            openZipFile.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        openZipFile.close();
                    }
                }
                return arrayList;
            } catch (Throwable th3) {
                if (openZipFile != null) {
                    if (0 != 0) {
                        try {
                            openZipFile.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        openZipFile.close();
                    }
                }
                throw th3;
            }
        } catch (IOException e) {
            throw DbException.convertIOException(e, "listFiles " + str);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public FileChannel open(String str) throws IOException {
        ZipFile openZipFile = openZipFile();
        ZipEntry entry = openZipFile.getEntry(getEntryName());
        if (entry == null) {
            openZipFile.close();
            throw new FileNotFoundException(this.name);
        }
        return new FileZip(openZipFile, entry);
    }

    @Override // org.h2.store.fs.FilePath
    public OutputStream newOutputStream(boolean z) throws IOException {
        throw new IOException("write");
    }

    @Override // org.h2.store.fs.FilePath
    public void moveTo(FilePath filePath, boolean z) {
        throw DbException.getUnsupportedException("write");
    }

    private static String translateFileName(String str) {
        if (str.startsWith("zip:")) {
            str = str.substring("zip:".length());
        }
        int indexOf = str.indexOf(33);
        if (indexOf >= 0) {
            str = str.substring(0, indexOf);
        }
        return FilePathDisk.expandUserHomeDirectory(str);
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath toRealPath() {
        return this;
    }

    private String getEntryName() {
        String substring;
        int indexOf = this.name.indexOf(33);
        if (indexOf <= 0) {
            substring = "";
        } else {
            substring = this.name.substring(indexOf + 1);
        }
        String replace = substring.replace('\\', '/');
        if (replace.startsWith("/")) {
            replace = replace.substring(1);
        }
        return replace;
    }

    private ZipFile openZipFile() throws IOException {
        return new ZipFile(translateFileName(this.name));
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath createTempFile(String str, boolean z) throws IOException {
        if (!z) {
            throw new IOException("File system is read-only");
        }
        return new FilePathDisk().getPath(this.name).createTempFile(str, true);
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return "zip";
    }
}
