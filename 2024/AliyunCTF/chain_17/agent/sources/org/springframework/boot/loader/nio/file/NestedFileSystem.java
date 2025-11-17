package org.springframework.boot.loader.nio.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.tomcat.websocket.BasicAuthenticator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:org/springframework/boot/loader/nio/file/NestedFileSystem.class */
public class NestedFileSystem extends FileSystem {
    private static final Set<String> SUPPORTED_FILE_ATTRIBUTE_VIEWS = Set.of(BasicAuthenticator.schemeName);
    private static final String FILE_SYSTEMS_CLASS_NAME = FileSystems.class.getName();
    private static final Object EXISTING_FILE_SYSTEM = new Object();
    private final NestedFileSystemProvider provider;
    private final Path jarPath;
    private volatile boolean closed;
    private final Map<String, Object> zipFileSystems = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedFileSystem(NestedFileSystemProvider provider, Path jarPath) {
        if (provider == null || jarPath == null) {
            throw new IllegalArgumentException("Provider and JarPath must not be null");
        }
        this.provider = provider;
        this.jarPath = jarPath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void installZipFileSystemIfNecessary(String nestedEntryName) {
        boolean seen;
        try {
            synchronized (this.zipFileSystems) {
                seen = this.zipFileSystems.putIfAbsent(nestedEntryName, EXISTING_FILE_SYSTEM) != null;
            }
            if (!seen) {
                URI uri = new URI("jar:nested:" + this.jarPath.toUri().getPath() + "/!" + nestedEntryName);
                if (!hasFileSystem(uri)) {
                    FileSystem zipFileSystem = FileSystems.newFileSystem(uri, (Map<String, ?>) Collections.emptyMap());
                    synchronized (this.zipFileSystems) {
                        this.zipFileSystems.put(nestedEntryName, zipFileSystem);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean hasFileSystem(URI uri) {
        try {
            FileSystems.getFileSystem(uri);
            return true;
        } catch (FileSystemNotFoundException e) {
            return isCreatingNewFileSystem();
        }
    }

    private boolean isCreatingNewFileSystem() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack != null) {
            for (StackTraceElement element : stack) {
                if (FILE_SYSTEMS_CLASS_NAME.equals(element.getClassName())) {
                    return "newFileSystem".equals(element.getMethodName());
                }
            }
            return false;
        }
        return false;
    }

    @Override // java.nio.file.FileSystem
    public FileSystemProvider provider() {
        return this.provider;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Path getJarPath() {
        return this.jarPath;
    }

    @Override // java.nio.file.FileSystem, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        synchronized (this.zipFileSystems) {
            Stream<Object> stream = this.zipFileSystems.values().stream();
            Class<FileSystem> cls = FileSystem.class;
            Objects.requireNonNull(FileSystem.class);
            Stream<Object> filter = stream.filter(cls::isInstance);
            Class<FileSystem> cls2 = FileSystem.class;
            Objects.requireNonNull(FileSystem.class);
            filter.map(cls2::cast).forEach(this::closeZipFileSystem);
        }
        this.provider.removeFileSystem(this);
    }

    private void closeZipFileSystem(FileSystem zipFileSystem) {
        try {
            zipFileSystem.close();
        } catch (Exception e) {
        }
    }

    @Override // java.nio.file.FileSystem
    public boolean isOpen() {
        return !this.closed;
    }

    @Override // java.nio.file.FileSystem
    public boolean isReadOnly() {
        return true;
    }

    @Override // java.nio.file.FileSystem
    public String getSeparator() {
        return "/!";
    }

    @Override // java.nio.file.FileSystem
    public Iterable<Path> getRootDirectories() {
        assertNotClosed();
        return Collections.emptySet();
    }

    @Override // java.nio.file.FileSystem
    public Iterable<FileStore> getFileStores() {
        assertNotClosed();
        return Collections.emptySet();
    }

    @Override // java.nio.file.FileSystem
    public Set<String> supportedFileAttributeViews() {
        assertNotClosed();
        return SUPPORTED_FILE_ATTRIBUTE_VIEWS;
    }

    @Override // java.nio.file.FileSystem
    public Path getPath(String first, String... more) {
        assertNotClosed();
        if (more.length != 0) {
            throw new IllegalArgumentException("Nested paths must contain a single element");
        }
        return new NestedPath(this, first);
    }

    @Override // java.nio.file.FileSystem
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        throw new UnsupportedOperationException("Nested paths do not support path matchers");
    }

    @Override // java.nio.file.FileSystem
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException("Nested paths do not have a user principal lookup service");
    }

    @Override // java.nio.file.FileSystem
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException("Nested paths do not support the WacherService");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NestedFileSystem other = (NestedFileSystem) obj;
        return this.jarPath.equals(other.jarPath);
    }

    public int hashCode() {
        return this.jarPath.hashCode();
    }

    public String toString() {
        return this.jarPath.toAbsolutePath().toString();
    }

    private void assertNotClosed() {
        if (this.closed) {
            throw new ClosedFileSystemException();
        }
    }
}
