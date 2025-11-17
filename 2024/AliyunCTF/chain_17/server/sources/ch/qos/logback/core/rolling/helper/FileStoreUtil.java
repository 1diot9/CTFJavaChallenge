package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RolloverFailure;
import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/FileStoreUtil.class */
public class FileStoreUtil {
    static final String PATH_CLASS_STR = "java.nio.file.Path";
    static final String FILES_CLASS_STR = "java.nio.file.Files";

    public static boolean areOnSameFileStore(File a, File b) throws RolloverFailure {
        if (!a.exists()) {
            throw new IllegalArgumentException("File [" + String.valueOf(a) + "] does not exist.");
        }
        if (!b.exists()) {
            throw new IllegalArgumentException("File [" + String.valueOf(b) + "] does not exist.");
        }
        try {
            Path pathA = a.toPath();
            Path pathB = b.toPath();
            FileStore fileStoreA = Files.getFileStore(pathA);
            FileStore fileStoreB = Files.getFileStore(pathB);
            return fileStoreA.equals(fileStoreB);
        } catch (Exception e) {
            throw new RolloverFailure("Failed to check file store equality for [" + String.valueOf(a) + "] and [" + String.valueOf(b) + "]", e);
        }
    }
}
