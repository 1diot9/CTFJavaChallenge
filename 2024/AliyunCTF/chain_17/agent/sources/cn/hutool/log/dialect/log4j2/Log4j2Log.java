package cn.hutool.log.dialect.log4j2;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.AbstractLogger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/log4j2/Log4j2Log.class */
public class Log4j2Log extends AbstractLog {
    private static final long serialVersionUID = -6843151523380063975L;
    private final transient Logger logger;

    public Log4j2Log(Logger logger) {
        this.logger = logger;
    }

    public Log4j2Log(Class<?> clazz) {
        this(LogManager.getLogger(clazz));
    }

    public Log4j2Log(String name) {
        this(LogManager.getLogger(name));
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
        logIfEnabled(fqcn, Level.TRACE, t, format, arguments);
    }

    @Override // cn.hutool.log.level.DebugLog
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.DEBUG, t, format, arguments);
    }

    @Override // cn.hutool.log.level.InfoLog
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.INFO, t, format, arguments);
    }

    @Override // cn.hutool.log.level.WarnLog
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.WARN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.ERROR, t, format, arguments);
    }

    @Override // cn.hutool.log.Log
    public void log(String fqcn, cn.hutool.log.level.Level level, Throwable t, String format, Object... arguments) {
        Level log4j2Level;
        switch (level) {
            case TRACE:
                log4j2Level = Level.TRACE;
                break;
            case DEBUG:
                log4j2Level = Level.DEBUG;
                break;
            case INFO:
                log4j2Level = Level.INFO;
                break;
            case WARN:
                log4j2Level = Level.WARN;
                break;
            case ERROR:
                log4j2Level = Level.ERROR;
                break;
            default:
                throw new Error(StrUtil.format("Can not identify level: {}", level));
        }
        logIfEnabled(fqcn, log4j2Level, t, format, arguments);
    }

    private void logIfEnabled(String fqcn, Level level, Throwable t, String msgTemplate, Object... arguments) {
        if (this.logger.isEnabled(level)) {
            if (this.logger instanceof AbstractLogger) {
                ((AbstractLogger) this.logger).logIfEnabled(fqcn, level, (Marker) null, StrUtil.format(msgTemplate, arguments), t);
            } else {
                this.logger.log(level, StrUtil.format(msgTemplate, arguments), t);
            }
        }
    }
}
