package org.springframework.core.serializer.support;

import org.springframework.core.NestedRuntimeException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/serializer/support/SerializationFailedException.class */
public class SerializationFailedException extends NestedRuntimeException {
    public SerializationFailedException(String message) {
        super(message);
    }

    public SerializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
