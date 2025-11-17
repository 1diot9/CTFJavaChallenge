package org.h2.store.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/FilePathWrapper.class */
public abstract class FilePathWrapper extends FilePath {
    private FilePath base;

    @Override // org.h2.store.fs.FilePath
    public FilePathWrapper getPath(String str) {
        return create(str, unwrap(str));
    }

    public FilePathWrapper wrap(FilePath filePath) {
        if (filePath == null) {
            return null;
        }
        return create(getPrefix() + filePath.name, filePath);
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath unwrap() {
        return unwrap(this.name);
    }

    private FilePathWrapper create(String str, FilePath filePath) {
        try {
            FilePathWrapper filePathWrapper = (FilePathWrapper) getClass().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            filePathWrapper.name = str;
            filePathWrapper.base = filePath;
            return filePathWrapper;
        } catch (Exception e) {
            throw new IllegalArgumentException("Path: " + str, e);
        }
    }

    protected String getPrefix() {
        return getScheme() + ":";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FilePath unwrap(String str) {
        return FilePath.get(str.substring(getScheme().length() + 1));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FilePath getBase() {
        return this.base;
    }

    @Override // org.h2.store.fs.FilePath
    public boolean canWrite() {
        return this.base.canWrite();
    }

    @Override // org.h2.store.fs.FilePath
    public void createDirectory() {
        this.base.createDirectory();
    }

    @Override // org.h2.store.fs.FilePath
    public boolean createFile() {
        return this.base.createFile();
    }

    @Override // org.h2.store.fs.FilePath
    public void delete() {
        this.base.delete();
    }

    @Override // org.h2.store.fs.FilePath
    public boolean exists() {
        return this.base.exists();
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath getParent() {
        return wrap(this.base.getParent());
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isAbsolute() {
        return this.base.isAbsolute();
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isDirectory() {
        return this.base.isDirectory();
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isRegularFile() {
        return this.base.isRegularFile();
    }

    @Override // org.h2.store.fs.FilePath
    public long lastModified() {
        return this.base.lastModified();
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath toRealPath() {
        return wrap(this.base.toRealPath());
    }

    @Override // org.h2.store.fs.FilePath
    public List<FilePath> newDirectoryStream() {
        List<FilePath> newDirectoryStream = this.base.newDirectoryStream();
        int size = newDirectoryStream.size();
        for (int i = 0; i < size; i++) {
            newDirectoryStream.set(i, wrap(newDirectoryStream.get(i)));
        }
        return newDirectoryStream;
    }

    @Override // org.h2.store.fs.FilePath
    public void moveTo(FilePath filePath, boolean z) {
        this.base.moveTo(((FilePathWrapper) filePath).base, z);
    }

    @Override // org.h2.store.fs.FilePath
    public InputStream newInputStream() throws IOException {
        return this.base.newInputStream();
    }

    @Override // org.h2.store.fs.FilePath
    public OutputStream newOutputStream(boolean z) throws IOException {
        return this.base.newOutputStream(z);
    }

    @Override // org.h2.store.fs.FilePath
    public FileChannel open(String str) throws IOException {
        return this.base.open(str);
    }

    @Override // org.h2.store.fs.FilePath
    public boolean setReadOnly() {
        return this.base.setReadOnly();
    }

    @Override // org.h2.store.fs.FilePath
    public long size() {
        return this.base.size();
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath createTempFile(String str, boolean z) throws IOException {
        return wrap(this.base.createTempFile(str, z));
    }
}
