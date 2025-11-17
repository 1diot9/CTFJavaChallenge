package org.apache.commons.logging;

import java.io.Serializable;
import java.util.function.Function;
import java.util.logging.LogRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter.class */
final class LogAdapter {
    private static final boolean log4jSpiPresent = isPresent("org.apache.logging.log4j.spi.ExtendedLogger");
    private static final boolean log4jSlf4jProviderPresent = isPresent("org.apache.logging.slf4j.SLF4JProvider");
    private static final boolean slf4jSpiPresent = isPresent("org.slf4j.spi.LocationAwareLogger");
    private static final boolean slf4jApiPresent = isPresent("org.slf4j.Logger");
    private static final Function<String, Log> createLog;

    static {
        if (log4jSpiPresent) {
            if (log4jSlf4jProviderPresent && slf4jSpiPresent) {
                createLog = Slf4jAdapter::createLocationAwareLog;
                return;
            } else {
                createLog = Log4jAdapter::createLog;
                return;
            }
        }
        if (slf4jSpiPresent) {
            createLog = Slf4jAdapter::createLocationAwareLog;
        } else if (slf4jApiPresent) {
            createLog = Slf4jAdapter::createLog;
        } else {
            createLog = JavaUtilAdapter::createLog;
        }
    }

    private LogAdapter() {
    }

    public static Log createLog(String name) {
        return createLog.apply(name);
    }

