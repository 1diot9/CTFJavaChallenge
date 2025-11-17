package io.micrometer.common.util.internal.logging;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/internal/logging/WarnThenDebugLogger.class */
public class WarnThenDebugLogger {
    private final InternalLogger logger;
    private final AtomicBoolean warnLogged = new AtomicBoolean();

    public WarnThenDebugLogger(Class<?> clazz) {
        this.logger = InternalLoggerFactory.getInstance(clazz);
    }

    public void log(String message, Throwable ex) {
        if (this.warnLogged.compareAndSet(false, true)) {
            log(InternalLogLevel.WARN, getWarnMessage(message), ex);
        } else {
            log(InternalLogLevel.DEBUG, message, ex);
        }
    }

    private String getWarnMessage(String message) {
        return message + " Note that subsequent logs will be logged at debug level.";
    }

    private void log(InternalLogLevel level, String finalMessage, Throwable ex) {
        if (ex != null) {
            this.logger.log(level, finalMessage, ex);
        } else {
            this.logger.log(level, finalMessage);
        }
    }

    public void log(String message) {
        log(message, (Throwable) null);
    }

    public void log(Supplier<String> messageSupplier, Throwable ex) {
        if (this.warnLogged.compareAndSet(false, true)) {
            log(InternalLogLevel.WARN, getWarnMessage(messageSupplier.get()), ex);
        } else if (this.logger.isDebugEnabled()) {
            log(InternalLogLevel.DEBUG, messageSupplier.get(), ex);
        }
    }

    public void log(Supplier<String> messageSupplier) {
        log(messageSupplier, (Throwable) null);
    }
}
