package org.springframework.boot.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.util.EnumSet;
import java.util.HexFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/system/ApplicationTemp.class */
public class ApplicationTemp {
    private static final FileAttribute<?>[] NO_FILE_ATTRIBUTES = new FileAttribute[0];
    private static final EnumSet<PosixFilePermission> DIRECTORY_PERMISSIONS = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE);
    private final Class<?> sourceClass;
    private final Lock pathLock;
    private volatile Path path;

    public ApplicationTemp() {
        this(null);
    }

    public ApplicationTemp(Class<?> sourceClass) {
        this.pathLock = new ReentrantLock();
        this.sourceClass = sourceClass;
    }

    public String toString() {
        return getDir().getAbsolutePath();
    }

    public File getDir() {
        return getPath().toFile();
    }

    public File getDir(String subDir) {
        return createDirectory(getPath().resolve(subDir)).toFile();
    }

    private Path getPath() {
        if (this.path == null) {
            this.pathLock.lock();
            try {
                if (this.path == null) {
                    String hash = HexFormat.of().withUpperCase().formatHex(generateHash(this.sourceClass));
                    this.path = createDirectory(getTempDirectory().resolve(hash));
                }
            } finally {
                this.pathLock.unlock();
            }
        }
        return this.path;
    }

    private Path createDirectory(Path path) {
        try {
            if (!Files.exists(path, new LinkOption[0])) {
                Files.createDirectory(path, getFileAttributes(path.getFileSystem(), DIRECTORY_PERMISSIONS));
            }
            return path;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create application temp directory " + path, ex);
        }
    }

    private FileAttribute<?>[] getFileAttributes(FileSystem fileSystem, EnumSet<PosixFilePermission> ownerReadWrite) {
        if (!fileSystem.supportedFileAttributeViews().contains("posix")) {
            return NO_FILE_ATTRIBUTES;
        }
        return new FileAttribute[]{PosixFilePermissions.asFileAttribute(ownerReadWrite)};
    }

    private Path getTempDirectory() {
        String property = System.getProperty("java.io.tmpdir");
        Assert.state(StringUtils.hasLength(property), "No 'java.io.tmpdir' property set");
        Path tempDirectory = Paths.get(property, new String[0]);
        Assert.state(Files.exists(tempDirectory, new LinkOption[0]), (Supplier<String>) () -> {
            return "Temp directory '" + tempDirectory + "' does not exist";
        });
        Assert.state(Files.isDirectory(tempDirectory, new LinkOption[0]), (Supplier<String>) () -> {
            return "Temp location '" + tempDirectory + "' is not a directory";
        });
        return tempDirectory;
    }

    private byte[] generateHash(Class<?> sourceClass) {
        ApplicationHome home = new ApplicationHome(sourceClass);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            update(digest, home.getSource());
            update(digest, home.getDir());
            update(digest, System.getProperty("user.dir"));
            update(digest, System.getProperty("java.home"));
            update(digest, System.getProperty("java.class.path"));
            update(digest, System.getProperty("sun.java.command"));
            update(digest, System.getProperty("sun.boot.class.path"));
            return digest.digest();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void update(MessageDigest digest, Object source) {
        if (source != null) {
            digest.update(getUpdateSourceBytes(source));
        }
    }

    private byte[] getUpdateSourceBytes(Object source) {
        if (source instanceof File) {
            File file = (File) source;
            return getUpdateSourceBytes(file.getAbsolutePath());
        }
        return source.toString().getBytes();
    }
}
