package org.springframework.core;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NestedCheckedException.class */
public abstract class NestedCheckedException extends Exception {
    private static final long serialVersionUID = 7100714597678207546L;

    public NestedCheckedException(String msg) {
        super(msg);
    }

    public NestedCheckedException(@Nullable String msg, @Nullable Throwable cause) {
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
        if (cause instanceof NestedCheckedException) {
            NestedCheckedException exception = (NestedCheckedException) cause;
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
