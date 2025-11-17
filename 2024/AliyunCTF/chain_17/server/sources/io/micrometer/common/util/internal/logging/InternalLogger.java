package io.micrometer.common.util.internal.logging;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/internal/logging/InternalLogger.class */
public interface InternalLogger {
    String name();

    boolean isTraceEnabled();

    void trace(String str);

    void trace(String str, Object obj);

    void trace(String str, Object obj, Object obj2);

    void trace(String str, Object... objArr);

    void trace(String str, Throwable th);

    void trace(Throwable th);

    boolean isDebugEnabled();

    void debug(String str);

    void debug(String str, Object obj);

    void debug(String str, Object obj, Object obj2);

    void debug(String str, Object... objArr);

    void debug(String str, Throwable th);

    void debug(Throwable th);

    boolean isInfoEnabled();

    void info(String str);

    void info(String str, Object obj);

    void info(String str, Object obj, Object obj2);

    void info(String str, Object... objArr);

    void info(String str, Throwable th);

    void info(Throwable th);

    boolean isWarnEnabled();

    void warn(String str);

    void warn(String str, Object obj);

    void warn(String str, Object... objArr);

    void warn(String str, Object obj, Object obj2);

    void warn(String str, Throwable th);

    void warn(Throwable th);

    boolean isErrorEnabled();

    void error(String str);

    void error(String str, Object obj);

    void error(String str, Object obj, Object obj2);

    void error(String str, Object... objArr);

    void error(String str, Throwable th);

    void error(Throwable th);

    boolean isEnabled(InternalLogLevel internalLogLevel);

    void log(InternalLogLevel internalLogLevel, String str);

    void log(InternalLogLevel internalLogLevel, String str, Object obj);

    void log(InternalLogLevel internalLogLevel, String str, Object obj, Object obj2);

    void log(InternalLogLevel internalLogLevel, String str, Object... objArr);

    void log(InternalLogLevel internalLogLevel, String str, Throwable th);

    void log(InternalLogLevel internalLogLevel, Throwable th);
}
