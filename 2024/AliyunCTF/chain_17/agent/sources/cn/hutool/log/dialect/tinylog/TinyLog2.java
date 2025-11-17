package cn.hutool.log.dialect.tinylog;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.AbstractLog;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/log/dialect/tinylog/TinyLog2.class */
public class TinyLog2 extends AbstractLog {
    private static final long serialVersionUID = 1;
    private static final int DEPTH = 5;
    private final int level;
    private final String name;
    private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();
    private static final MessageFormatter formatter = new AdvancedMessageFormatter(Configuration.getLocale(), Configuration.isEscapingEnabled());

    public TinyLog2(Class<?> clazz) {
        this(null == clazz ? "null" : clazz.getName());
    }

    public TinyLog2(String name) {
        this.name = name;
        this.level = provider.getMinimumLevel().ordinal();
    }

    @Override // cn.hutool.log.Log
    public String getName() {
        return this.name;
    }

    @Override // cn.hutool.log.level.TraceLog
    public boolean isTraceEnabled() {
        return this.level <= Level.TRACE.ordinal();
    }

    @Override // cn.hutool.log.level.TraceLog
    public void trace(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.TRACE, t, format, arguments);
    }

    @Override // cn.hutool.log.level.DebugLog
    public boolean isDebugEnabled() {
        return this.level <= Level.DEBUG.ordinal();
    }

    @Override // cn.hutool.log.level.DebugLog
    public void debug(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.DEBUG, t, format, arguments);
    }

    @Override // cn.hutool.log.level.InfoLog
    public boolean isInfoEnabled() {
        return this.level <= Level.INFO.ordinal();
    }

    @Override // cn.hutool.log.level.InfoLog
    public void info(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.INFO, t, format, arguments);
    }

    @Override // cn.hutool.log.level.WarnLog
    public boolean isWarnEnabled() {
        return this.level <= Level.WARN.ordinal();
    }

    @Override // cn.hutool.log.level.WarnLog
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.WARN, t, format, arguments);
    }

    @Override // cn.hutool.log.level.ErrorLog
    public boolean isErrorEnabled() {
        return this.level <= Level.ERROR.ordinal();
    }

    @Override // cn.hutool.log.level.ErrorLog
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, Level.ERROR, t, format, arguments);
    }

    @Override // cn.hutool.log.Log
    public void log(String fqcn, cn.hutool.log.level.Level level, Throwable t, String format, Object... arguments) {
        logIfEnabled(fqcn, toTinyLevel(level), t, format, arguments);
    }

    @Override // cn.hutool.log.AbstractLog, cn.hutool.log.Log
    public boolean isEnabled(cn.hutool.log.level.Level level) {
        return this.level <= toTinyLevel(level).ordinal();
    }

    private void logIfEnabled(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        if (null == t) {
            t = getLastArgumentIfThrowable(arguments);
        }
        provider.log(5, (String) null, level, t, formatter, StrUtil.toString(format), arguments);
    }

    private Level toTinyLevel(cn.hutool.log.level.Level level) {
        Level tinyLevel;
        switch (level) {
            case TRACE:
                tinyLevel = Level.TRACE;
                break;
            case DEBUG:
                tinyLevel = Level.DEBUG;
                break;
            case INFO:
                tinyLevel = Level.INFO;
                break;
            case WARN:
                tinyLevel = Level.WARN;
                break;
            case ERROR:
                tinyLevel = Level.ERROR;
                break;
            case OFF:
                tinyLevel = Level.OFF;
                break;
            default:
                throw new Error(StrUtil.format("Can not identify level: {}", level));
        }
        return tinyLevel;
    }

    private static Throwable getLastArgumentIfThrowable(Object... arguments) {
        if (ArrayUtil.isNotEmpty(arguments) && (arguments[arguments.length - 1] instanceof Throwable)) {
            return (Throwable) arguments[arguments.length - 1];
        }
        return null;
    }
}
