package io.micrometer.common.util.internal.logging;

import org.slf4j.spi.LocationAwareLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/internal/logging/LocationAwareSlf4JLogger.class */
public final class LocationAwareSlf4JLogger extends AbstractInternalLogger {
    static final String FQCN = LocationAwareSlf4JLogger.class.getName();
    private static final long serialVersionUID = -8292030083201538180L;
    private final transient LocationAwareLogger logger;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocationAwareSlf4JLogger(LocationAwareLogger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    private void log(int level, String message) {
        this.logger.log(null, FQCN, level, message, null, null);
    }

    private void log(int level, String message, Throwable cause) {
        this.logger.log(null, FQCN, level, message, null, cause);
    }

    private void log(int level, org.slf4j.helpers.FormattingTuple tuple) {
        this.logger.log(null, FQCN, level, tuple.getMessage(), tuple.getArgArray(), tuple.getThrowable());
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String msg) {
        if (isTraceEnabled()) {
            log(0, msg);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            log(0, org.slf4j.helpers.MessageFormatter.format(format, arg));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String format, Object argA, Object argB) {
        if (isTraceEnabled()) {
            log(0, org.slf4j.helpers.MessageFormatter.format(format, argA, argB));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String format, Object... argArray) {
        if (isTraceEnabled()) {
            log(0, org.slf4j.helpers.MessageFormatter.arrayFormat(format, argArray));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            log(0, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String msg) {
        if (isDebugEnabled()) {
            log(10, msg);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            log(10, org.slf4j.helpers.MessageFormatter.format(format, arg));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String format, Object argA, Object argB) {
        if (isDebugEnabled()) {
            log(10, org.slf4j.helpers.MessageFormatter.format(format, argA, argB));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String format, Object... argArray) {
        if (isDebugEnabled()) {
            log(10, org.slf4j.helpers.MessageFormatter.arrayFormat(format, argArray));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            log(10, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String msg) {
        if (isInfoEnabled()) {
            log(20, msg);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            log(20, org.slf4j.helpers.MessageFormatter.format(format, arg));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String format, Object argA, Object argB) {
        if (isInfoEnabled()) {
            log(20, org.slf4j.helpers.MessageFormatter.format(format, argA, argB));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String format, Object... argArray) {
        if (isInfoEnabled()) {
            log(20, org.slf4j.helpers.MessageFormatter.arrayFormat(format, argArray));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            log(20, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String msg) {
        if (isWarnEnabled()) {
            log(30, msg);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            log(30, org.slf4j.helpers.MessageFormatter.format(format, arg));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String format, Object... argArray) {
        if (isWarnEnabled()) {
            log(30, org.slf4j.helpers.MessageFormatter.arrayFormat(format, argArray));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String format, Object argA, Object argB) {
        if (isWarnEnabled()) {
            log(30, org.slf4j.helpers.MessageFormatter.format(format, argA, argB));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            log(30, msg, t);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String msg) {
        if (isErrorEnabled()) {
            log(40, msg);
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            log(40, org.slf4j.helpers.MessageFormatter.format(format, arg));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String format, Object argA, Object argB) {
        if (isErrorEnabled()) {
            log(40, org.slf4j.helpers.MessageFormatter.format(format, argA, argB));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String format, Object... argArray) {
        if (isErrorEnabled()) {
            log(40, org.slf4j.helpers.MessageFormatter.arrayFormat(format, argArray));
        }
    }

    @Override // io.micrometer.common.util.internal.logging.InternalLogger
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            log(40, msg, t);
        }
    }
}
