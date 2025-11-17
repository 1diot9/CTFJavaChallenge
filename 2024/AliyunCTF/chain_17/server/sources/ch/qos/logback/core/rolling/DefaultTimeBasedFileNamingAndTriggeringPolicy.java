package ch.qos.logback.core.rolling;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;
import java.io.File;
import java.time.Instant;

@NoAutoStart
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/DefaultTimeBasedFileNamingAndTriggeringPolicy.class */
public class DefaultTimeBasedFileNamingAndTriggeringPolicy<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E> {
    @Override // ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        super.start();
        if (!super.isErrorFree()) {
            return;
        }
        if (this.tbrp.fileNamePattern.hasIntegerTokenCOnverter()) {
            addError("Filename pattern [" + String.valueOf(this.tbrp.fileNamePattern) + "] contains an integer token converter, i.e. %i, INCOMPATIBLE with this configuration. Remove it.");
            return;
        }
        this.archiveRemover = new TimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc);
        this.archiveRemover.setContext(this.context);
        this.started = true;
    }

    @Override // ch.qos.logback.core.rolling.TriggeringPolicy
    public boolean isTriggeringEvent(File activeFile, E event) {
        long currentTime = getCurrentTime();
        long localNextCheck = this.atomicNextCheck.get();
        if (currentTime >= localNextCheck) {
            long nextCheck = computeNextCheck(currentTime);
            this.atomicNextCheck.set(nextCheck);
            Instant instantOfElapsedPeriod = this.dateInCurrentPeriod;
            addInfo("Elapsed period: " + instantOfElapsedPeriod.toString());
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convert(instantOfElapsedPeriod);
            setDateInCurrentPeriod(currentTime);
            return true;
        }
        return false;
    }

    public String toString() {
        return "c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy";
    }
}
