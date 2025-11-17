package ch.qos.logback.core.rolling;

import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.InvocationGate;
import ch.qos.logback.core.util.SimpleInvocationGate;
import java.io.File;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/SizeBasedTriggeringPolicy.class */
public class SizeBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    public static final String SEE_SIZE_FORMAT = "http://logback.qos.ch/codes.html#sbtp_size_format";
    public static final long DEFAULT_MAX_FILE_SIZE = 10485760;
    FileSize maxFileSize = new FileSize(DEFAULT_MAX_FILE_SIZE);
    InvocationGate invocationGate = new SimpleInvocationGate();
    Duration checkIncrement = null;

    @Override // ch.qos.logback.core.rolling.TriggeringPolicyBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.checkIncrement != null) {
            this.invocationGate = new SimpleInvocationGate(this.checkIncrement);
        }
        super.start();
    }

    @Override // ch.qos.logback.core.rolling.TriggeringPolicy
    public boolean isTriggeringEvent(File activeFile, E event) {
        long now = System.currentTimeMillis();
        return !this.invocationGate.isTooSoon(now) && activeFile.length() >= this.maxFileSize.getSize();
    }

    public FileSize getMaxFileSize() {
        return this.maxFileSize;
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }

    public Duration getCheckIncrement() {
        return this.checkIncrement;
    }

    public void setCheckIncrement(Duration checkIncrement) {
        this.checkIncrement = checkIncrement;
    }
}
