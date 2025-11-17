package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;
import java.io.File;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/TimeBasedArchiveRemover.class */
public class TimeBasedArchiveRemover extends ContextAwareBase implements ArchiveRemover {
    protected static final long UNINITIALIZED = -1;
    protected static final long INACTIVITY_TOLERANCE_IN_MILLIS = 2764800000L;
    static final int MAX_VALUE_FOR_INACTIVITY_PERIODS = 336;
    final FileNamePattern fileNamePattern;
    final RollingCalendar rc;
    final boolean parentClean;
    private int maxHistory = 0;
    private long totalSizeCap = 0;
    long lastHeartBeat = -1;
    int callCount = 0;

    public TimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
        this.fileNamePattern = fileNamePattern;
        this.rc = rc;
        this.parentClean = computeParentCleaningFlag(fileNamePattern);
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public Future<?> cleanAsynchronously(Instant now) {
        ArhiveRemoverRunnable runnable = new ArhiveRemoverRunnable(now);
        ExecutorService alternateExecutorService = this.context.getAlternateExecutorService();
        Future<?> future = alternateExecutorService.submit(runnable);
        return future;
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public void clean(Instant now) {
        long nowInMillis = now.toEpochMilli();
        int periodsElapsed = computeElapsedPeriodsSinceLastClean(nowInMillis);
        this.lastHeartBeat = nowInMillis;
        if (periodsElapsed > 1) {
            addInfo("Multiple periods, i.e. " + periodsElapsed + " periods, seem to have elapsed. This is expected at application start.");
        }
        for (int i = 0; i < periodsElapsed; i++) {
            int offset = getPeriodOffsetForDeletionTarget() - i;
            Instant instantOfPeriodToClean = this.rc.getEndOfNextNthPeriod(now, offset);
            cleanPeriod(instantOfPeriodToClean);
        }
    }

    protected File[] getFilesInPeriod(Instant instantOfPeriodToClean) {
        String filenameToDelete = this.fileNamePattern.convert(instantOfPeriodToClean);
        File file2Delete = new File(filenameToDelete);
        if (fileExistsAndIsFile(file2Delete)) {
            return new File[]{file2Delete};
        }
        return new File[0];
    }

    private boolean fileExistsAndIsFile(File file2Delete) {
        return file2Delete.exists() && file2Delete.isFile();
    }

    public void cleanPeriod(Instant instantOfPeriodToClean) {
        File[] matchingFileArray = getFilesInPeriod(instantOfPeriodToClean);
        for (File f : matchingFileArray) {
            checkAndDeleteFile(f);
        }
        if (this.parentClean && matchingFileArray.length > 0) {
            File parentDir = getParentDir(matchingFileArray[0]);
            removeFolderIfEmpty(parentDir);
        }
    }

    private boolean checkAndDeleteFile(File f) {
        addInfo("deleting " + String.valueOf(f));
        if (f == null) {
            addWarn("Cannot delete empty file");
            return false;
        }
        if (!f.exists()) {
            addWarn("Cannot delete non existent file");
            return false;
        }
        boolean result = f.delete();
        if (!result) {
            addWarn("Failed to delete file " + f.toString());
        }
        return result;
    }

    void capTotalSize(Instant now) {
        long totalSize = 0;
        long totalRemoved = 0;
        for (int offset = 0; offset < this.maxHistory; offset++) {
            Instant instant = this.rc.getEndOfNextNthPeriod(now, -offset);
            File[] matchingFileArray = getFilesInPeriod(instant);
            descendingSort(matchingFileArray, instant);
            for (File f : matchingFileArray) {
                long size = f.length();
                if (totalSize + size > this.totalSizeCap) {
                    addInfo("Deleting [" + String.valueOf(f) + "] of size " + String.valueOf(new FileSize(size)));
                    totalRemoved += size;
                    checkAndDeleteFile(f);
                }
                totalSize += size;
            }
        }
        addInfo("Removed  " + String.valueOf(new FileSize(totalRemoved)) + " of files");
    }

    protected void descendingSort(File[] matchingFileArray, Instant instant) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public File getParentDir(File file) {
        File absolute = file.getAbsoluteFile();
        File parentDir = absolute.getParentFile();
        return parentDir;
    }

    int computeElapsedPeriodsSinceLastClean(long nowInMillis) {
        long periodsElapsed;
        if (this.lastHeartBeat == -1) {
            addInfo("first clean up after appender initialization");
            long periodsElapsed2 = this.rc.periodBarriersCrossed(nowInMillis, nowInMillis + INACTIVITY_TOLERANCE_IN_MILLIS);
            periodsElapsed = Math.min(periodsElapsed2, 336L);
        } else {
            periodsElapsed = this.rc.periodBarriersCrossed(this.lastHeartBeat, nowInMillis);
        }
        return (int) periodsElapsed;
    }

    boolean computeParentCleaningFlag(FileNamePattern fileNamePattern) {
        Converter<Object> p;
        DateTokenConverter<Object> dtc = fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc.getDatePattern().indexOf(47) != -1) {
            return true;
        }
        Converter<Object> converter = fileNamePattern.headTokenConverter;
        while (true) {
            p = converter;
            if (p == null || (p instanceof DateTokenConverter)) {
                break;
            }
            converter = p.getNext();
        }
        while (p != null) {
            if (p instanceof LiteralConverter) {
                String s = p.convert(null);
                if (s.indexOf(47) != -1) {
                    return true;
                }
            }
            p = p.getNext();
        }
        return false;
    }

    void removeFolderIfEmpty(File dir) {
        removeFolderIfEmpty(dir, 0);
    }

    private void removeFolderIfEmpty(File dir, int depth) {
        if (depth < 3 && dir.isDirectory() && FileFilterUtil.isEmptyDirectory(dir)) {
            addInfo("deleting folder [" + String.valueOf(dir) + "]");
            checkAndDeleteFile(dir);
            removeFolderIfEmpty(dir.getParentFile(), depth + 1);
        }
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    protected int getPeriodOffsetForDeletionTarget() {
        return (-this.maxHistory) - 1;
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public void setTotalSizeCap(long totalSizeCap) {
        this.totalSizeCap = totalSizeCap;
    }

    public String toString() {
        return "c.q.l.core.rolling.helper.TimeBasedArchiveRemover";
    }

    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/TimeBasedArchiveRemover$ArhiveRemoverRunnable.class */
    public class ArhiveRemoverRunnable implements Runnable {
        Instant now;

        ArhiveRemoverRunnable(Instant now) {
            this.now = now;
        }

        @Override // java.lang.Runnable
        public void run() {
            TimeBasedArchiveRemover.this.clean(this.now);
            if (TimeBasedArchiveRemover.this.totalSizeCap != 0 && TimeBasedArchiveRemover.this.totalSizeCap > 0) {
                TimeBasedArchiveRemover.this.capTotalSize(this.now);
            }
        }
    }
}
