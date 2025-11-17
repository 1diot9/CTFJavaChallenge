package cn.hutool.log.dialect.commons;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import cn.hutool.log.level.Level;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/commons/ApacheCommonsLog.class */
public class ApacheCommonsLog extends AbstractLog {
    private static final long serialVersionUID = -6843151523380063975L;
    private final transient Log logger;
    private final String name;

    public ApacheCommonsLog(Log logger, String name) {
        this.logger = logger;
        this.name = name;
    }

    public ApacheCommonsLog(Class<?> clazz) {
        this(LogFactory.getLog(clazz), null == clazz ? "null" : clazz.getName());
    }

    public ApacheCommonsLog(String name) {
        this(LogFactory.getLog(name), name);
    }

    @Override // cn.hutool.log.Log
    public String getName() {
        return this.name;
    }

    @Override // cn.hutool.log.level.TraceLog
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override // cn.hutool.log.level.TraceLog
    public void trace(String fqcn, Throwable t, String format, Object... arguments) {
        if (isTraceEnabled()) {
            this.logger.trace(StrUtil.format(format, arguments), t);
        }
    }

    @Override // cn.hutool.log.level.DebugLog
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(String fqcn, Throwable t, String format, Object... arguments) {
        if (isDebugEnabled()) {
            this.logger.debug(StrUtil.format(format, arguments), t);
        }
    }

    @Override // cn.hutool.log.level.InfoLog
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(String fqcn, Throwable t, String format, Object... arguments) {
        if (isInfoEnabled()) {
            this.logger.info(StrUtil.format(format, arguments), t);
        }
    }

    @Override // cn.hutool.log.level.WarnLog
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override // cn.hutool.log.AbstractLog, cn.hutool.log.level.WarnLog
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            this.logger.warn(StrUtil.format(format, arguments));
        }
    }

    @Override // cn.hutool.log.AbstractLog, cn.hutool.log.level.WarnLog
    public void warn(Throwable t, String format, Object... arguments) {
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        if (isWarnEnabled()) {
            this.logger.warn(StrUtil.format(format, arguments), t);
        }
    }

    @Override // cn.hutool.log.level.ErrorLog
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        if (isErrorEnabled()) {
            this.logger.warn(StrUtil.format(format, arguments), t);
        }
    }

    @Override // cn.hutool.log.Log
    public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        switch (level) {
            case TRACE:
                trace(t, format, arguments);
                return;
            case DEBUG:
                debug(t, format, arguments);
                return;
            case INFO:
                info(t, format, arguments);
                return;
            case WARN:
                warn(t, format, arguments);
                return;
            case ERROR:
                error(t, format, arguments);
                return;
            default:
                throw new Error(StrUtil.format("Can not identify level: {}", level));
        }
    }
}
