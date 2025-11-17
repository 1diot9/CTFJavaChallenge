package org.springframework.core.log;

import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.NoOpLog;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/log/CompositeLog.class */
final class CompositeLog implements Log {
    private static final Log NO_OP_LOG = new NoOpLog();
    private final List<Log> loggers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompositeLog(List<Log> loggers) {
        this.loggers = loggers;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isFatalEnabled() {
        return isEnabled((v0) -> {
            return v0.isFatalEnabled();
        });
    }

    @Override // org.apache.commons.logging.Log
    public boolean isErrorEnabled() {
        return isEnabled((v0) -> {
            return v0.isErrorEnabled();
        });
    }

    @Override // org.apache.commons.logging.Log
    public boolean isWarnEnabled() {
        return isEnabled((v0) -> {
            return v0.isWarnEnabled();
        });
    }

    @Override // org.apache.commons.logging.Log
    public boolean isInfoEnabled() {
        return isEnabled((v0) -> {
            return v0.isInfoEnabled();
        });
    }

    @Override // org.apache.commons.logging.Log
    public boolean isDebugEnabled() {
        return isEnabled((v0) -> {
            return v0.isDebugEnabled();
        });
    }

    @Override // org.apache.commons.logging.Log
    public boolean isTraceEnabled() {
        return isEnabled((v0) -> {
            return v0.isTraceEnabled();
        });
    }

    private boolean isEnabled(Predicate<Log> predicate) {
        return getLogger(predicate) != NO_OP_LOG;
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message) {
        getLogger((v0) -> {
            return v0.isFatalEnabled();
        }).fatal(message);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message, Throwable ex) {
        getLogger((v0) -> {
            return v0.isFatalEnabled();
        }).fatal(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message) {
        getLogger((v0) -> {
            return v0.isErrorEnabled();
        }).error(message);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message, Throwable ex) {
        getLogger((v0) -> {
            return v0.isErrorEnabled();
        }).error(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message) {
        getLogger((v0) -> {
            return v0.isWarnEnabled();
        }).warn(message);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message, Throwable ex) {
        getLogger((v0) -> {
            return v0.isWarnEnabled();
        }).warn(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message) {
        getLogger((v0) -> {
            return v0.isInfoEnabled();
        }).info(message);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message, Throwable ex) {
        getLogger((v0) -> {
            return v0.isInfoEnabled();
        }).info(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message) {
        getLogger((v0) -> {
            return v0.isDebugEnabled();
        }).debug(message);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message, Throwable ex) {
        getLogger((v0) -> {
            return v0.isDebugEnabled();
        }).debug(message, ex);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message) {
        getLogger((v0) -> {
            return v0.isTraceEnabled();
        }).trace(message);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message, Throwable ex) {
        getLogger((v0) -> {
            return v0.isTraceEnabled();
        }).trace(message, ex);
    }

    private Log getLogger(Predicate<Log> predicate) {
        for (Log logger : this.loggers) {
            if (predicate.test(logger)) {
                return logger;
            }
        }
        return NO_OP_LOG;
    }
}
