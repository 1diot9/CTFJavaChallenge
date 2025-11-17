package cn.hutool.log.dialect.log4j;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import cn.hutool.log.level.Level;
import org.apache.log4j.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/log4j/Log4jLog.class */
public class Log4jLog extends AbstractLog {
    private static final long serialVersionUID = -6843151523380063975L;
    private final Logger logger;

    public Log4jLog(Logger logger) {
        this.logger = logger;
    }

    public Log4jLog(Class<?> clazz) {
        this(null == clazz ? "null" : clazz.getName());
    }

    public Log4jLog(String name) {
        this(Logger.getLogger(name));
    }

    @Override // cn.hutool.log.Log
    public String getName() {
        return this.logger.getName();
    }

    @Override // cn.hutool.log.level.TraceLog
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override // cn.hutool.log.level.TraceLog
    public void trace(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.TRACE, t, format, arguments);
    }

    @Override // cn.hutool.log.level.DebugLog
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.DEBUG, t, format, arguments);
    }

    @Override // cn.hutool.log.level.InfoLog
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.INFO, t, format, arguments);
    }

    @Override // cn.hutool.log.level.WarnLog
    public boolean isWarnEnabled() {
        return this.logger.isEnabledFor(org.apache.log4j.Level.WARN);
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.WARN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public boolean isErrorEnabled() {
        return this.logger.isEnabledFor(org.apache.log4j.Level.ERROR);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.ERROR, t, format, arguments);
    }

    @Override // cn.hutool.log.Log
    public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        org.apache.log4j.Level log4jLevel;
        switch (level) {
            case TRACE:
                log4jLevel = org.apache.log4j.Level.TRACE;
                break;
            case DEBUG:
                log4jLevel = org.apache.log4j.Level.DEBUG;
                break;
            case INFO:
                log4jLevel = org.apache.log4j.Level.INFO;
                break;
            case WARN:
                log4jLevel = org.apache.log4j.Level.WARN;
                break;
            case ERROR:
                log4jLevel = org.apache.log4j.Level.ERROR;
                break;
            default:
                throw new Error(StrUtil.format("Can not identify level: {}", level));
        }
        if (this.logger.isEnabledFor(log4jLevel)) {
            this.logger.log(fqcn, log4jLevel, StrUtil.format(format, arguments), t);
        }
    }
}
