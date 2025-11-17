package org.springframework.core;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NestedRuntimeException.class */
public abstract class NestedRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 5439915454935047936L;

    public NestedRuntimeException(String msg) {
        super(msg);
    }

    public NestedRuntimeException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

    @Nullable
    public Throwable getRootCause() {
        return NestedExceptionUtils.getRootCause(this);
    }

    public Throwable getMostSpecificCause() {
        Throwable rootCause = getRootCause();
        return rootCause != null ? rootCause : this;
    }

    public boolean contains(@Nullable Class<?> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        Throwable cause = getCause();
        if (cause == this) {
            return false;
        }
        if (cause instanceof NestedRuntimeException) {
            NestedRuntimeException exception = (NestedRuntimeException) cause;
            return exception.contains(exType);
        }
        while (cause != null) {
            if (exType.isInstance(cause)) {
                return true;
            }
            if (cause.getCause() != cause) {
                cause = cause.getCause();
            } else {
                return false;
            }
        }
        return false;
    }
}
