package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/RollingPolicyBase.class */
public abstract class RollingPolicyBase extends ContextAwareBase implements RollingPolicy {
    protected CompressionMode compressionMode = CompressionMode.NONE;
    FileNamePattern fileNamePattern;
    protected String fileNamePatternStr;
    private FileAppender<?> parent;
    FileNamePattern zipEntryFileNamePattern;
    private boolean started;

    /* JADX INFO: Access modifiers changed from: protected */
    public void determineCompressionMode() {
        if (this.fileNamePatternStr.endsWith(".gz")) {
            addInfo("Will use gz compression");
            this.compressionMode = CompressionMode.GZ;
        } else if (this.fileNamePatternStr.endsWith(".zip")) {
            addInfo("Will use zip compression");
            this.compressionMode = CompressionMode.ZIP;
        } else {
            addInfo("No compression will be used");
            this.compressionMode = CompressionMode.NONE;
        }
    }

    public void setFileNamePattern(String fnp) {
        this.fileNamePatternStr = fnp;
    }

    public String getFileNamePattern() {
        return this.fileNamePatternStr;
    }

    @Override // ch.qos.logback.core.rolling.RollingPolicy
    public CompressionMode getCompressionMode() {
        return this.compressionMode;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.started;
    }

    public void start() {
        this.started = true;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.started = false;
    }

    @Override // ch.qos.logback.core.rolling.RollingPolicy
    public void setParent(FileAppender<?> appender) {
        this.parent = appender;
    }

    public boolean isParentPrudent() {
        return this.parent.isPrudent();
    }

    public String getParentsRawFileProperty() {
        return this.parent.rawFileProperty();
    }
}
