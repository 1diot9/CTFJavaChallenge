package org.h2.store.fs.disk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/disk/FilePathDisk.class */
public class FilePathDisk extends FilePath {
    private static final String CLASSPATH_PREFIX = "classpath:";

    @Override // org.h2.store.fs.FilePath
    public FilePathDisk getPath(String str) {
        FilePathDisk filePathDisk = new FilePathDisk();
        filePathDisk.name = translateFileName(str);
        return filePathDisk;
    }

    @Override // org.h2.store.fs.FilePath
    public long size() {
        if (this.name.startsWith("classpath:")) {
            String substring = this.name.substring("classpath:".length());
            if (!substring.startsWith("/")) {
                substring = "/" + substring;
            }
            URL resource = getClass().getResource(substring);
            if (resource == null) {
                return 0L;
            }
            try {
                URI uri = resource.toURI();
                if ("file".equals(resource.getProtocol())) {
                    return Files.size(Paths.get(uri));
                }
                try {
                    FileSystems.getFileSystem(uri);
                    return Files.size(Paths.get(uri));
                } catch (FileSystemNotFoundException e) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("create", "true");
                    FileSystem newFileSystem = FileSystems.newFileSystem(uri, hashMap);
                    Throwable th = null;
                    try {
                        try {
                            long size = Files.size(Paths.get(uri));
                            if (newFileSystem != null) {
                                if (0 != 0) {
                                    try {
                                        newFileSystem.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    newFileSystem.close();
                                }
                            }
                            return size;
                        } finally {
                        }
                    } finally {
                    }
                }
            } catch (Exception e2) {
                return 0L;
            }
        }
        try {
            return Files.size(Paths.get(this.name, new String[0]));
        } catch (IOException e3) {
            return 0L;
        }
    }

    protected static String translateFileName(String str) {
        String replace = str.replace('\\', '/');
        if (replace.startsWith("file:")) {
            replace = replace.substring(5);
        } else if (replace.startsWith("nio:")) {
            replace = replace.substring(4);
        }
        return expandUserHomeDirectory(replace);
    }

    public static String expandUserHomeDirectory(String str) {
        if (str.startsWith(Constants.SERVER_PROPERTIES_DIR) && (str.length() == 1 || str.startsWith("~/"))) {
            str = SysProperties.USER_HOME + str.substring(1);
        }
        return str;
    }

    @Override // org.h2.store.fs.FilePath
    public void moveTo(FilePath filePath, boolean z) {
        Path path = Paths.get(this.name, new String[0]);
        Path path2 = Paths.get(filePath.name, new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            throw DbException.get(ErrorCode.FILE_RENAME_FAILED_2, this.name + " (not found)", filePath.name);
        }
        if (z) {
            try {
                Files.move(path, path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                return;
            } catch (AtomicMoveNotSupportedException e) {
            } catch (IOException e2) {
                throw DbException.get(ErrorCode.FILE_RENAME_FAILED_2, e2, this.name, filePath.name);
            }
        }
        CopyOption[] copyOptionArr = z ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0];
        try {
            Files.move(path, path2, copyOptionArr);
        } catch (FileAlreadyExistsException e3) {
            throw DbException.get(ErrorCode.FILE_RENAME_FAILED_2, this.name, filePath + " (exists)");
        } catch (IOException e4) {
            IOException iOException = e4;
            for (int i = 0; i < SysProperties.MAX_FILE_RETRY; i++) {
                IOUtils.trace("rename", this.name + " >" + filePath, null);
                try {
                    Files.move(path, path2, copyOptionArr);
                    return;
                } catch (FileAlreadyExistsException e5) {
                    throw DbException.get(ErrorCode.FILE_RENAME_FAILED_2, this.name, filePath + " (exists)");
                } catch (IOException e6) {
                    iOException = e4;
                    wait(i);
                }
            }
            throw DbException.get(ErrorCode.FILE_RENAME_FAILED_2, iOException, this.name, filePath.name);
        }
    }

    private static void wait(int i) {
        if (i == 8) {
            System.gc();
        }
        try {
            Thread.sleep(Math.min(256, i * i));
        } catch (InterruptedException e) {
        }
    }

    @Override // org.h2.store.fs.FilePath
    public boolean createFile() {
        Path path = Paths.get(this.name, new String[0]);
        for (int i = 0; i < SysProperties.MAX_FILE_RETRY; i++) {
            try {
                Files.createFile(path, new FileAttribute[0]);
                return true;
            } catch (FileAlreadyExistsException e) {
                return false;
            } catch (IOException e2) {
                wait(i);
            }
        }
        return false;
    }

