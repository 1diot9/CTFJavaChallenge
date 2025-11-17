package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/TimeBasedFileNamingAndTriggeringPolicyBase.class */
public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E> extends ContextAwareBase implements TimeBasedFileNamingAndTriggeringPolicy<E> {
    private static String COLLIDING_DATE_FORMAT_URL = "http://logback.qos.ch/codes.html#rfa_collision_in_dateFormat";
    protected TimeBasedRollingPolicy<E> tbrp;
    protected String elapsedPeriodsFileName;
    protected RollingCalendar rc;
    protected ArchiveRemover archiveRemover = null;
    protected long artificialCurrentTime = -1;
    protected AtomicLong atomicNextCheck = new AtomicLong(0);
    protected Instant dateInCurrentPeriod = null;
    protected boolean started = false;
    protected boolean errorFree = true;

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.started;
    }

    public void start() {
        DateTokenConverter<Object> dtc = this.tbrp.fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc == null) {
            throw new IllegalStateException("FileNamePattern [" + this.tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
        }
        if (dtc.getZoneId() != null) {
            TimeZone tz = TimeZone.getTimeZone(dtc.getZoneId());
            this.rc = new RollingCalendar(dtc.getDatePattern(), tz, Locale.getDefault());
        } else {
            this.rc = new RollingCalendar(dtc.getDatePattern());
        }
        addInfo("The date pattern is '" + dtc.getDatePattern() + "' from file name pattern '" + this.tbrp.fileNamePattern.getPattern() + "'.");
        this.rc.printPeriodicity(this);
        if (!this.rc.isCollisionFree()) {
            addError("The date format in FileNamePattern will result in collisions in the names of archived log files.");
            addError("For more information, please visit " + COLLIDING_DATE_FORMAT_URL);
            withErrors();
            return;
        }
        long timestamp = getCurrentTime();
        setDateInCurrentPeriod(timestamp);
        if (this.tbrp.getParentsRawFileProperty() != null) {
            File currentFile = new File(this.tbrp.getParentsRawFileProperty());
            if (currentFile.exists() && currentFile.canRead()) {
                timestamp = currentFile.lastModified();
                setDateInCurrentPeriod(timestamp);
            }
        }
        addInfo("Setting initial period to " + String.valueOf(this.dateInCurrentPeriod));
        long nextCheck = computeNextCheck(timestamp);
        this.atomicNextCheck.set(nextCheck);
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.started = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long computeNextCheck(long timestamp) {
        return this.rc.getNextTriggeringDate(Instant.ofEpochMilli(timestamp)).toEpochMilli();
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public String getElapsedPeriodsFileName() {
        return this.elapsedPeriodsFileName;
    }

    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWithoutCompSuffix.convert(this.dateInCurrentPeriod);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDateInCurrentPeriod(long timestamp) {
        this.dateInCurrentPeriod = Instant.ofEpochMilli(timestamp);
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public void setCurrentTime(long timeInMillis) {
        this.artificialCurrentTime = timeInMillis;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public long getCurrentTime() {
        if (this.artificialCurrentTime >= 0) {
            return this.artificialCurrentTime;
        }
        return System.currentTimeMillis();
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> _tbrp) {
        this.tbrp = _tbrp;
    }

    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
    public ArchiveRemover getArchiveRemover() {
        return this.archiveRemover;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void withErrors() {
        this.errorFree = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isErrorFree() {
        return this.errorFree;
    }
}
