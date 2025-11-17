package org.springframework.boot.loader.launch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import org.springframework.boot.loader.launch.Archive;
import org.springframework.boot.loader.net.protocol.jar.JarUrl;

/* loaded from: agent.jar:org/springframework/boot/loader/launch/JarFileArchive.class */
class JarFileArchive implements Archive {
    private static final String UNPACK_MARKER = "UNPACK:";
    private static final FileAttribute<?>[] NO_FILE_ATTRIBUTES = new FileAttribute[0];
    private static final FileAttribute<?>[] DIRECTORY_PERMISSION_ATTRIBUTES = asFileAttributes(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE);
    private static final FileAttribute<?>[] FILE_PERMISSION_ATTRIBUTES = asFileAttributes(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
    private static final Path TEMP = Paths.get(System.getProperty("java.io.tmpdir"), new String[0]);
    private final File file;
    private final JarFile jarFile;
    private volatile Path tempUnpackDirectory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarFileArchive(File file) throws IOException {
        this(file, new JarFile(file));
    }

    private JarFileArchive(File file, JarFile jarFile) {
        this.file = file;
        this.jarFile = jarFile;
    }

    @Override // org.springframework.boot.loader.launch.Archive
    public Manifest getManifest() throws IOException {
        return this.jarFile.getManifest();
    }

    @Override // org.springframework.boot.loader.launch.Archive
    public Set<URL> getClassPathUrls(Predicate<Archive.Entry> includeFilter, Predicate<Archive.Entry> directorySearchFilter) throws IOException {
        return (Set) this.jarFile.stream().map(JarArchiveEntry::new).filter(includeFilter).map(this::getNestedJarUrl).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private URL getNestedJarUrl(JarArchiveEntry archiveEntry) {
        try {
            JarEntry jarEntry = archiveEntry.jarEntry();
            String comment = jarEntry.getComment();
            if (comment != null && comment.startsWith(UNPACK_MARKER)) {
                return getUnpackedNestedJarUrl(jarEntry);
            }
            return JarUrl.create(this.file, jarEntry);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private URL getUnpackedNestedJarUrl(JarEntry jarEntry) throws IOException {
        String name = jarEntry.getName();
        if (name.lastIndexOf(47) != -1) {
            name = name.substring(name.lastIndexOf(47) + 1);
        }
        Path path = getTempUnpackDirectory().resolve(name);
        if (!Files.exists(path, new LinkOption[0]) || Files.size(path) != jarEntry.getSize()) {
            unpack(jarEntry, path);
        }
        return path.toUri().toURL();
    }

    private Path getTempUnpackDirectory() {
        Path tempUnpackDirectory;
        Path tempUnpackDirectory2 = this.tempUnpackDirectory;
        if (tempUnpackDirectory2 != null) {
            return tempUnpackDirectory2;
        }
        synchronized (TEMP) {
            tempUnpackDirectory = this.tempUnpackDirectory;
            if (tempUnpackDirectory == null) {
                tempUnpackDirectory = createUnpackDirectory(TEMP);
                this.tempUnpackDirectory = tempUnpackDirectory;
            }
        }
        return tempUnpackDirectory;
    }

    private Path createUnpackDirectory(Path parent) {
        int attempts = 0;
        String fileName = Paths.get(this.jarFile.getName(), new String[0]).getFileName().toString();
        while (true) {
            int i = attempts;
            attempts++;
            if (i < 100) {
                Path unpackDirectory = parent.resolve(fileName + "-spring-boot-libs-" + UUID.randomUUID());
                try {
                    createDirectory(unpackDirectory);
                    return unpackDirectory;
                } catch (IOException e) {
                }
            } else {
                throw new IllegalStateException("Failed to create unpack directory in directory '" + parent + "'");
            }
        }
    }

    private void createDirectory(Path path) throws IOException {
        Files.createDirectory(path, getFileAttributes(path, DIRECTORY_PERMISSION_ATTRIBUTES));
    }

    private void unpack(JarEntry entry, Path path) throws IOException {
        createFile(path);
        path.toFile().deleteOnExit();
        InputStream in = this.jarFile.getInputStream(entry);
        try {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            if (in != null) {
                in.close();
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private void createFile(Path path) throws IOException {
        Files.createFile(path, getFileAttributes(path, FILE_PERMISSION_ATTRIBUTES));
    }

    private FileAttribute<?>[] getFileAttributes(Path path, FileAttribute<?>[] permissionAttributes) {
        return !supportsPosix(path.getFileSystem()) ? NO_FILE_ATTRIBUTES : permissionAttributes;
    }

    private boolean supportsPosix(FileSystem fileSystem) {
        return fileSystem.supportedFileAttributeViews().contains("posix");
    }

    @Override // org.springframework.boot.loader.launch.Archive, java.lang.AutoCloseable
    public void close() throws IOException {
        this.jarFile.close();
    }

    public String toString() {
        return this.file.toString();
    }

    private static FileAttribute<?>[] asFileAttributes(PosixFilePermission... permissions) {
        return new FileAttribute[]{PosixFilePermissions.asFileAttribute(Set.of((Object[]) permissions))};
    }

    /* loaded from: agent.jar:org/springframework/boot/loader/launch/JarFileArchive$JarArchiveEntry.class */
    private static final class JarArchiveEntry extends Record implements Archive.Entry {
        private final JarEntry jarEntry;

        private JarArchiveEntry(JarEntry jarEntry) {
            this.jarEntry = jarEntry;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, JarArchiveEntry.class), JarArchiveEntry.class, "jarEntry", "FIELD:Lorg/springframework/boot/loader/launch/JarFileArchive$JarArchiveEntry;->jarEntry:Ljava/util/jar/JarEntry;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, JarArchiveEntry.class), JarArchiveEntry.class, "jarEntry", "FIELD:Lorg/springframework/boot/loader/launch/JarFileArchive$JarArchiveEntry;->jarEntry:Ljava/util/jar/JarEntry;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, JarArchiveEntry.class, Object.class), JarArchiveEntry.class, "jarEntry", "FIELD:Lorg/springframework/boot/loader/launch/JarFileArchive$JarArchiveEntry;->jarEntry:Ljava/util/jar/JarEntry;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public JarEntry jarEntry() {
            return this.jarEntry;
        }

        @Override // org.springframework.boot.loader.launch.Archive.Entry
        public String name() {
            return this.jarEntry.getName();
        }

        @Override // org.springframework.boot.loader.launch.Archive.Entry
        public boolean isDirectory() {
            return this.jarEntry.isDirectory();
        }
    }
}