    @Override // org.h2.store.fs.FilePath
    public boolean exists() {
        return Files.exists(Paths.get(this.name, new String[0]), new LinkOption[0]);
    }

    @Override // org.h2.store.fs.FilePath
    public void delete() {
        Path path = Paths.get(this.name, new String[0]);
        IOException iOException = null;
        for (int i = 0; i < SysProperties.MAX_FILE_RETRY; i++) {
            IOUtils.trace("delete", this.name, null);
            try {
                Files.deleteIfExists(path);
                return;
            } catch (AccessDeniedException e) {
                try {
                    FileStore fileStore = Files.getFileStore(path);
                    if (!fileStore.supportsFileAttributeView(PosixFileAttributeView.class) && fileStore.supportsFileAttributeView(DosFileAttributeView.class)) {
                        Files.setAttribute(path, "dos:readonly", false, new LinkOption[0]);
                        Files.delete(path);
                    }
                } catch (IOException e2) {
                    iOException = e2;
                }
                wait(i);
            } catch (DirectoryNotEmptyException e3) {
                throw DbException.get(ErrorCode.FILE_DELETE_FAILED_1, e3, this.name);
            } catch (IOException e4) {
                iOException = e4;
                wait(i);
            }
        }
        throw DbException.get(ErrorCode.FILE_DELETE_FAILED_1, iOException, this.name);
    }

