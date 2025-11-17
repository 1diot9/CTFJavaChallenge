package io.micrometer.common.util.internal.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/internal/logging/JdkLogger.class */
class JdkLogger extends AbstractInternalLogger {
    private static final long serialVersionUID = -1767272577989225979L;
    final transient Logger logger;
    static final String SELF = JdkLogger.class.getName();
    static final String SUPER = AbstractInternalLogger.class.getName();

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdkLogger(Logger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(Level.FINEST);
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String msg) {
        if (this.logger.isLoggable(Level.FINEST)) {
            log(SELF, Level.FINEST, msg, (Throwable) null);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String format, Object arg) {
        if (this.logger.isLoggable(Level.FINEST)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String format, Object argA, Object argB) {
        if (this.logger.isLoggable(Level.FINEST)) {
            FormattingTuple ft = MessageFormatter.format(format, argA, argB);
            log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String format, Object... argArray) {
        if (this.logger.isLoggable(Level.FINEST)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.FINEST)) {
            log(SELF, Level.FINEST, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String msg) {
        if (this.logger.isLoggable(Level.FINE)) {
            log(SELF, Level.FINE, msg, (Throwable) null);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String format, Object arg) {
        if (this.logger.isLoggable(Level.FINE)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String format, Object argA, Object argB) {
        if (this.logger.isLoggable(Level.FINE)) {
            FormattingTuple ft = MessageFormatter.format(format, argA, argB);
            log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String format, Object... argArray) {
        if (this.logger.isLoggable(Level.FINE)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.FINE)) {
            log(SELF, Level.FINE, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String msg) {
        if (this.logger.isLoggable(Level.INFO)) {
            log(SELF, Level.INFO, msg, (Throwable) null);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String format, Object arg) {
        if (this.logger.isLoggable(Level.INFO)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String format, Object argA, Object argB) {
        if (this.logger.isLoggable(Level.INFO)) {
            FormattingTuple ft = MessageFormatter.format(format, argA, argB);
            log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String format, Object... argArray) {
        if (this.logger.isLoggable(Level.INFO)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.INFO)) {
            log(SELF, Level.INFO, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(Level.WARNING);
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String msg) {
        if (this.logger.isLoggable(Level.WARNING)) {
            log(SELF, Level.WARNING, msg, (Throwable) null);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String format, Object arg) {
        if (this.logger.isLoggable(Level.WARNING)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.WARNING, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String format, Object argA, Object argB) {
        if (this.logger.isLoggable(Level.WARNING)) {
            FormattingTuple ft = MessageFormatter.format(format, argA, argB);
            log(SELF, Level.WARNING, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String format, Object... argArray) {
        if (this.logger.isLoggable(Level.WARNING)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            log(SELF, Level.WARNING, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.WARNING)) {
            log(SELF, Level.WARNING, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String msg) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            log(SELF, Level.SEVERE, msg, (Throwable) null);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String format, Object arg) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            log(SELF, Level.SEVERE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String format, Object argA, Object argB) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            FormattingTuple ft = MessageFormatter.format(format, argA, argB);
            log(SELF, Level.SEVERE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String format, Object... arguments) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            log(SELF, Level.SEVERE, ft.getMessage(), ft.getThrowable());
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            log(SELF, Level.SEVERE, msg, t);
        }
    }

    private void log(String callerFQCN, Level level, String msg, Throwable t) {
        LogRecord record = new LogRecord(level, msg);
        record.setLoggerName(name());
        record.setThrown(t);
        fillCallerData(callerFQCN, record);
        this.logger.log(record);
    }

    private static void fillCallerData(String callerFQCN, LogRecord record) {
        StackTraceElement[] steArray = new Throwable().getStackTrace();
        int selfIndex = -1;
        for (int i = 0; i < steArray.length; i++) {
            String className = steArray[i].getClassName();
            if (className.equals(callerFQCN) || className.equals(SUPER)) {
                selfIndex = i;
                break;
            }
        }
        int found = -1;
        int i2 = selfIndex + 1;
        while (true) {
            if (i2 >= steArray.length) {
                break;
            }
            String className2 = steArray[i2].getClassName();
            if (className2.equals(callerFQCN) || className2.equals(SUPER)) {
                i2++;
            } else {
                found = i2;
                break;
            }
        }
        if (found != -1) {
            StackTraceElement ste = steArray[found];
            record.setSourceClassName(ste.getClassName());
            record.setSourceMethodName(ste.getMethodName());
        }
    }
}
