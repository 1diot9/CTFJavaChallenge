package ch.qos.logback.core.rolling;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.RenameUtil;
import ch.qos.logback.core.util.FileSize;
import java.io.File;
import java.time.Instant;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/TimeBasedRollingPolicy.class */
public class TimeBasedRollingPolicy<E> extends RollingPolicyBase implements TriggeringPolicy<E> {
    static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
    FileNamePattern fileNamePatternWithoutCompSuffix;
    private Compressor compressor;
    Future<?> compressionFuture;
    Future<?> cleanUpFuture;
    private ArchiveRemover archiveRemover;
    TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy;
    private RenameUtil renameUtil = new RenameUtil();
    private int maxHistory = 0;
    protected FileSize totalSizeCap = new FileSize(0);
    boolean cleanHistoryOnStart = false;

    @Override // ch.qos.logback.core.rolling.RollingPolicyBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.renameUtil.setContext(this.context);
        if (this.fileNamePatternStr != null) {
            this.fileNamePattern = new FileNamePattern(this.fileNamePatternStr, this.context);
            determineCompressionMode();
            this.compressor = new Compressor(this.compressionMode);
            this.compressor.setContext(this.context);
            this.fileNamePatternWithoutCompSuffix = new FileNamePattern(Compressor.computeFileNameStrWithoutCompSuffix(this.fileNamePatternStr, this.compressionMode), this.context);
            addInfo("Will use the pattern " + String.valueOf(this.fileNamePatternWithoutCompSuffix) + " for the active file");
            if (this.compressionMode == CompressionMode.ZIP) {
                String zipEntryFileNamePatternStr = transformFileNamePattern2ZipEntry(this.fileNamePatternStr);
                this.zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, this.context);
            }
            if (this.timeBasedFileNamingAndTriggeringPolicy == null) {
                this.timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy();
            }
            this.timeBasedFileNamingAndTriggeringPolicy.setContext(this.context);
            this.timeBasedFileNamingAndTriggeringPolicy.setTimeBasedRollingPolicy(this);
            this.timeBasedFileNamingAndTriggeringPolicy.start();
            if (!this.timeBasedFileNamingAndTriggeringPolicy.isStarted()) {
                addWarn("Subcomponent did not start. TimeBasedRollingPolicy will not start.");
                return;
            }
            if (this.maxHistory != 0) {
                this.archiveRemover = this.timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover();
                this.archiveRemover.setMaxHistory(this.maxHistory);
                this.archiveRemover.setTotalSizeCap(this.totalSizeCap.getSize());
                if (this.cleanHistoryOnStart) {
                    addInfo("Cleaning on start up");
                    Instant now = Instant.ofEpochMilli(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
                    this.cleanUpFuture = this.archiveRemover.cleanAsynchronously(now);
                }
            } else if (!isUnboundedTotalSizeCap()) {
                addWarn("'maxHistory' is not set, ignoring 'totalSizeCap' option with value [" + String.valueOf(this.totalSizeCap) + "]");
            }
            super.start();
            return;
        }
        addWarn(FNP_NOT_SET);
        addWarn(CoreConstants.SEE_FNP_NOT_SET);
        throw new IllegalStateException("The FileNamePattern option must be set before using TimeBasedRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isUnboundedTotalSizeCap() {
        return this.totalSizeCap.getSize() == 0;
    }

    @Override // ch.qos.logback.core.rolling.RollingPolicyBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        if (!isStarted()) {
            return;
        }
        waitForAsynchronousJobToStop(this.compressionFuture, "compression");
        waitForAsynchronousJobToStop(this.cleanUpFuture, "clean-up");
        super.stop();
    }

    private void waitForAsynchronousJobToStop(Future<?> aFuture, String jobDescription) {
        if (aFuture != null) {
            try {
                aFuture.get(30L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                addError("Timeout while waiting for " + jobDescription + " job to finish", e);
            } catch (Exception e2) {
                addError("Unexpected exception while waiting for " + jobDescription + " job to finish", e2);
            }
        }
    }

    private String transformFileNamePattern2ZipEntry(String fileNamePatternStr) {
        String slashified = FileFilterUtil.slashify(fileNamePatternStr);
        return FileFilterUtil.afterLastSlash(slashified);
    }

    public void setTimeBasedFileNamingAndTriggeringPolicy(TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedTriggering) {
        this.timeBasedFileNamingAndTriggeringPolicy = timeBasedTriggering;
    }

    public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
        return this.timeBasedFileNamingAndTriggeringPolicy;
    }

    @Override // ch.qos.logback.core.rolling.RollingPolicy
    public void rollover() throws RolloverFailure {
        String elapsedPeriodsFileName = this.timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();
        String elapsedPeriodStem = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
        if (this.compressionMode == CompressionMode.NONE) {
            if (getParentsRawFileProperty() != null) {
                this.renameUtil.rename(getParentsRawFileProperty(), elapsedPeriodsFileName);
            }
        } else if (getParentsRawFileProperty() == null) {
            this.compressionFuture = this.compressor.asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, elapsedPeriodStem);
        } else {
            this.compressionFuture = renameRawAndAsyncCompress(elapsedPeriodsFileName, elapsedPeriodStem);
        }
        if (this.archiveRemover != null) {
            Instant now = Instant.ofEpochMilli(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime());
            this.cleanUpFuture = this.archiveRemover.cleanAsynchronously(now);
        }
    }

    Future<?> renameRawAndAsyncCompress(String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
        String parentsRawFile = getParentsRawFileProperty();
        String tmpTarget = nameOfCompressedFile + System.nanoTime() + ".tmp";
        this.renameUtil.rename(parentsRawFile, tmpTarget);
        return this.compressor.asyncCompress(tmpTarget, nameOfCompressedFile, innerEntryName);
    }

    @Override // ch.qos.logback.core.rolling.RollingPolicy
    public String getActiveFileName() {
        String parentsRawFileProperty = getParentsRawFileProperty();
        if (parentsRawFileProperty != null) {
            return parentsRawFileProperty;
        }
        return this.timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
    }

    @Override // ch.qos.logback.core.rolling.TriggeringPolicy
    public boolean isTriggeringEvent(File activeFile, E event) {
        return this.timeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent(activeFile, event);
    }

    public int getMaxHistory() {
        return this.maxHistory;
    }

    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public boolean isCleanHistoryOnStart() {
        return this.cleanHistoryOnStart;
    }

    public void setCleanHistoryOnStart(boolean cleanHistoryOnStart) {
        this.cleanHistoryOnStart = cleanHistoryOnStart;
    }

    public String toString() {
        return "c.q.l.core.rolling.TimeBasedRollingPolicy@" + hashCode();
    }

    public void setTotalSizeCap(FileSize totalSizeCap) {
        addInfo("setting totalSizeCap to " + totalSizeCap.toString());
        this.totalSizeCap = totalSizeCap;
    }
}