    private static boolean isPresent(String className) {
        try {
            Class.forName(className, false, LogAdapter.class.getClassLoader());
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$Log4jAdapter.class */
    private static class Log4jAdapter {
        private Log4jAdapter() {
        }

        public static Log createLog(String name) {
            return new Log4jLog(name);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$Slf4jAdapter.class */
    private static class Slf4jAdapter {
        private Slf4jAdapter() {
        }

        public static Log createLocationAwareLog(String name) {
            Logger logger = LoggerFactory.getLogger(name);
            if (!(logger instanceof LocationAwareLogger)) {
                return new Slf4jLog(logger);
            }
            LocationAwareLogger locationAwareLogger = (LocationAwareLogger) logger;
            return new Slf4jLocationAwareLog(locationAwareLogger);
        }

        public static Log createLog(String name) {
            return new Slf4jLog(LoggerFactory.getLogger(name));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$JavaUtilAdapter.class */
    private static class JavaUtilAdapter {
        private JavaUtilAdapter() {
        }

        public static Log createLog(String name) {
            return new JavaUtilLog(name);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$Log4jLog.class */
    private static class Log4jLog implements Log, Serializable {
        private static final String FQCN = Log4jLog.class.getName();
        private static final LoggerContext loggerContext = LogManager.getContext(Log4jLog.class.getClassLoader(), false);
        private final String name;
        private final transient ExtendedLogger logger;

        public Log4jLog(String name) {
            this.name = name;
            LoggerContext context = loggerContext;
            this.logger = (context == null ? LogManager.getContext(Log4jLog.class.getClassLoader(), false) : context).getLogger(name);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isFatalEnabled() {
            return this.logger.isEnabled(Level.FATAL);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isErrorEnabled() {
            return this.logger.isEnabled(Level.ERROR);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isWarnEnabled() {
            return this.logger.isEnabled(Level.WARN);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isInfoEnabled() {
            return this.logger.isEnabled(Level.INFO);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isDebugEnabled() {
            return this.logger.isEnabled(Level.DEBUG);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isTraceEnabled() {
            return this.logger.isEnabled(Level.TRACE);
        }

        @Override // org.apache.commons.logging.Log
        public void fatal(Object message) {
            log(Level.FATAL, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void fatal(Object message, Throwable exception) {
            log(Level.FATAL, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void error(Object message) {
            log(Level.ERROR, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void error(Object message, Throwable exception) {
            log(Level.ERROR, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void warn(Object message) {
            log(Level.WARN, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void warn(Object message, Throwable exception) {
            log(Level.WARN, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void info(Object message) {
            log(Level.INFO, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void info(Object message, Throwable exception) {
            log(Level.INFO, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void debug(Object message) {
            log(Level.DEBUG, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void debug(Object message, Throwable exception) {
            log(Level.DEBUG, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void trace(Object message) {
            log(Level.TRACE, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void trace(Object message, Throwable exception) {
            log(Level.TRACE, message, exception);
        }

        private void log(Level level, Object message, Throwable exception) {
            if (message instanceof String) {
                String text = (String) message;
                if (exception != null) {
                    this.logger.logIfEnabled(FQCN, level, (Marker) null, text, exception);
                    return;
                } else {
                    this.logger.logIfEnabled(FQCN, level, null, text);
                    return;
                }
            }
            this.logger.logIfEnabled(FQCN, level, (Marker) null, message, exception);
        }

        protected Object readResolve() {
            return new Log4jLog(this.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$Slf4jLog.class */
    public static class Slf4jLog<T extends Logger> implements Log, Serializable {
        protected final String name;
        protected final transient T logger;

        public Slf4jLog(T logger) {
            this.name = logger.getName();
            this.logger = logger;
        }

        @Override // org.apache.commons.logging.Log
        public boolean isFatalEnabled() {
            return isErrorEnabled();
        }

        @Override // org.apache.commons.logging.Log
        public boolean isErrorEnabled() {
            return this.logger.isErrorEnabled();
        }

        @Override // org.apache.commons.logging.Log
        public boolean isWarnEnabled() {
            return this.logger.isWarnEnabled();
        }

        @Override // org.apache.commons.logging.Log
        public boolean isInfoEnabled() {
            return this.logger.isInfoEnabled();
        }

        @Override // org.apache.commons.logging.Log
        public boolean isDebugEnabled() {
            return this.logger.isDebugEnabled();
        }

        @Override // org.apache.commons.logging.Log
        public boolean isTraceEnabled() {
            return this.logger.isTraceEnabled();
        }

        @Override // org.apache.commons.logging.Log
        public void fatal(Object message) {
            error(message);
        }

        @Override // org.apache.commons.logging.Log
        public void fatal(Object message, Throwable exception) {
            error(message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void error(Object message) {
            if ((message instanceof String) || this.logger.isErrorEnabled()) {
                this.logger.error(String.valueOf(message));
            }
        }

        @Override // org.apache.commons.logging.Log
        public void error(Object message, Throwable exception) {
            if ((message instanceof String) || this.logger.isErrorEnabled()) {
                this.logger.error(String.valueOf(message), exception);
            }
        }

        @Override // org.apache.commons.logging.Log
        public void warn(Object message) {
            if ((message instanceof String) || this.logger.isWarnEnabled()) {
                this.logger.warn(String.valueOf(message));
            }
        }

        @Override // org.apache.commons.logging.Log
        public void warn(Object message, Throwable exception) {
            if ((message instanceof String) || this.logger.isWarnEnabled()) {
                this.logger.warn(String.valueOf(message), exception);
            }
        }

        @Override // org.apache.commons.logging.Log
        public void info(Object message) {
            if ((message instanceof String) || this.logger.isInfoEnabled()) {
                this.logger.info(String.valueOf(message));
            }
        }

        @Override // org.apache.commons.logging.Log
        public void info(Object message, Throwable exception) {
            if ((message instanceof String) || this.logger.isInfoEnabled()) {
                this.logger.info(String.valueOf(message), exception);
            }
        }

        @Override // org.apache.commons.logging.Log
        public void debug(Object message) {
            if ((message instanceof String) || this.logger.isDebugEnabled()) {
                this.logger.debug(String.valueOf(message));
            }
        }

        @Override // org.apache.commons.logging.Log
        public void debug(Object message, Throwable exception) {
            if ((message instanceof String) || this.logger.isDebugEnabled()) {
                this.logger.debug(String.valueOf(message), exception);
            }
        }

        @Override // org.apache.commons.logging.Log
        public void trace(Object message) {
            if ((message instanceof String) || this.logger.isTraceEnabled()) {
                this.logger.trace(String.valueOf(message));
            }
        }

        @Override // org.apache.commons.logging.Log
        public void trace(Object message, Throwable exception) {
            if ((message instanceof String) || this.logger.isTraceEnabled()) {
                this.logger.trace(String.valueOf(message), exception);
            }
        }

        protected Object readResolve() {
            return Slf4jAdapter.createLog(this.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$Slf4jLocationAwareLog.class */
    public static class Slf4jLocationAwareLog extends Slf4jLog<LocationAwareLogger> implements Serializable {
        private static final String FQCN = Slf4jLocationAwareLog.class.getName();

        public Slf4jLocationAwareLog(LocationAwareLogger logger) {
            super(logger);
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void fatal(Object message) {
            error(message);
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void fatal(Object message, Throwable exception) {
            error(message, exception);
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void error(Object message) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isErrorEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 40, String.valueOf(message), null, null);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void error(Object message, Throwable exception) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isErrorEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 40, String.valueOf(message), null, exception);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void warn(Object message) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isWarnEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 30, String.valueOf(message), null, null);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void warn(Object message, Throwable exception) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isWarnEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 30, String.valueOf(message), null, exception);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void info(Object message) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isInfoEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 20, String.valueOf(message), null, null);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void info(Object message, Throwable exception) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isInfoEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 20, String.valueOf(message), null, exception);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void debug(Object message) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isDebugEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 10, String.valueOf(message), null, null);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void debug(Object message, Throwable exception) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isDebugEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 10, String.valueOf(message), null, exception);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void trace(Object message) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isTraceEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 0, String.valueOf(message), null, null);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog, org.apache.commons.logging.Log
        public void trace(Object message, Throwable exception) {
            if ((message instanceof String) || ((LocationAwareLogger) this.logger).isTraceEnabled()) {
                ((LocationAwareLogger) this.logger).log(null, FQCN, 0, String.valueOf(message), null, exception);
            }
        }

        @Override // org.apache.commons.logging.LogAdapter.Slf4jLog
        protected Object readResolve() {
            return Slf4jAdapter.createLocationAwareLog(this.name);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$JavaUtilLog.class */
    private static class JavaUtilLog implements Log, Serializable {
        private final String name;
        private final transient java.util.logging.Logger logger;

        public JavaUtilLog(String name) {
            this.name = name;
            this.logger = java.util.logging.Logger.getLogger(name);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isFatalEnabled() {
            return isErrorEnabled();
        }

        @Override // org.apache.commons.logging.Log
        public boolean isErrorEnabled() {
            return this.logger.isLoggable(java.util.logging.Level.SEVERE);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isWarnEnabled() {
            return this.logger.isLoggable(java.util.logging.Level.WARNING);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isInfoEnabled() {
            return this.logger.isLoggable(java.util.logging.Level.INFO);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isDebugEnabled() {
            return this.logger.isLoggable(java.util.logging.Level.FINE);
        }

        @Override // org.apache.commons.logging.Log
        public boolean isTraceEnabled() {
            return this.logger.isLoggable(java.util.logging.Level.FINEST);
        }

        @Override // org.apache.commons.logging.Log
        public void fatal(Object message) {
            error(message);
        }

        @Override // org.apache.commons.logging.Log
        public void fatal(Object message, Throwable exception) {
            error(message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void error(Object message) {
            log(java.util.logging.Level.SEVERE, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void error(Object message, Throwable exception) {
            log(java.util.logging.Level.SEVERE, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void warn(Object message) {
            log(java.util.logging.Level.WARNING, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void warn(Object message, Throwable exception) {
            log(java.util.logging.Level.WARNING, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void info(Object message) {
            log(java.util.logging.Level.INFO, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void info(Object message, Throwable exception) {
            log(java.util.logging.Level.INFO, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void debug(Object message) {
            log(java.util.logging.Level.FINE, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void debug(Object message, Throwable exception) {
            log(java.util.logging.Level.FINE, message, exception);
        }

        @Override // org.apache.commons.logging.Log
        public void trace(Object message) {
            log(java.util.logging.Level.FINEST, message, null);
        }

        @Override // org.apache.commons.logging.Log
        public void trace(Object message, Throwable exception) {
            log(java.util.logging.Level.FINEST, message, exception);
        }

        private void log(java.util.logging.Level level, Object message, Throwable exception) {
            LogRecord rec;
            if (this.logger.isLoggable(level)) {
                if (message instanceof LogRecord) {
                    LogRecord logRecord = (LogRecord) message;
                    rec = logRecord;
                } else {
                    rec = new LocationResolvingLogRecord(level, String.valueOf(message));
                    rec.setLoggerName(this.name);
                    rec.setResourceBundleName(this.logger.getResourceBundleName());
                    rec.setResourceBundle(this.logger.getResourceBundle());
                    rec.setThrown(exception);
                }
                this.logger.log(rec);
            }
        }

        protected Object readResolve() {
            return new JavaUtilLog(this.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-jcl-6.1.3.jar:org/apache/commons/logging/LogAdapter$LocationResolvingLogRecord.class */
    public static class LocationResolvingLogRecord extends LogRecord {
        private static final String FQCN = JavaUtilLog.class.getName();
        private volatile boolean resolved;

        public LocationResolvingLogRecord(java.util.logging.Level level, String msg) {
            super(level, msg);
        }

        @Override // java.util.logging.LogRecord
        public String getSourceClassName() {
            if (!this.resolved) {
                resolve();
            }
            return super.getSourceClassName();
        }

        @Override // java.util.logging.LogRecord
        public void setSourceClassName(String sourceClassName) {
            super.setSourceClassName(sourceClassName);
            this.resolved = true;
        }

        @Override // java.util.logging.LogRecord
        public String getSourceMethodName() {
            if (!this.resolved) {
                resolve();
            }
            return super.getSourceMethodName();
        }

        @Override // java.util.logging.LogRecord
        public void setSourceMethodName(String sourceMethodName) {
            super.setSourceMethodName(sourceMethodName);
            this.resolved = true;
        }

        private void resolve() {
            StackTraceElement[] stack = new Throwable().getStackTrace();
            String sourceClassName = null;
            String sourceMethodName = null;
            boolean found = false;
            int length = stack.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                StackTraceElement element = stack[i];
                String className = element.getClassName();
                if (FQCN.equals(className)) {
                    found = true;
                } else if (found) {
                    sourceClassName = className;
                    sourceMethodName = element.getMethodName();
                    break;
                }
                i++;
            }
            setSourceClassName(sourceClassName);
            setSourceMethodName(sourceMethodName);
        }

        protected Object writeReplace() {
            LogRecord serialized = new LogRecord(getLevel(), getMessage());
            serialized.setLoggerName(getLoggerName());
            serialized.setResourceBundle(getResourceBundle());
            serialized.setResourceBundleName(getResourceBundleName());
            serialized.setSourceClassName(getSourceClassName());
            serialized.setSourceMethodName(getSourceMethodName());
            serialized.setSequenceNumber(getSequenceNumber());
            serialized.setParameters(getParameters());
            serialized.setLongThreadID(getLongThreadID());
            serialized.setInstant(getInstant());
            serialized.setThrown(getThrown());
            return serialized;
        }
    }
}
