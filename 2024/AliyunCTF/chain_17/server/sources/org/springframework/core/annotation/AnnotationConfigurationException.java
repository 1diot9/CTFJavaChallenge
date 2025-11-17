package org.springframework.core.annotation;

import org.springframework.core.NestedRuntimeException;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationConfigurationException.class */
public class AnnotationConfigurationException extends NestedRuntimeException {
    public AnnotationConfigurationException(String message) {
        super(message);
    }

    public AnnotationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
