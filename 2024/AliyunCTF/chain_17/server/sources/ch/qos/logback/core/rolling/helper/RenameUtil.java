package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.FileUtil;
import java.io.File;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/RenameUtil.class */
public class RenameUtil extends ContextAwareBase {
    static String RENAMING_ERROR_URL = "http://logback.qos.ch/codes.html#renamingError";

    public void rename(String src, String target) throws RolloverFailure {
        if (src.equals(target)) {
            addWarn("Source and target files are the same [" + src + "]. Skipping.");
            return;
        }
        File srcFile = new File(src);
        if (srcFile.exists()) {
            File targetFile = new File(target);
            createMissingTargetDirsIfNecessary(targetFile);
            addInfo("Renaming file [" + String.valueOf(srcFile) + "] to [" + String.valueOf(targetFile) + "]");
            boolean result = srcFile.renameTo(targetFile);
            if (!result) {
                addWarn("Failed to rename file [" + String.valueOf(srcFile) + "] as [" + String.valueOf(targetFile) + "].");
                Boolean areOnDifferentVolumes = areOnDifferentVolumes(srcFile, targetFile);
                if (Boolean.TRUE.equals(areOnDifferentVolumes)) {
                    addWarn("Detected different file systems for source [" + src + "] and target [" + target + "]. Attempting rename by copying.");
                    renameByCopying(src, target);
                    return;
                } else {
                    addWarn("Please consider leaving the [file] option of " + RollingFileAppender.class.getSimpleName() + " empty.");
                    addWarn("See also " + RENAMING_ERROR_URL);
                    return;
                }
            }
            return;
        }
        throw new RolloverFailure("File [" + src + "] does not exist.");
    }

    Boolean areOnDifferentVolumes(File srcFile, File targetFile) throws RolloverFailure {
        if (!EnvUtil.isJDK7OrHigher()) {
            return false;
        }
        File parentOfTarget = targetFile.getAbsoluteFile().getParentFile();
        if (parentOfTarget == null) {
            addWarn("Parent of target file [" + String.valueOf(targetFile) + "] is null");
            return null;
        }
        if (!parentOfTarget.exists()) {
            addWarn("Parent of target file [" + String.valueOf(targetFile) + "] does not exist");
            return null;
        }
        try {
            boolean onSameFileStore = FileStoreUtil.areOnSameFileStore(srcFile, parentOfTarget);
            return Boolean.valueOf(!onSameFileStore);
        } catch (RolloverFailure rf) {
            addWarn("Error while checking file store equality", rf);
            return null;
        }
    }

    public void renameByCopying(String src, String target) throws RolloverFailure {
        FileUtil fileUtil = new FileUtil(getContext());
        fileUtil.copy(src, target);
        File srcFile = new File(src);
        if (!srcFile.delete()) {
            addWarn("Could not delete " + src);
        }
    }

    void createMissingTargetDirsIfNecessary(File toFile) throws RolloverFailure {
        boolean result = FileUtil.createMissingParentDirectories(toFile);
        if (!result) {
            throw new RolloverFailure("Failed to create parent directories for [" + toFile.getAbsolutePath() + "]");
        }
    }

    public String toString() {
        return "c.q.l.co.rolling.helper.RenameUtil";
    }
}
