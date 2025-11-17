package org.springframework.boot.loader.nio.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

/* loaded from: agent.jar:org/springframework/boot/loader/nio/file/NestedFileStore.class */
class NestedFileStore extends FileStore {
    private final NestedFileSystem fileSystem;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedFileStore(NestedFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override // java.nio.file.FileStore
    public String name() {
        return this.fileSystem.toString();
    }

    @Override // java.nio.file.FileStore
    public String type() {
        return "nestedfs";
    }

    @Override // java.nio.file.FileStore
    public boolean isReadOnly() {
        return this.fileSystem.isReadOnly();
    }

    @Override // java.nio.file.FileStore
    public long getTotalSpace() throws IOException {
        return 0L;
    }

    @Override // java.nio.file.FileStore
    public long getUsableSpace() throws IOException {
        return 0L;
    }

    @Override // java.nio.file.FileStore
    public long getUnallocatedSpace() throws IOException {
        return 0L;
    }

    @Override // java.nio.file.FileStore
    public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
        return getJarPathFileStore().supportsFileAttributeView(type);
    }

    @Override // java.nio.file.FileStore
    public boolean supportsFileAttributeView(String name) {
        return getJarPathFileStore().supportsFileAttributeView(name);
    }

    @Override // java.nio.file.FileStore
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> cls) {
        return (V) getJarPathFileStore().getFileStoreAttributeView(cls);
    }

    @Override // java.nio.file.FileStore
    public Object getAttribute(String attribute) throws IOException {
        try {
            return getJarPathFileStore().getAttribute(attribute);
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

    protected FileStore getJarPathFileStore() {
        try {
            return Files.getFileStore(this.fileSystem.getJarPath());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
