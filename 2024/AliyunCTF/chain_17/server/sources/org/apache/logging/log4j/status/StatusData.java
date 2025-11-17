package org.apache.logging.log4j.status;

import ch.qos.logback.core.CoreConstants;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/status/StatusData.class */
public class StatusData implements Serializable {
    private static final long serialVersionUID = -4341916115118014017L;
    private final long timestamp = System.currentTimeMillis();
    private final StackTraceElement caller;
    private final Level level;
    private final Message msg;
    private String threadName;
    private final Throwable throwable;

    public StatusData(final StackTraceElement caller, final Level level, final Message msg, final Throwable t, final String threadName) {
        this.caller = caller;
        this.level = level;
        this.msg = msg;
        this.throwable = t;
        this.threadName = threadName;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public StackTraceElement getStackTraceElement() {
        return this.caller;
    }

    public Level getLevel() {
        return this.level;
    }

    public Message getMessage() {
        return this.msg;
    }

    public String getThreadName() {
        if (this.threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }
        return this.threadName;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String getFormattedStatus() {
        Throwable t;
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat(CoreConstants.ISO8601_PATTERN);
        sb.append(format.format(new Date(this.timestamp)));
        sb.append(' ');
        sb.append(getThreadName());
        sb.append(' ');
        sb.append(this.level.toString());
        sb.append(' ');
        sb.append(this.msg.getFormattedMessage());
        Object[] params = this.msg.getParameters();
        if (this.throwable == null && params != null && (params[params.length - 1] instanceof Throwable)) {
            t = (Throwable) params[params.length - 1];
        } else {
            t = this.throwable;
        }
        if (t != null) {
            sb.append(' ');
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(baos));
            sb.append(baos.toString());
        }
        return sb.toString();
    }

    public String toString() {
        return getMessage().getFormattedMessage();
    }
}
