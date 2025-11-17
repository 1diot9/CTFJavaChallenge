package org.springframework.boot.logging;

import org.apache.commons.logging.Log;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LogLevel.class */
public enum LogLevel {
    TRACE((v0, v1, v2) -> {
        v0.trace(v1, v2);
    }),
    DEBUG((v0, v1, v2) -> {
        v0.debug(v1, v2);
    }),
    INFO((v0, v1, v2) -> {
        v0.info(v1, v2);
    }),
    WARN((v0, v1, v2) -> {
        v0.warn(v1, v2);
    }),
    ERROR((v0, v1, v2) -> {
        v0.error(v1, v2);
    }),
    FATAL((v0, v1, v2) -> {
        v0.fatal(v1, v2);
    }),
    OFF(null);

    private final LogMethod logMethod;

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LogLevel$LogMethod.class */
    public interface LogMethod {
        void log(Log logger, Object message, Throwable cause);
    }

    LogLevel(LogMethod logMethod) {
        this.logMethod = logMethod;
    }

    public void log(Log logger, Object message) {
        log(logger, message, null);
    }

    public void log(Log logger, Object message, Throwable cause) {
        if (logger != null && this.logMethod != null) {
            this.logMethod.log(logger, message, cause);
        }
    }
}