    @Override // org.h2.store.fs.FilePath
    public List<FilePath> newDirectoryStream() {
        try {
            Stream<Path> list = Files.list(toRealPath(Paths.get(this.name, new String[0])));
            Throwable th = null;
            try {
                List<FilePath> list2 = (List) list.collect(ArrayList::new, (arrayList, path) -> {
                    arrayList.add(getPath(path.toString()));
                }, (v0, v1) -> {
                    v0.addAll(v1);
                });
                if (list != null) {
                    if (0 != 0) {
                        try {
                            list.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        list.close();
                    }
                }
                return list2;
            } finally {
            }
        } catch (NoSuchFileException e) {
            return Collections.emptyList();
        } catch (IOException e2) {
            throw DbException.convertIOException(e2, this.name);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public boolean canWrite() {
        try {
            return Files.isWritable(Paths.get(this.name, new String[0]));
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.h2.store.fs.FilePath
    public boolean setReadOnly() {
        Path path = Paths.get(this.name, new String[0]);
        try {
            FileStore fileStore = Files.getFileStore(path);
            if (!fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
                if (fileStore.supportsFileAttributeView(DosFileAttributeView.class)) {
                    Files.setAttribute(path, "dos:readonly", true, new LinkOption[0]);
                    return true;
                }
                return false;
            }
            HashSet hashSet = new HashSet();
            for (PosixFilePermission posixFilePermission : Files.getPosixFilePermissions(path, new LinkOption[0])) {
                switch (AnonymousClass1.$SwitchMap$java$nio$file$attribute$PosixFilePermission[posixFilePermission.ordinal()]) {
                    case 1:
                    case 2:
                    case 3:
                        break;
                    default:
                        hashSet.add(posixFilePermission);
                        break;
                }
            }
            Files.setPosixFilePermissions(path, hashSet);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /* renamed from: org.h2.store.fs.disk.FilePathDisk$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/disk/FilePathDisk$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$nio$file$attribute$PosixFilePermission = new int[PosixFilePermission.values().length];

        static {
            try {
                $SwitchMap$java$nio$file$attribute$PosixFilePermission[PosixFilePermission.OWNER_WRITE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$nio$file$attribute$PosixFilePermission[PosixFilePermission.GROUP_WRITE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$nio$file$attribute$PosixFilePermission[PosixFilePermission.OTHERS_WRITE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    @Override // org.h2.store.fs.FilePath
    public FilePathDisk toRealPath() {
        return getPath(toRealPath(Paths.get(this.name, new String[0])).toString());
    }

    private static Path toRealPath(Path path) {
        try {
            path = path.toRealPath(new LinkOption[0]);
        } catch (IOException e) {
            path = parentToRealPath(path.toAbsolutePath().normalize());
        }
        return path;
    }

    private static Path parentToRealPath(Path path) {
        Path parent = path.getParent();
        if (parent == null) {
            return path;
        }
        try {
            parent = parent.toRealPath(new LinkOption[0]);
        } catch (IOException e) {
            parent = parentToRealPath(parent);
        }
        return parent.resolve(path.getFileName());
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath getParent() {
        Path parent = Paths.get(this.name, new String[0]).getParent();
        if (parent == null) {
            return null;
        }
        return getPath(parent.toString());
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isDirectory() {
        return Files.isDirectory(Paths.get(this.name, new String[0]), new LinkOption[0]);
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isRegularFile() {
        return Files.isRegularFile(Paths.get(this.name, new String[0]), new LinkOption[0]);
    }

    @Override // org.h2.store.fs.FilePath
    public boolean isAbsolute() {
        return Paths.get(this.name, new String[0]).isAbsolute();
    }

    @Override // org.h2.store.fs.FilePath
    public long lastModified() {
        try {
            return Files.getLastModifiedTime(Paths.get(this.name, new String[0]), new LinkOption[0]).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    @Override // org.h2.store.fs.FilePath
    public void createDirectory() {
        Path path = Paths.get(this.name, new String[0]);
        try {
            Files.createDirectory(path, new FileAttribute[0]);
        } catch (FileAlreadyExistsException e) {
            throw DbException.get(ErrorCode.FILE_CREATION_FAILED_1, this.name + " (a file with this name already exists)");
        } catch (IOException e2) {
            IOException iOException = e2;
            for (int i = 0; i < SysProperties.MAX_FILE_RETRY; i++) {
                if (Files.isDirectory(path, new LinkOption[0])) {
                    return;
                }
                try {
                    Files.createDirectory(path, new FileAttribute[0]);
                } catch (FileAlreadyExistsException e3) {
                    throw DbException.get(ErrorCode.FILE_CREATION_FAILED_1, this.name + " (a file with this name already exists)");
                } catch (IOException e4) {
                    iOException = e4;
                }
                wait(i);
            }
            throw DbException.get(ErrorCode.FILE_CREATION_FAILED_1, iOException, this.name);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public OutputStream newOutputStream(boolean z) throws IOException {
        Path path = Paths.get(this.name, new String[0]);
        OpenOption[] openOptionArr = z ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND} : new OpenOption[0];
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            OutputStream newOutputStream = Files.newOutputStream(path, openOptionArr);
            IOUtils.trace("openFileOutputStream", this.name, newOutputStream);
            return newOutputStream;
        } catch (IOException e) {
            freeMemoryAndFinalize();
            return Files.newOutputStream(path, openOptionArr);
        }
    }

    @Override // org.h2.store.fs.FilePath
    public InputStream newInputStream() throws IOException {
        if (this.name.matches("[a-zA-Z]{2,19}:.*")) {
            if (this.name.startsWith("classpath:")) {
                String substring = this.name.substring("classpath:".length());
                if (!substring.startsWith("/")) {
                    substring = "/" + substring;
                }
                InputStream resourceAsStream = getClass().getResourceAsStream(substring);
                if (resourceAsStream == null) {
                    resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(substring.substring(1));
                }
                if (resourceAsStream == null) {
                    throw new FileNotFoundException("resource " + substring);
                }
                return resourceAsStream;
            }
            return new URL(this.name).openStream();
        }
        InputStream newInputStream = Files.newInputStream(Paths.get(this.name, new String[0]), new OpenOption[0]);
        IOUtils.trace("openFileInputStream", this.name, newInputStream);
        return newInputStream;
    }

    static void freeMemoryAndFinalize() {
        IOUtils.trace("freeMemoryAndFinalize", null, null);
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        for (int i = 0; i < 16; i++) {
            runtime.gc();
            long freeMemory2 = runtime.freeMemory();
            runtime.runFinalization();
            if (freeMemory2 != freeMemory) {
                freeMemory = freeMemory2;
            } else {
                return;
            }
        }
    }

    @Override // org.h2.store.fs.FilePath
    public FileChannel open(String str) throws IOException {
        FileChannel open = FileChannel.open(Paths.get(this.name, new String[0]), FileUtils.modeToOptions(str), FileUtils.NO_ATTRIBUTES);
        IOUtils.trace("open", this.name, open);
        return open;
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return "file";
    }

    @Override // org.h2.store.fs.FilePath
    public FilePath createTempFile(String str, boolean z) throws IOException {
        Path createTempFile;
        Path absolutePath = Paths.get(this.name + '.', new String[0]).toAbsolutePath();
        String path = absolutePath.getFileName().toString();
        if (z) {
            Path path2 = Paths.get(System.getProperty("java.io.tmpdir", "."), new String[0]);
            if (!Files.isDirectory(path2, new LinkOption[0])) {
                Files.createDirectories(path2, new FileAttribute[0]);
            }
            createTempFile = Files.createTempFile(path, str, new FileAttribute[0]);
        } else {
            Path parent = absolutePath.getParent();
            Files.createDirectories(parent, new FileAttribute[0]);
            createTempFile = Files.createTempFile(parent, path, str, new FileAttribute[0]);
        }
        return get(createTempFile.toString());
    }
}
