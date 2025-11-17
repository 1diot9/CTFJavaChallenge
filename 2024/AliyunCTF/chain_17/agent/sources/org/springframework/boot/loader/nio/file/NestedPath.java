package org.springframework.boot.loader.nio.file;

import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import org.springframework.boot.loader.zip.ZipContent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:org/springframework/boot/loader/nio/file/NestedPath.class */
public final class NestedPath implements Path {
    private final NestedFileSystem fileSystem;
    private final String nestedEntryName;
    private volatile Boolean entryExists;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedPath(NestedFileSystem fileSystem, String nestedEntryName) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("'filesSystem' must not be null");
        }
        this.fileSystem = fileSystem;
        this.nestedEntryName = (nestedEntryName == null || nestedEntryName.isBlank()) ? null : nestedEntryName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Path getJarPath() {
        return this.fileSystem.getJarPath();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getNestedEntryName() {
        return this.nestedEntryName;
    }

    @Override // java.nio.file.Path
    public NestedFileSystem getFileSystem() {
        return this.fileSystem;
    }

    @Override // java.nio.file.Path
    public boolean isAbsolute() {
        return true;
    }

    @Override // java.nio.file.Path
    public Path getRoot() {
        return null;
    }

    @Override // java.nio.file.Path
    public Path getFileName() {
        return this;
    }

    @Override // java.nio.file.Path
    public Path getParent() {
        return null;
    }

    @Override // java.nio.file.Path
    public int getNameCount() {
        return 1;
    }

    @Override // java.nio.file.Path
    public Path getName(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Nested paths only have a single element");
        }
        return this;
    }

    @Override // java.nio.file.Path
    public Path subpath(int beginIndex, int endIndex) {
        if (beginIndex != 0 || endIndex != 1) {
            throw new IllegalArgumentException("Nested paths only have a single element");
        }
        return this;
    }

    @Override // java.nio.file.Path
    public boolean startsWith(Path other) {
        return equals(other);
    }

    @Override // java.nio.file.Path
    public boolean endsWith(Path other) {
        return equals(other);
    }

    @Override // java.nio.file.Path
    public Path normalize() {
        return this;
    }

    @Override // java.nio.file.Path
    public Path resolve(Path other) {
        throw new UnsupportedOperationException("Unable to resolve nested path");
    }

    @Override // java.nio.file.Path
    public Path relativize(Path other) {
        throw new UnsupportedOperationException("Unable to relativize nested path");
    }

    @Override // java.nio.file.Path
    public URI toUri() {
        try {
            String uri = "nested:" + this.fileSystem.getJarPath().toUri().getPath();
            if (this.nestedEntryName != null) {
                uri = uri + "/!" + this.nestedEntryName;
            }
            return new URI(uri);
        } catch (URISyntaxException ex) {
            throw new IOError(ex);
        }
    }

    @Override // java.nio.file.Path
    public Path toAbsolutePath() {
        return this;
    }

    @Override // java.nio.file.Path
    public Path toRealPath(LinkOption... options) throws IOException {
        return this;
    }

    @Override // java.nio.file.Path, java.nio.file.Watchable
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        throw new UnsupportedOperationException("Nested paths cannot be watched");
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.lang.Comparable
    public int compareTo(Path other) {
        NestedPath otherNestedPath = cast(other);
        return this.nestedEntryName.compareTo(otherNestedPath.nestedEntryName);
    }

    @Override // java.nio.file.Path
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NestedPath other = (NestedPath) obj;
        return Objects.equals(this.fileSystem, other.fileSystem) && Objects.equals(this.nestedEntryName, other.nestedEntryName);
    }

    @Override // java.nio.file.Path
    public int hashCode() {
        return Objects.hash(this.fileSystem, this.nestedEntryName);
    }

    @Override // java.nio.file.Path
    public String toString() {
        String string = this.fileSystem.getJarPath().toString();
        if (this.nestedEntryName != null) {
            string = string + this.fileSystem.getSeparator() + this.nestedEntryName;
        }
        return string;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void assertExists() throws NoSuchFileException {
        if (!Files.isRegularFile(getJarPath(), new LinkOption[0])) {
            throw new NoSuchFileException(toString());
        }
        Boolean entryExists = this.entryExists;
        if (entryExists == null) {
            try {
                ZipContent content = ZipContent.open(getJarPath(), this.nestedEntryName);
                try {
                    entryExists = true;
                    if (content != null) {
                        content.close();
                    }
                } finally {
                }
            } catch (IOException e) {
                entryExists = false;
            }
            this.entryExists = entryExists;
        }
        if (!entryExists.booleanValue()) {
            throw new NoSuchFileException(toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NestedPath cast(Path path) {
        if (path instanceof NestedPath) {
            NestedPath nestedPath = (NestedPath) path;
            return nestedPath;
        }
        throw new ProviderMismatchException();
    }
}
