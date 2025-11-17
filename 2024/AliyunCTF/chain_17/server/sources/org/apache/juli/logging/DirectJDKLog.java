package org.apache.juli.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/logging/DirectJDKLog.class */
class DirectJDKLog implements Log {
    public final Logger logger;
    private static final String SIMPLE_FMT = "java.util.logging.SimpleFormatter";
    private static final String FORMATTER = "org.apache.juli.formatter";

    static {
        if (System.getProperty("java.util.logging.config.class") == null && System.getProperty("java.util.logging.config.file") == null) {
            try {
                Formatter fmt = (Formatter) Class.forName(System.getProperty(FORMATTER, SIMPLE_FMT)).getConstructor(new Class[0]).newInstance(new Object[0]);
                Logger root = Logger.getLogger("");
                for (Handler handler : root.getHandlers()) {
                    if (handler instanceof ConsoleHandler) {
                        handler.setFormatter(fmt);
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    DirectJDKLog(String name) {
        this.logger = Logger.getLogger(name);
    }

    @Override // org.apache.juli.logging.Log
    public final boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    @Override // org.apache.juli.logging.Log
    public final boolean isWarnEnabled() {
        return this.logger.isLoggable(Level.WARNING);
    }

    @Override // org.apache.juli.logging.Log
    public final boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }

    @Override // org.apache.juli.logging.Log
    public final boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }

    @Override // org.apache.juli.logging.Log
    public final boolean isFatalEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    @Override // org.apache.juli.logging.Log
    public final boolean isTraceEnabled() {
        return this.logger.isLoggable(Level.FINER);
    }

    @Override // org.apache.juli.logging.Log
    public final void debug(Object message) {
        log(Level.FINE, String.valueOf(message), null);
    }

    @Override // org.apache.juli.logging.Log
    public final void debug(Object message, Throwable t) {
        log(Level.FINE, String.valueOf(message), t);
    }

    @Override // org.apache.juli.logging.Log
    public final void trace(Object message) {
        log(Level.FINER, String.valueOf(message), null);
    }

    @Override // org.apache.juli.logging.Log
    public final void trace(Object message, Throwable t) {
        log(Level.FINER, String.valueOf(message), t);
    }

    @Override // org.apache.juli.logging.Log
    public final void info(Object message) {
        log(Level.INFO, String.valueOf(message), null);
    }

    @Override // org.apache.juli.logging.Log
    public final void info(Object message, Throwable t) {
        log(Level.INFO, String.valueOf(message), t);
    }

    @Override // org.apache.juli.logging.Log
    public final void warn(Object message) {
        log(Level.WARNING, String.valueOf(message), null);
    }

    @Override // org.apache.juli.logging.Log
    public final void warn(Object message, Throwable t) {
        log(Level.WARNING, String.valueOf(message), t);
    }

    @Override // org.apache.juli.logging.Log
    public final void error(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    @Override // org.apache.juli.logging.Log
    public final void error(Object message, Throwable t) {
        log(Level.SEVERE, String.valueOf(message), t);
    }

    @Override // org.apache.juli.logging.Log
    public final void fatal(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    @Override // org.apache.juli.logging.Log
    public final void fatal(Object message, Throwable t) {
        log(Level.SEVERE, String.valueOf(message), t);
    }

    private void log(Level level, String msg, Throwable ex) {
        if (this.logger.isLoggable(level)) {
            Throwable dummyException = new Throwable();
            StackTraceElement[] locations = dummyException.getStackTrace();
            String cname = "unknown";
            String method = "unknown";
            if (locations != null && locations.length > 2) {
                StackTraceElement caller = locations[2];
                cname = caller.getClassName();
                method = caller.getMethodName();
            }
            if (ex == null) {
                this.logger.logp(level, cname, method, msg);
            } else {
                this.logger.logp(level, cname, method, msg, ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Log getInstance(String name) {
        return new DirectJDKLog(name);
    }
}
