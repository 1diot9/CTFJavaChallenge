package org.springframework.boot.logging;

import java.util.List;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/DelegatingLoggingSystemFactory.class */
class DelegatingLoggingSystemFactory implements LoggingSystemFactory {
    private final Function<ClassLoader, List<LoggingSystemFactory>> delegates;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DelegatingLoggingSystemFactory(Function<ClassLoader, List<LoggingSystemFactory>> delegates) {
        this.delegates = delegates;
    }

    @Override // org.springframework.boot.logging.LoggingSystemFactory
    public LoggingSystem getLoggingSystem(ClassLoader classLoader) {
        List<LoggingSystemFactory> delegates = this.delegates != null ? this.delegates.apply(classLoader) : null;
        if (delegates != null) {
            for (LoggingSystemFactory delegate : delegates) {
                LoggingSystem loggingSystem = delegate.getLoggingSystem(classLoader);
                if (loggingSystem != null) {
                    return loggingSystem;
                }
            }
            return null;
        }
        return null;
    }
}
