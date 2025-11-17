package ch.qos.logback.core.rolling;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.InvocationGate;
import ch.qos.logback.core.util.SimpleInvocationGate;
import java.io.File;
import java.time.Instant;

@NoAutoStart
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/SizeAndTimeBasedFNATP.class */
public class SizeAndTimeBasedFNATP<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E> {
    volatile int currentPeriodsCounter;
    FileSize maxFileSize;
    Duration checkIncrement;
    static String MISSING_INT_TOKEN = "Missing integer token, that is %i, in FileNamePattern [";
    static String MISSING_DATE_TOKEN = "Missing date token, that is %d, in FileNamePattern [";
    private final Usage usage;
    InvocationGate invocationGate;

    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/SizeAndTimeBasedFNATP$Usage.class */
    enum Usage {
        EMBEDDED,
        DIRECT
    }

    public SizeAndTimeBasedFNATP() {
        this(Usage.DIRECT);
    }

    public SizeAndTimeBasedFNATP(Usage usage) {
        this.currentPeriodsCounter = 0;
        this.checkIncrement = null;
        this.invocationGate = new SimpleInvocationGate();
        this.usage = usage;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        super.start();
        if (this.usage == Usage.DIRECT) {
            addWarn(CoreConstants.SIZE_AND_TIME_BASED_FNATP_IS_DEPRECATED);
            addWarn("For more information see http://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedRollingPolicy");
        }
        if (!super.isErrorFree()) {
            return;
        }
        if (this.maxFileSize == null) {
            addError("maxFileSize property is mandatory.");
            withErrors();
        }
        if (this.checkIncrement != null) {
            this.invocationGate = new SimpleInvocationGate(this.checkIncrement);
        }
        if (!validateDateAndIntegerTokens()) {
            withErrors();
            return;
        }
        this.archiveRemover = createArchiveRemover();
        this.archiveRemover.setContext(this.context);
        String regex = this.tbrp.fileNamePattern.toRegexForFixedDate(this.dateInCurrentPeriod);
        String stemRegex = FileFilterUtil.afterLastSlash(regex);
        computeCurrentPeriodsHighestCounterValue(stemRegex);
        if (isErrorFree()) {
            this.started = true;
        }
    }

    private boolean validateDateAndIntegerTokens() {
        boolean inError = false;
        if (this.tbrp.fileNamePattern.getIntegerTokenConverter() == null) {
            inError = true;
            addError(MISSING_INT_TOKEN + this.tbrp.fileNamePatternStr + "]");
            addError(CoreConstants.SEE_MISSING_INTEGER_TOKEN);
        }
        if (this.tbrp.fileNamePattern.getPrimaryDateTokenConverter() == null) {
            inError = true;
            addError(MISSING_DATE_TOKEN + this.tbrp.fileNamePatternStr + "]");
        }
        return !inError;
    }

    protected ArchiveRemover createArchiveRemover() {
        return new SizeAndTimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc);
    }

    void computeCurrentPeriodsHighestCounterValue(String stemRegex) {
        File file = new File(getCurrentPeriodsFileNameWithoutCompressionSuffix());
        File parentDir = file.getParentFile();
        File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
        if (matchingFileArray == null || matchingFileArray.length == 0) {
            this.currentPeriodsCounter = 0;
            return;
        }
        this.currentPeriodsCounter = FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
        if (this.tbrp.getParentsRawFileProperty() != null || this.tbrp.compressionMode != CompressionMode.NONE) {
            this.currentPeriodsCounter++;
        }
    }

    @Override // ch.qos.logback.core.rolling.TriggeringPolicy
    public boolean isTriggeringEvent(File activeFile, E event) {
        long currentTime = getCurrentTime();
        long localNextCheck = this.atomicNextCheck.get();
        if (currentTime >= localNextCheck) {
            long nextCheckCandidate = computeNextCheck(currentTime);
            this.atomicNextCheck.set(nextCheckCandidate);
            Instant instantInElapsedPeriod = this.dateInCurrentPeriod;
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(instantInElapsedPeriod, Integer.valueOf(this.currentPeriodsCounter));
            this.currentPeriodsCounter = 0;
            setDateInCurrentPeriod(currentTime);
            return true;
        }
        return checkSizeBasedTrigger(activeFile, currentTime);
    }

    private boolean checkSizeBasedTrigger(File activeFile, long currentTime) {
        if (this.invocationGate.isTooSoon(currentTime)) {
            return false;
        }
        if (activeFile == null) {
            addWarn("activeFile == null");
            return false;
        }
        if (this.maxFileSize == null) {
            addWarn("maxFileSize = null");
            return false;
        }
        if (activeFile.length() >= this.maxFileSize.getSize()) {
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(this.dateInCurrentPeriod, Integer.valueOf(this.currentPeriodsCounter));
            this.currentPeriodsCounter++;
            return true;
        }
        return false;
    }

    public Duration getCheckIncrement() {
        return this.checkIncrement;
    }

    public void setCheckIncrement(Duration checkIncrement) {
        this.checkIncrement = checkIncrement;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase, ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(this.dateInCurrentPeriod, Integer.valueOf(this.currentPeriodsCounter));
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }
}
