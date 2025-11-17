package org.springframework.boot.loader.nio.file;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.loader.net.protocol.nested.NestedLocation;

/* loaded from: server.jar:org/springframework/boot/loader/nio/file/NestedFileSystemProvider.class */
public class NestedFileSystemProvider extends FileSystemProvider {
    private Map<Path, NestedFileSystem> fileSystems = new HashMap();

    @Override // java.nio.file.spi.FileSystemProvider
    public String getScheme() {
        return "nested";
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        NestedFileSystem fileSystem;
        NestedLocation location = NestedLocation.fromUri(uri);
        Path jarPath = location.path();
        synchronized (this.fileSystems) {
            if (this.fileSystems.containsKey(jarPath)) {
                throw new FileSystemAlreadyExistsException();
            }
            fileSystem = new NestedFileSystem(this, location.path());
            this.fileSystems.put(location.path(), fileSystem);
        }
        return fileSystem;
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileSystem getFileSystem(URI uri) {
        NestedFileSystem fileSystem;
        NestedLocation location = NestedLocation.fromUri(uri);
        synchronized (this.fileSystems) {
            fileSystem = this.fileSystems.get(location.path());
            if (fileSystem == null) {
                throw new FileSystemNotFoundException();
            }
        }
        return fileSystem;
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public Path getPath(URI uri) {
        Path path;
        NestedLocation location = NestedLocation.fromUri(uri);
        synchronized (this.fileSystems) {
            NestedFileSystem fileSystem = this.fileSystems.computeIfAbsent(location.path(), path2 -> {
                return new NestedFileSystem(this, path2);
            });
            fileSystem.installZipFileSystemIfNecessary(location.nestedEntryName());
            path = fileSystem.getPath(location.nestedEntryName(), new String[0]);
        }
        return path;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeFileSystem(NestedFileSystem fileSystem) {
        synchronized (this.fileSystems) {
            this.fileSystems.remove(fileSystem.getJarPath());
        }
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        NestedPath nestedPath = NestedPath.cast(path);
        return new NestedByteChannel(nestedPath.getJarPath(), nestedPath.getNestedEntryName());
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        throw new NotDirectoryException(NestedPath.cast(dir).toString());
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void delete(Path path) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return path.equals(path2);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public FileStore getFileStore(Path path) throws IOException {
        NestedPath nestedPath = NestedPath.cast(path);
        nestedPath.assertExists();
        return new NestedFileStore(nestedPath.getFileSystem());
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        Path jarPath = getJarPath(path);
        jarPath.getFileSystem().provider().checkAccess(jarPath, modes);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> cls, LinkOption... linkOptionArr) {
        Path jarPath = getJarPath(path);
        return (V) jarPath.getFileSystem().provider().getFileAttributeView(jarPath, cls, linkOptionArr);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> cls, LinkOption... linkOptionArr) throws IOException {
        Path jarPath = getJarPath(path);
        return (A) jarPath.getFileSystem().provider().readAttributes(jarPath, cls, linkOptionArr);
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        Path jarPath = getJarPath(path);
        return jarPath.getFileSystem().provider().readAttributes(jarPath, attributes, options);
    }

    protected Path getJarPath(Path path) {
        return NestedPath.cast(path).getJarPath();
    }

    @Override // java.nio.file.spi.FileSystemProvider
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new ReadOnlyFileSystemException();
    }
}
