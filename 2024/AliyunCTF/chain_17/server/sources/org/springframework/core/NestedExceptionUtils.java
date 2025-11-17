package org.springframework.core;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/NestedExceptionUtils.class */
public abstract class NestedExceptionUtils {
    @Nullable
    @Deprecated(since = "6.0")
    public static String buildMessage(@Nullable String message, @Nullable Throwable cause) {
        if (cause == null) {
            return message;
        }
        StringBuilder sb = new StringBuilder(64);
        if (message != null) {
            sb.append(message).append("; ");
        }
        sb.append("nested exception is ").append(cause);
        return sb.toString();
    }

    @Nullable
    public static Throwable getRootCause(@Nullable Throwable original) {
        if (original == null) {
            return null;
        }
        Throwable rootCause = null;
        Throwable cause = original.getCause();
        while (true) {
            Throwable cause2 = cause;
            if (cause2 == null || cause2 == rootCause) {
                break;
            }
            rootCause = cause2;
            cause = cause2.getCause();
        }
        return rootCause;
    }

    public static Throwable getMostSpecificCause(Throwable original) {
        Throwable rootCause = getRootCause(original);
        return rootCause != null ? rootCause : original;
    }
}
