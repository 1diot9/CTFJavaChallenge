package org.apache.logging.log4j.spi;

import java.net.URI;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/LoggerContextFactory.class */
public interface LoggerContextFactory {
    LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext);

    LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext, URI configLocation, String name);

    void removeContext(LoggerContext context);

    default void shutdown(String fqcn, ClassLoader loader, boolean currentContext, boolean allContexts) {
        if (hasContext(fqcn, loader, currentContext)) {
            LoggerContext ctx = getContext(fqcn, loader, null, currentContext);
            if (ctx instanceof Terminable) {
                ((Terminable) ctx).terminate();
            }
        }
    }

    default boolean hasContext(String fqcn, ClassLoader loader, boolean currentContext) {
        return false;
    }

    default boolean isClassLoaderDependent() {
        return true;
    }
}
