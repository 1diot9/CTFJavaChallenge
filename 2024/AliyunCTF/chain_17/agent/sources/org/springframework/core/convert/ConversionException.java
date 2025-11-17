package org.springframework.core.convert;

import org.springframework.core.NestedRuntimeException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/ConversionException.class */
public abstract class ConversionException extends NestedRuntimeException {
    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
