package org.slf4j;

import org.slf4j.event.Level;
import org.slf4j.helpers.CheckReturnValue;
import org.slf4j.spi.DefaultLoggingEventBuilder;
import org.slf4j.spi.LoggingEventBuilder;
import org.slf4j.spi.NOPLoggingEventBuilder;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/Logger.class */
public interface Logger {
    public static final String ROOT_LOGGER_NAME = "ROOT";

    String getName();

    boolean isTraceEnabled();

    void trace(String str);

    void trace(String str, Object obj);

    void trace(String str, Object obj, Object obj2);

    void trace(String str, Object... objArr);

    void trace(String str, Throwable th);

    boolean isTraceEnabled(Marker marker);

    void trace(Marker marker, String str);

    void trace(Marker marker, String str, Object obj);

    void trace(Marker marker, String str, Object obj, Object obj2);

    void trace(Marker marker, String str, Object... objArr);

    void trace(Marker marker, String str, Throwable th);

    boolean isDebugEnabled();

    void debug(String str);

    void debug(String str, Object obj);

    void debug(String str, Object obj, Object obj2);

    void debug(String str, Object... objArr);

    void debug(String str, Throwable th);

    boolean isDebugEnabled(Marker marker);

    void debug(Marker marker, String str);

    void debug(Marker marker, String str, Object obj);

    void debug(Marker marker, String str, Object obj, Object obj2);

    void debug(Marker marker, String str, Object... objArr);

    void debug(Marker marker, String str, Throwable th);

    boolean isInfoEnabled();

    void info(String str);

    void info(String str, Object obj);

    void info(String str, Object obj, Object obj2);

    void info(String str, Object... objArr);

    void info(String str, Throwable th);

    boolean isInfoEnabled(Marker marker);

    void info(Marker marker, String str);

    void info(Marker marker, String str, Object obj);

    void info(Marker marker, String str, Object obj, Object obj2);

    void info(Marker marker, String str, Object... objArr);

    void info(Marker marker, String str, Throwable th);

    boolean isWarnEnabled();

    void warn(String str);

    void warn(String str, Object obj);

    void warn(String str, Object... objArr);

    void warn(String str, Object obj, Object obj2);

    void warn(String str, Throwable th);

    boolean isWarnEnabled(Marker marker);

    void warn(Marker marker, String str);

    void warn(Marker marker, String str, Object obj);

    void warn(Marker marker, String str, Object obj, Object obj2);

    void warn(Marker marker, String str, Object... objArr);

    void warn(Marker marker, String str, Throwable th);

    boolean isErrorEnabled();

    void error(String str);

    void error(String str, Object obj);

    void error(String str, Object obj, Object obj2);

    void error(String str, Object... objArr);

    void error(String str, Throwable th);

    boolean isErrorEnabled(Marker marker);

    void error(Marker marker, String str);

    void error(Marker marker, String str, Object obj);

    void error(Marker marker, String str, Object obj, Object obj2);

    void error(Marker marker, String str, Object... objArr);

    void error(Marker marker, String str, Throwable th);

    default LoggingEventBuilder makeLoggingEventBuilder(Level level) {
        return new DefaultLoggingEventBuilder(this, level);
    }

    @CheckReturnValue
    default LoggingEventBuilder atLevel(Level level) {
        if (isEnabledForLevel(level)) {
            return makeLoggingEventBuilder(level);
        }
        return NOPLoggingEventBuilder.singleton();
    }

    default boolean isEnabledForLevel(Level level) {
        int levelInt = level.toInt();
        switch (levelInt) {
            case 0:
                return isTraceEnabled();
            case 10:
                return isDebugEnabled();
            case 20:
                return isInfoEnabled();
            case 30:
                return isWarnEnabled();
            case 40:
                return isErrorEnabled();
            default:
                throw new IllegalArgumentException("Level [" + level + "] not recognized.");
        }
    }

    @CheckReturnValue
    default LoggingEventBuilder atTrace() {
        if (isTraceEnabled()) {
            return makeLoggingEventBuilder(Level.TRACE);
        }
        return NOPLoggingEventBuilder.singleton();
    }

    @CheckReturnValue
    default LoggingEventBuilder atDebug() {
        if (isDebugEnabled()) {
            return makeLoggingEventBuilder(Level.DEBUG);
        }
        return NOPLoggingEventBuilder.singleton();
    }

    @CheckReturnValue
    default LoggingEventBuilder atInfo() {
        if (isInfoEnabled()) {
            return makeLoggingEventBuilder(Level.INFO);
        }
        return NOPLoggingEventBuilder.singleton();
    }

    @CheckReturnValue
    default LoggingEventBuilder atWarn() {
        if (isWarnEnabled()) {
            return makeLoggingEventBuilder(Level.WARN);
        }
        return NOPLoggingEventBuilder.singleton();
    }

    @CheckReturnValue
    default LoggingEventBuilder atError() {
        if (isErrorEnabled()) {
            return makeLoggingEventBuilder(Level.ERROR);
        }
        return NOPLoggingEventBuilder.singleton();
    }
}
