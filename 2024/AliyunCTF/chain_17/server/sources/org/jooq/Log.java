package org.jooq;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Log.class */
public interface Log {
    boolean isTraceEnabled();

    void trace(Object obj);

    void trace(Object obj, Object obj2);

    void trace(Object obj, Throwable th);

    void trace(Object obj, Object obj2, Throwable th);

    boolean isDebugEnabled();

    void debug(Object obj);

    void debug(Object obj, Object obj2);

    void debug(Object obj, Throwable th);

    void debug(Object obj, Object obj2, Throwable th);

    boolean isInfoEnabled();

    void info(Object obj);

    void info(Object obj, Object obj2);

    void info(Object obj, Throwable th);

    void info(Object obj, Object obj2, Throwable th);

    boolean isWarnEnabled();

    void warn(Object obj);

    void warn(Object obj, Object obj2);

    void warn(Object obj, Throwable th);

    void warn(Object obj, Object obj2, Throwable th);

    boolean isErrorEnabled();

    void error(Object obj);

    void error(Object obj, Object obj2);

    void error(Object obj, Throwable th);

    void error(Object obj, Object obj2, Throwable th);

    void log(Level level, Object obj);

    void log(Level level, Object obj, Object obj2);

    void log(Level level, Object obj, Throwable th);

    void log(Level level, Object obj, Object obj2, Throwable th);

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Log$Level.class */
    public enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL;

        public boolean supports(Level level) {
            return ordinal() <= level.ordinal();
        }
    }
}
