package ch.qos.logback.core.rolling;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.util.ContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/RollingFileAppender.class */
public class RollingFileAppender<E> extends FileAppender<E> {
    File currentlyActiveFile;
    TriggeringPolicy<E> triggeringPolicy;
    RollingPolicy rollingPolicy;
    Lock triggeringPolicyLock = new ReentrantLock();
    private static String RFA_NO_TP_URL = "http://logback.qos.ch/codes.html#rfa_no_tp";
    private static String RFA_NO_RP_URL = "http://logback.qos.ch/codes.html#rfa_no_rp";
    private static String COLLISION_URL = "http://logback.qos.ch/codes.html#rfa_collision";
    private static String RFA_LATE_FILE_URL = "http://logback.qos.ch/codes.html#rfa_file_after";

    @Override // ch.qos.logback.core.FileAppender, ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.triggeringPolicy == null) {
            addWarn("No TriggeringPolicy was set for the RollingFileAppender named " + getName());
            addWarn("For more information, please visit " + RFA_NO_TP_URL);
            return;
        }
        if (!this.triggeringPolicy.isStarted()) {
            addWarn("TriggeringPolicy has not started. RollingFileAppender will not start");
            return;
        }
        if (checkForCollisionsInPreviousRollingFileAppenders()) {
            addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
            addError("For more information, please visit " + COLLISION_WITH_EARLIER_APPENDER_URL);
            return;
        }
        if (!this.append) {
            addWarn("Append mode is mandatory for RollingFileAppender. Defaulting to append=true.");
            this.append = true;
        }
        if (this.rollingPolicy == null) {
            addError("No RollingPolicy was set for the RollingFileAppender named " + getName());
            addError("For more information, please visit " + RFA_NO_RP_URL);
            return;
        }
        if (checkForFileAndPatternCollisions()) {
            addError("File property collides with fileNamePattern. Aborting.");
            addError("For more information, please visit " + COLLISION_URL);
            return;
        }
        if (isPrudent()) {
            if (rawFileProperty() != null) {
                addWarn("Setting \"File\" property to null on account of prudent mode");
                setFile(null);
            }
            if (this.rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
                addError("Compression is not supported in prudent mode. Aborting");
                return;
            }
        }
        this.currentlyActiveFile = new File(getFile());
        addInfo("Active log file name: " + getFile());
        super.start();
    }

    private boolean checkForFileAndPatternCollisions() {
        FileNamePattern fileNamePattern;
        if ((this.triggeringPolicy instanceof RollingPolicyBase) && (fileNamePattern = ((RollingPolicyBase) this.triggeringPolicy).fileNamePattern) != null && this.fileName != null) {
            return this.fileName.matches(fileNamePattern.toRegex());
        }
        return false;
    }

    private boolean checkForCollisionsInPreviousRollingFileAppenders() {
        boolean z = false;
        if ((this.triggeringPolicy instanceof RollingPolicyBase) && innerCheckForFileNamePatternCollisionInPreviousRFA(((RollingPolicyBase) this.triggeringPolicy).fileNamePattern)) {
            z = true;
        }
        return z;
    }

    private boolean innerCheckForFileNamePatternCollisionInPreviousRFA(FileNamePattern fileNamePattern) {
        boolean collisionsDetected = false;
        Map<String, FileNamePattern> map = (Map) this.context.getObject(CoreConstants.RFA_FILENAME_PATTERN_COLLISION_MAP);
        if (map == null) {
            return false;
        }
        for (Map.Entry<String, FileNamePattern> entry : map.entrySet()) {
            if (fileNamePattern.equals(entry.getValue())) {
                addErrorForCollision("FileNamePattern", entry.getValue().toString(), entry.getKey());
                collisionsDetected = true;
            }
        }
        if (this.name != null) {
            map.put(getName(), fileNamePattern);
        }
        return collisionsDetected;
    }

    @Override // ch.qos.logback.core.FileAppender, ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        if (!isStarted()) {
            return;
        }
        super.stop();
        if (this.rollingPolicy != null) {
            this.rollingPolicy.stop();
        }
        if (this.triggeringPolicy != null) {
            this.triggeringPolicy.stop();
        }
        Map<String, FileNamePattern> map = ContextUtil.getFilenamePatternCollisionMap(this.context);
        if (map != null && getName() != null) {
            map.remove(getName());
        }
    }

    @Override // ch.qos.logback.core.FileAppender
    public void setFile(String file) {
        if (file != null && (this.triggeringPolicy != null || this.rollingPolicy != null)) {
            addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
            addError("For more information, please visit " + RFA_LATE_FILE_URL);
        }
        super.setFile(file);
    }

    @Override // ch.qos.logback.core.FileAppender
    public String getFile() {
        return this.rollingPolicy.getActiveFileName();
    }

    public void rollover() {
        this.streamWriteLock.lock();
        try {
            closeOutputStream();
            attemptRollover();
            attemptOpenFile();
        } finally {
            this.streamWriteLock.unlock();
        }
    }

    private void attemptOpenFile() {
        try {
            this.currentlyActiveFile = new File(this.rollingPolicy.getActiveFileName());
            openFile(this.rollingPolicy.getActiveFileName());
        } catch (IOException e) {
            addError("setFile(" + this.fileName + ", false) call failed.", e);
        }
    }

    private void attemptRollover() {
        try {
            this.rollingPolicy.rollover();
        } catch (RolloverFailure e) {
            addWarn("RolloverFailure occurred. Deferring roll-over.");
            this.append = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.OutputStreamAppender
    public void subAppend(E event) {
        this.triggeringPolicyLock.lock();
        try {
            if (this.triggeringPolicy.isTriggeringEvent(this.currentlyActiveFile, event)) {
                rollover();
            }
            super.subAppend(event);
        } finally {
            this.triggeringPolicyLock.unlock();
        }
    }

    public RollingPolicy getRollingPolicy() {
        return this.rollingPolicy;
    }

    public TriggeringPolicy<E> getTriggeringPolicy() {
        return this.triggeringPolicy;
    }

    public void setRollingPolicy(RollingPolicy policy) {
        this.rollingPolicy = policy;
        if (this.rollingPolicy instanceof TriggeringPolicy) {
            this.triggeringPolicy = (TriggeringPolicy) policy;
        }
    }

    public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
        this.triggeringPolicy = policy;
        if (policy instanceof RollingPolicy) {
            this.rollingPolicy = (RollingPolicy) policy;
        }
    }
}
