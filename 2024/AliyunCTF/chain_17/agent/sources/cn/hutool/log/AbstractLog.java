package cn.hutool.log;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.level.Level;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/AbstractLog.class */
public abstract class AbstractLog implements Log, Serializable {
    private static final long serialVersionUID = -3211115409504005616L;
    private static final String FQCN = AbstractLog.class.getName();

    @Override // cn.hutool.log.Log
    public boolean isEnabled(Level level) {
        switch (level) {
            case TRACE:
                return isTraceEnabled();
            case DEBUG:
                return isDebugEnabled();
            case INFO:
                return isInfoEnabled();
            case WARN:
                return isWarnEnabled();
            case ERROR:
                return isErrorEnabled();
            default:
                throw new Error(StrUtil.format("Can not identify level: {}", level));
        }
    }

    @Override // cn.hutool.log.level.TraceLog
    public void trace(Throwable t) {
        trace(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
    }

    @Override // cn.hutool.log.level.TraceLog
    public void trace(String format, Object... arguments) {
        trace(null, format, arguments);
    }

    @Override // cn.hutool.log.level.TraceLog
    public void trace(Throwable t, String format, Object... arguments) {
        trace(FQCN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(Throwable t) {
        debug(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(String format, Object... arguments) {
        if (null != arguments && 1 == arguments.length && (arguments[0] instanceof Throwable)) {
            debug((Throwable) arguments[0], format, new Object[0]);
        } else {
            debug(null, format, arguments);
        }
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(Throwable t, String format, Object... arguments) {
        debug(FQCN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(Throwable t) {
        info(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(String format, Object... arguments) {
        if (null != arguments && 1 == arguments.length && (arguments[0] instanceof Throwable)) {
            info((Throwable) arguments[0], format, new Object[0]);
        } else {
            info(null, format, arguments);
        }
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(Throwable t, String format, Object... arguments) {
        info(FQCN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(Throwable t) {
        warn(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(String format, Object... arguments) {
        if (null != arguments && 1 == arguments.length && (arguments[0] instanceof Throwable)) {
            warn((Throwable) arguments[0], format, new Object[0]);
        } else {
            warn(null, format, arguments);
        }
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(Throwable t, String format, Object... arguments) {
        warn(FQCN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(Throwable t) {
        error(t, ExceptionUtil.getSimpleMessage(t), new Object[0]);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(String format, Object... arguments) {
        if (null != arguments && 1 == arguments.length && (arguments[0] instanceof Throwable)) {
            error((Throwable) arguments[0], format, new Object[0]);
        } else {
            error(null, format, arguments);
        }
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(Throwable t, String format, Object... arguments) {
        error(FQCN, t, format, arguments);
    }

    @Override // cn.hutool.log.Log
    public void log(Level level, String format, Object... arguments) {
        if (null != arguments && 1 == arguments.length && (arguments[0] instanceof Throwable)) {
            log(level, (Throwable) arguments[0], format, new Object[0]);
        } else {
            log(level, null, format, arguments);
        }
    }

    @Override // cn.hutool.log.Log
    public void log(Level level, Throwable t, String format, Object... arguments) {
        log(FQCN, level, t, format, arguments);
    }
}
