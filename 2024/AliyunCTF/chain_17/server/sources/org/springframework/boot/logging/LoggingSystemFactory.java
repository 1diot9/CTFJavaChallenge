package org.springframework.boot.logging;

import org.springframework.core.io.support.SpringFactoriesLoader;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/LoggingSystemFactory.class */
public interface LoggingSystemFactory {
    LoggingSystem getLoggingSystem(ClassLoader classLoader);

    static LoggingSystemFactory fromSpringFactories() {
        return new DelegatingLoggingSystemFactory(classLoader -> {
            return SpringFactoriesLoader.loadFactories(LoggingSystemFactory.class, classLoader);
        });
    }
}
