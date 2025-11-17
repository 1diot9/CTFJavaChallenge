package cn.hutool.log.dialect.logtube;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import cn.hutool.log.level.Level;
import io.github.logtube.Logtube;
import io.github.logtube.core.IEventLogger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/logtube/LogTubeLog.class */
public class LogTubeLog extends AbstractLog {
    private final IEventLogger logger;

    public LogTubeLog(IEventLogger logger) {
        this.logger = logger;
    }

    public LogTubeLog(Class<?> clazz) {
        this(null == clazz ? "null" : clazz.getName());
    }

    public LogTubeLog(String name) {
        this(Logtube.getLogger(name));
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
        return this.logger.isWarnEnabled();
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.WARN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.ERROR, t, format, arguments);
    }

    @Override // cn.hutool.log.Log
    public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        String topic = level.name().toLowerCase();
        this.logger.topic(topic).xStackTraceElement(ExceptionUtil.getStackElement(6), (String) null).message(StrUtil.format(format, arguments)).xException(t).commit();
    }
}
