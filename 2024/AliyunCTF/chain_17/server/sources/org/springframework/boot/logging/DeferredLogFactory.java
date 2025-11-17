package org.springframework.boot.logging;

import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/DeferredLogFactory.class */
public interface DeferredLogFactory {
    Log getLog(Supplier<Log> destination);

    default Log getLog(Class<?> destination) {
        return getLog(() -> {
            return LogFactory.getLog((Class<?>) destination);
        });
    }

    default Log getLog(Log destination) {
        return getLog(() -> {
            return destination;
        });
    }
}
