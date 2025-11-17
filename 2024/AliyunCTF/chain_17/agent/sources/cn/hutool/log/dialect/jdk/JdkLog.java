package cn.hutool.log.dialect.jdk;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/jdk/JdkLog.class */
public class JdkLog extends AbstractLog {
    private static final long serialVersionUID = -6843151523380063975L;
    private final transient Logger logger;

    public JdkLog(Logger logger) {
        this.logger = logger;
    }

    public JdkLog(Class<?> clazz) {
        this(null == clazz ? "null" : clazz.getName());
    }

    public JdkLog(String name) {
        this(Logger.getLogger(name));
    }

    @Override // cn.hutool.log.Log
    public String getName() {
        return this.logger.getName();
    }

    @Override // cn.hutool.log.level.TraceLog
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(Level.FINEST);
    }

    @Override // cn.hutool.log.level.TraceLog
    public void trace(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.FINEST, t, format, arguments);
    }

    @Override // cn.hutool.log.level.DebugLog
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.FINE, t, format, arguments);
    }

    @Override // cn.hutool.log.level.InfoLog
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.INFO, t, format, arguments);
    }

    @Override // cn.hutool.log.level.WarnLog
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(Level.WARNING);
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.WARNING, t, format, arguments);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.SEVERE, t, format, arguments);
    }

    @Override // cn.hutool.log.Log
    public void log(String fqcn, cn.hutool.log.level.Level level, Throwable t, String format, Object... arguments) {
        Level jdkLevel;
        switch (level) {
            case TRACE:
                jdkLevel = Level.FINEST;
                break;
            case DEBUG:
                jdkLevel = Level.FINE;
                break;
            case INFO:
                jdkLevel = Level.INFO;
                break;
            case WARN:
                jdkLevel = Level.WARNING;
                break;
            case ERROR:
                jdkLevel = Level.SEVERE;
                break;
            default:
                throw new Error(StrUtil.format("Can not identify level: {}", level));
        }
        logIfEnabled(fqcn, jdkLevel, t, format, arguments);
    }

    private void logIfEnabled(String callerFQCN, Level level, Throwable throwable, String format, Object[] arguments) {
        if (this.logger.isLoggable(level)) {
            LogRecord record = new LogRecord(level, StrUtil.format(format, arguments));
            record.setLoggerName(getName());
            record.setThrown(throwable);
            fillCallerData(callerFQCN, record);
            this.logger.log(record);
        }
    }

    private static void fillCallerData(String callerFQCN, LogRecord record) {
        StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
        int found = -1;
        int i = steArray.length - 2;
        while (true) {
            if (i <= -1) {
                break;
            }
            String className = steArray[i].getClassName();
            if (!callerFQCN.equals(className)) {
                i--;
            } else {
                found = i;
                break;
            }
        }
        if (found > -1) {
            StackTraceElement ste = steArray[found + 1];
            record.setSourceClassName(ste.getClassName());
            record.setSourceMethodName(ste.getMethodName());
        }
    }
}
