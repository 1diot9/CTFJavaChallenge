package org.h2.store.fs;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/FileUtils.class */
public class FileUtils {
    public static final Set<? extends OpenOption> R = Collections.singleton(StandardOpenOption.READ);
    public static final Set<? extends OpenOption> RW = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE));
    public static final Set<? extends OpenOption> RWS = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SYNC));
    public static final Set<? extends OpenOption> RWD = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.DSYNC));
    public static final FileAttribute<?>[] NO_ATTRIBUTES = new FileAttribute[0];

    public static boolean exists(String str) {
        return FilePath.get(str).exists();
    }

    public static void createDirectory(String str) {
        FilePath.get(str).createDirectory();
    }

    public static boolean createFile(String str) {
        return FilePath.get(str).createFile();
    }

    public static void delete(String str) {
        FilePath.get(str).delete();
    }

    public static String toRealPath(String str) {
        return FilePath.get(str).toRealPath().toString();
    }

    public static String getParent(String str) {
        FilePath parent = FilePath.get(str).getParent();
        if (parent == null) {
            return null;
        }
        return parent.toString();
    }

    public static boolean isAbsolute(String str) {
        return FilePath.get(str).isAbsolute() || str.startsWith(File.separator) || str.startsWith("/");
    }

    public static void move(String str, String str2) {
        FilePath.get(str).moveTo(FilePath.get(str2), false);
    }

    public static void moveAtomicReplace(String str, String str2) {
        FilePath.get(str).moveTo(FilePath.get(str2), true);
    }

    public static String getName(String str) {
        return FilePath.get(str).getName();
    }

    public static List<String> newDirectoryStream(String str) {
        List<FilePath> newDirectoryStream = FilePath.get(str).newDirectoryStream();
        ArrayList arrayList = new ArrayList(newDirectoryStream.size());
        Iterator<FilePath> it = newDirectoryStream.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().toString());
        }
        return arrayList;
    }

    public static long lastModified(String str) {
        return FilePath.get(str).lastModified();
    }

    public static long size(String str) {
        return FilePath.get(str).size();
    }

    public static boolean isDirectory(String str) {
        return FilePath.get(str).isDirectory();
    }

    public static boolean isRegularFile(String str) {
        return FilePath.get(str).isRegularFile();
    }

    public static FileChannel open(String str, String str2) throws IOException {
        return FilePath.get(str).open(str2);
    }

    public static InputStream newInputStream(String str) throws IOException {
        return FilePath.get(str).newInputStream();
    }

    public static BufferedReader newBufferedReader(String str, Charset charset) throws IOException {
        return new BufferedReader(new InputStreamReader(newInputStream(str), charset), 4096);
    }

    public static OutputStream newOutputStream(String str, boolean z) throws IOException {
        return FilePath.get(str).newOutputStream(z);
    }

    public static boolean canWrite(String str) {
        return FilePath.get(str).canWrite();
    }

    public static boolean setReadOnly(String str) {
        return FilePath.get(str).setReadOnly();
    }

    public static String unwrap(String str) {
        return FilePath.get(str).unwrap().toString();
    }

    public static void deleteRecursive(String str, boolean z) {
        if (exists(str)) {
            if (isDirectory(str)) {
                Iterator<String> it = newDirectoryStream(str).iterator();
                while (it.hasNext()) {
                    deleteRecursive(it.next(), z);
                }
            }
            if (z) {
                tryDelete(str);
            } else {
                delete(str);
            }
        }
    }

    public static void createDirectories(String str) {
        if (str != null) {
            if (exists(str)) {
                if (!isDirectory(str)) {
                    createDirectory(str);
                }
            } else {
                createDirectories(getParent(str));
                createDirectory(str);
            }
        }
    }

    public static boolean tryDelete(String str) {
        try {
            FilePath.get(str).delete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String createTempFile(String str, String str2, boolean z) throws IOException {
        return FilePath.get(str).createTempFile(str2, z).toString();
    }

    public static void readFully(FileChannel fileChannel, ByteBuffer byteBuffer) throws IOException {
        while (fileChannel.read(byteBuffer) >= 0) {
            if (byteBuffer.remaining() <= 0) {
                return;
            }
        }
        throw new EOFException();
    }

    public static void writeFully(FileChannel fileChannel, ByteBuffer byteBuffer) throws IOException {
        do {
            fileChannel.write(byteBuffer);
        } while (byteBuffer.remaining() > 0);
    }

    public static Set<? extends OpenOption> modeToOptions(String str) {
        Set<? extends OpenOption> set;
        boolean z = -1;
        switch (str.hashCode()) {
            case Opcodes.FREM /* 114 */:
                if (str.equals("r")) {
                    z = false;
                    break;
                }
                break;
            case 3653:
                if (str.equals("rw")) {
                    z = true;
                    break;
                }
                break;
            case 113343:
                if (str.equals("rwd")) {
                    z = 3;
                    break;
                }
                break;
            case 113358:
                if (str.equals("rws")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                set = R;
                break;
            case true:
                set = RW;
                break;
            case true:
                set = RWS;
                break;
            case true:
                set = RWD;
                break;
            default:
                throw new IllegalArgumentException(str);
        }
        return set;
    }
}
