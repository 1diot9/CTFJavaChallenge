package org.springframework.core.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/IntrospectionFailureLogger.class */
enum IntrospectionFailureLogger {
    DEBUG { // from class: org.springframework.core.annotation.IntrospectionFailureLogger.1
        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public boolean isEnabled() {
            return IntrospectionFailureLogger.getLogger().isDebugEnabled();
        }

        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public void log(String message) {
            IntrospectionFailureLogger.getLogger().debug(message);
        }
    },
    INFO { // from class: org.springframework.core.annotation.IntrospectionFailureLogger.2
        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public boolean isEnabled() {
            return IntrospectionFailureLogger.getLogger().isInfoEnabled();
        }

        @Override // org.springframework.core.annotation.IntrospectionFailureLogger
        public void log(String message) {
            IntrospectionFailureLogger.getLogger().info(message);
        }
    };


    @Nullable
    private static Log logger;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean isEnabled();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void log(String message);

    /* JADX INFO: Access modifiers changed from: package-private */
    public void log(String message, @Nullable Object source, Exception ex) {
        String on = source != null ? " on " + source : "";
        log(message + on + ": " + ex);
    }

    private static Log getLogger() {
        Log logger2 = logger;
        if (logger2 == null) {
            logger2 = LogFactory.getLog((Class<?>) MergedAnnotation.class);
            logger = logger2;
        }
        return logger2;
    }
}
