package org.jooq.tools;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.jooq.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/JooqLogger.class */
public final class JooqLogger implements Log {
    private static volatile Log.Level globalThreshold = Log.Level.TRACE;
    private Logger slf4j;
    private java.util.logging.Logger util;
    private volatile Log.Level threshold;
    private final String name;
    private final String propertyName;
    private final AtomicInteger limitMessages;

    @Deprecated
    public JooqLogger(int limitMessages) {
        this(UUID.randomUUID().toString(), limitMessages);
    }

    JooqLogger(String name, int limitMessages) {
        this.threshold = Log.Level.TRACE;
        this.name = name;
        this.propertyName = "org.jooq.log." + name;
        this.limitMessages = limitMessages >= 0 ? new AtomicInteger(limitMessages) : null;
    }

    public static JooqLogger getLogger(Class<?> clazz) {
        return getLogger(clazz, null, -1);
    }

    public static JooqLogger getLogger(String name) {
        return getLogger(null, name, -1);
    }

    public static JooqLogger getLogger(Class<?> clazz, int limitMessages) {
        return getLogger(clazz, null, limitMessages);
    }

    public static JooqLogger getLogger(String name, int limitMessages) {
        return getLogger(null, name, limitMessages);
    }

    public static JooqLogger getLogger(Class<?> clazz, String nameSuffix, int limitMessages) {
        String str;
        if (clazz != null && nameSuffix != null) {
            str = clazz.getName() + "." + nameSuffix;
        } else if (clazz != null) {
            str = clazz.getName();
        } else {
            str = nameSuffix;
        }
        String name = str;
        JooqLogger result = new JooqLogger(name, limitMessages);
        try {
            result.slf4j = LoggerFactory.getLogger(name);
        } catch (Throwable th) {
            result.util = java.util.logging.Logger.getLogger(name);
        }
        try {
            result.isTraceEnabled();
        } catch (Throwable th2) {
            result.threshold = Log.Level.DEBUG;
        }
        try {
            result.isDebugEnabled();
        } catch (Throwable th3) {
            result.threshold = Log.Level.INFO;
        }
        try {
            result.isInfoEnabled();
        } catch (Throwable th4) {
            result.threshold = Log.Level.WARN;
        }
        return result;
    }

    private final Log.Level threshold() {
        Log.Level global = globalThreshold;
        Log.Level local = this.threshold;
        String p = System.getProperty(this.propertyName);
        if (p != null) {
            try {
                Log.Level valueOf = Log.Level.valueOf(p.toUpperCase());
                local = valueOf;
                this.threshold = valueOf;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unsupported log level for org.jooq.log." + this.name + ": " + p + ". Supported levels include: " + String.valueOf(Arrays.asList(Log.Level.values())), e);
            }
        }
        return local.supports(global) ? global : local;
    }

    private final void decrementLimitAndDo(Runnable runnable) {
        try {
            runnable.run();
        } finally {
            if (this.limitMessages != null) {
                this.limitMessages.getAndUpdate(i -> {
                    return Math.max(i - 1, 0);
                });
            }
        }
    }

    @Override // org.jooq.Log
    public boolean isTraceEnabled() {
        if (!threshold().supports(Log.Level.TRACE)) {
            return false;
        }
        if (this.limitMessages != null && this.limitMessages.get() == 0) {
            return false;
        }
        if (this.slf4j != null) {
            return this.slf4j.isTraceEnabled();
        }
        return this.util.isLoggable(java.util.logging.Level.FINER);
    }

    @Override // org.jooq.Log
    public void trace(Object message) {
        trace(message, (Object) null);
    }

    @Override // org.jooq.Log
    public void trace(Object message, Object details) {
        decrementLimitAndDo(() -> {
            if (!isTraceEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.trace(getMessage(message, details));
            } else {
                this.util.finer(getMessage(message, details));
            }
        });
    }

    @Override // org.jooq.Log
    public void trace(Object message, Throwable throwable) {
        trace(message, null, throwable);
    }

    @Override // org.jooq.Log
    public void trace(Object message, Object details, Throwable throwable) {
        decrementLimitAndDo(() -> {
            if (!isTraceEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.trace(getMessage(message, details), throwable);
            } else {
                this.util.log(java.util.logging.Level.FINER, getMessage(message, details), throwable);
            }
        });
    }

    @Override // org.jooq.Log
    public boolean isDebugEnabled() {
        if (!threshold().supports(Log.Level.DEBUG)) {
            return false;
        }
        if (this.limitMessages != null && this.limitMessages.get() == 0) {
            return false;
        }
        if (this.slf4j != null) {
            return this.slf4j.isDebugEnabled();
        }
        return this.util.isLoggable(java.util.logging.Level.FINE);
    }

    @Override // org.jooq.Log
    public void debug(Object message) {
        debug(message, (Object) null);
    }

    @Override // org.jooq.Log
    public void debug(Object message, Object details) {
        decrementLimitAndDo(() -> {
            if (!isDebugEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.debug(getMessage(message, details));
            } else {
                this.util.fine(getMessage(message, details));
            }
        });
    }

    @Override // org.jooq.Log
    public void debug(Object message, Throwable throwable) {
        debug(message, null, throwable);
    }

    @Override // org.jooq.Log
    public void debug(Object message, Object details, Throwable throwable) {
        decrementLimitAndDo(() -> {
            if (!isDebugEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.debug(getMessage(message, details), throwable);
            } else {
                this.util.log(java.util.logging.Level.FINE, getMessage(message, details), throwable);
            }
        });
    }

    @Override // org.jooq.Log
    public boolean isInfoEnabled() {
        if (!threshold().supports(Log.Level.INFO)) {
            return false;
        }
        if (this.limitMessages != null && this.limitMessages.get() == 0) {
            return false;
        }
        if (this.slf4j != null) {
            return this.slf4j.isInfoEnabled();
        }
        return this.util.isLoggable(java.util.logging.Level.INFO);
    }

