package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.FileUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/Compressor.class */
public class Compressor extends ContextAwareBase {
    final CompressionMode compressionMode;
    static final int BUFFER_SIZE = 8192;

    public Compressor(CompressionMode compressionMode) {
        this.compressionMode = compressionMode;
    }

    public void compress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) {
        switch (this.compressionMode) {
            case GZ:
                gzCompress(nameOfFile2Compress, nameOfCompressedFile);
                return;
            case ZIP:
                zipCompress(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
                return;
            case NONE:
                throw new UnsupportedOperationException("compress method called in NONE compression mode");
            default:
                return;
        }
    }

    private void zipCompress(String nameOfFile2zip, String nameOfZippedFile, String innerEntryName) {
        File file2zip = new File(nameOfFile2zip);
        if (!file2zip.exists()) {
            addStatus(new WarnStatus("The file to compress named [" + nameOfFile2zip + "] does not exist.", this));
            return;
        }
        if (innerEntryName == null) {
            addStatus(new WarnStatus("The innerEntryName parameter cannot be null", this));
            return;
        }
        if (!nameOfZippedFile.endsWith(".zip")) {
            nameOfZippedFile = nameOfZippedFile + ".zip";
        }
        File zippedFile = new File(nameOfZippedFile);
        if (zippedFile.exists()) {
            addStatus(new WarnStatus("The target compressed file named [" + nameOfZippedFile + "] exist already.", this));
            return;
        }
        addInfo("ZIP compressing [" + String.valueOf(file2zip) + "] as [" + String.valueOf(zippedFile) + "]");
        createMissingTargetDirsIfNecessary(zippedFile);
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(nameOfFile2zip));
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(nameOfZippedFile));
                try {
                    ZipEntry zipEntry = computeZipEntry(innerEntryName);
                    zos.putNextEntry(zipEntry);
                    byte[] inbuf = new byte[8192];
                    while (true) {
                        int n = bis.read(inbuf);
                        if (n == -1) {
                            break;
                        } else {
                            zos.write(inbuf, 0, n);
                        }
                    }
                    addInfo("Done ZIP compressing [" + String.valueOf(file2zip) + "] as [" + String.valueOf(zippedFile) + "]");
                    zos.close();
                    bis.close();
                } catch (Throwable th) {
                    try {
                        zos.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
            }
        } catch (Exception e) {
            addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2zip + "] into [" + nameOfZippedFile + "].", this, e));
        }
        if (!file2zip.delete()) {
            addStatus(new WarnStatus("Could not delete [" + nameOfFile2zip + "].", this));
        }
    }

    ZipEntry computeZipEntry(File zippedFile) {
        return computeZipEntry(zippedFile.getName());
    }

    ZipEntry computeZipEntry(String filename) {
        String nameOfFileNestedWithinArchive = computeFileNameStrWithoutCompSuffix(filename, this.compressionMode);
        return new ZipEntry(nameOfFileNestedWithinArchive);
    }

    private void gzCompress(String nameOfFile2gz, String nameOfgzedFile) {
        File file2gz = new File(nameOfFile2gz);
        if (!file2gz.exists()) {
            addStatus(new WarnStatus("The file to compress named [" + nameOfFile2gz + "] does not exist.", this));
            return;
        }
        if (!nameOfgzedFile.endsWith(".gz")) {
            nameOfgzedFile = nameOfgzedFile + ".gz";
        }
        File gzedFile = new File(nameOfgzedFile);
        if (gzedFile.exists()) {
            addWarn("The target compressed file named [" + nameOfgzedFile + "] exist already. Aborting file compression.");
            return;
        }
        addInfo("GZ compressing [" + String.valueOf(file2gz) + "] as [" + String.valueOf(gzedFile) + "]");
        createMissingTargetDirsIfNecessary(gzedFile);
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(nameOfFile2gz));
            try {
                GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(nameOfgzedFile));
                try {
                    byte[] inbuf = new byte[8192];
                    while (true) {
                        int n = bis.read(inbuf);
                        if (n == -1) {
                            break;
                        } else {
                            gzos.write(inbuf, 0, n);
                        }
                    }
                    addInfo("Done GZ compressing [" + String.valueOf(file2gz) + "] as [" + String.valueOf(gzedFile) + "]");
                    gzos.close();
                    bis.close();
                } catch (Throwable th) {
                    try {
                        gzos.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
            }
        } catch (Exception e) {
            addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2gz + "] into [" + nameOfgzedFile + "].", this, e));
        }
        if (!file2gz.delete()) {
            addStatus(new WarnStatus("Could not delete [" + nameOfFile2gz + "].", this));
        }
    }

    public static String computeFileNameStrWithoutCompSuffix(String fileNamePatternStr, CompressionMode compressionMode) {
        int len = fileNamePatternStr.length();
        switch (compressionMode) {
            case GZ:
                if (fileNamePatternStr.endsWith(".gz")) {
                    return fileNamePatternStr.substring(0, len - 3);
                }
                return fileNamePatternStr;
            case ZIP:
                if (fileNamePatternStr.endsWith(".zip")) {
                    return fileNamePatternStr.substring(0, len - 4);
                }
                return fileNamePatternStr;
            case NONE:
                return fileNamePatternStr;
            default:
                throw new IllegalStateException("Execution should not reach this point");
        }
    }

    void createMissingTargetDirsIfNecessary(File file) {
        boolean result = FileUtil.createMissingParentDirectories(file);
        if (!result) {
            addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
        }
    }

    public String toString() {
        return getClass().getName();
    }

    public Future<?> asyncCompress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
        CompressionRunnable runnable = new CompressionRunnable(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
        ExecutorService executorService = this.context.getExecutorService();
        Future<?> future = executorService.submit(runnable);
        return future;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/Compressor$CompressionRunnable.class */
    class CompressionRunnable implements Runnable {
        final String nameOfFile2Compress;
        final String nameOfCompressedFile;
        final String innerEntryName;

        public CompressionRunnable(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) {
            this.nameOfFile2Compress = nameOfFile2Compress;
            this.nameOfCompressedFile = nameOfCompressedFile;
            this.innerEntryName = innerEntryName;
        }

        @Override // java.lang.Runnable
        public void run() {
            Compressor.this.compress(this.nameOfFile2Compress, this.nameOfCompressedFile, this.innerEntryName);
        }
    }
}