    @Override // org.jooq.Log
    public void info(Object message) {
        info(message, (Object) null);
    }

    @Override // org.jooq.Log
    public void info(Object message, Object details) {
        decrementLimitAndDo(() -> {
            if (!isInfoEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.info(getMessage(message, details));
            } else {
                this.util.info(getMessage(message, details));
            }
        });
    }

    @Override // org.jooq.Log
    public void info(Object message, Throwable throwable) {
        info(message, null, throwable);
    }

    @Override // org.jooq.Log
    public void info(Object message, Object details, Throwable throwable) {
        decrementLimitAndDo(() -> {
            if (!isInfoEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.info(getMessage(message, details), throwable);
            } else {
                this.util.log(java.util.logging.Level.INFO, getMessage(message, details), throwable);
            }
        });
    }

    @Override // org.jooq.Log
    public boolean isWarnEnabled() {
        if (!threshold().supports(Log.Level.WARN)) {
            return false;
        }
        if (this.limitMessages != null && this.limitMessages.get() == 0) {
            return false;
        }
        if (this.slf4j != null) {
            return this.slf4j.isWarnEnabled();
        }
        return this.util.isLoggable(java.util.logging.Level.WARNING);
    }

    @Override // org.jooq.Log
    public void warn(Object message) {
        warn(message, (Object) null);
    }

    @Override // org.jooq.Log
    public void warn(Object message, Object details) {
        decrementLimitAndDo(() -> {
            if (!isWarnEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.warn(getMessage(message, details));
            } else {
                this.util.warning(getMessage(message, details));
            }
        });
    }

    @Override // org.jooq.Log
    public void warn(Object message, Throwable throwable) {
        warn(message, null, throwable);
    }

    @Override // org.jooq.Log
    public void warn(Object message, Object details, Throwable throwable) {
        decrementLimitAndDo(() -> {
            if (!isWarnEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.warn(getMessage(message, details), throwable);
            } else {
                this.util.log(java.util.logging.Level.WARNING, getMessage(message, details), throwable);
            }
        });
    }

    @Override // org.jooq.Log
    public boolean isErrorEnabled() {
        if (!threshold().supports(Log.Level.ERROR)) {
            return false;
        }
        if (this.limitMessages != null && this.limitMessages.get() == 0) {
            return false;
        }
        if (this.slf4j != null) {
            return this.slf4j.isErrorEnabled();
        }
        return this.util.isLoggable(java.util.logging.Level.SEVERE);
    }

    @Override // org.jooq.Log
    public void error(Object message) {
        error(message, (Object) null);
    }

    @Override // org.jooq.Log
    public void error(Object message, Object details) {
        decrementLimitAndDo(() -> {
            if (!isErrorEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.error(getMessage(message, details));
            } else {
                this.util.severe(getMessage(message, details));
            }
        });
    }

    @Override // org.jooq.Log
    public void error(Object message, Throwable throwable) {
        error(message, null, throwable);
    }

    @Override // org.jooq.Log
    public void error(Object message, Object details, Throwable throwable) {
        decrementLimitAndDo(() -> {
            if (!isErrorEnabled()) {
                return;
            }
            if (this.slf4j != null) {
                this.slf4j.error(getMessage(message, details), throwable);
            } else {
                this.util.log(java.util.logging.Level.SEVERE, getMessage(message, details), throwable);
            }
        });
    }

    @Override // org.jooq.Log
    public void log(Log.Level level, Object message) {
        log(level, message, (Object) null);
    }

    @Override // org.jooq.Log
    public void log(Log.Level level, Object message, Object details) {
        switch (level) {
            case TRACE:
                trace(message, details);
                return;
            case DEBUG:
                debug(message, details);
                return;
            case INFO:
                info(message, details);
                return;
            case WARN:
                warn(message, details);
                return;
            case ERROR:
            case FATAL:
                error(message, details);
                return;
            default:
                return;
        }
    }

    @Override // org.jooq.Log
    public void log(Log.Level level, Object message, Throwable throwable) {
        log(level, message, null, throwable);
    }

    @Override // org.jooq.Log
    public void log(Log.Level level, Object message, Object details, Throwable throwable) {
        switch (level) {
            case TRACE:
                trace(message, details, throwable);
                return;
            case DEBUG:
                debug(message, details, throwable);
                return;
            case INFO:
                info(message, details, throwable);
                return;
            case WARN:
                warn(message, details, throwable);
                return;
            case ERROR:
            case FATAL:
                error(message, details, throwable);
                return;
            default:
                return;
        }
    }

    private String getMessage(Object message, Object details) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.rightPad(String.valueOf(message), 25));
        if (details != null) {
            sb.append(": ");
            sb.append(details);
        }
        return sb.toString();
    }

    public static void globalThreshold(Level level) {
        switch (level) {
            case TRACE:
                globalThreshold(Log.Level.TRACE);
                return;
            case DEBUG:
                globalThreshold(Log.Level.DEBUG);
                return;
            case INFO:
                globalThreshold(Log.Level.INFO);
                return;
            case WARN:
                globalThreshold(Log.Level.WARN);
                return;
            case ERROR:
                globalThreshold(Log.Level.ERROR);
                return;
            case FATAL:
                globalThreshold(Log.Level.FATAL);
                return;
            default:
                return;
        }
    }

    public static void globalThreshold(Log.Level level) {
        globalThreshold = level;
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/JooqLogger$Level.class */
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

    public static void initSimpleFormatter() {
        if (System.getProperty("java.util.logging.SimpleFormatter.format") == null) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
        }
    }
}
