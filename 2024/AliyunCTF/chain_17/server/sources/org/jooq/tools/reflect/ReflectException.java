package org.jooq.tools.reflect;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/reflect/ReflectException.class */
public class ReflectException extends RuntimeException {
    private static final long serialVersionUID = -6213149635297151442L;

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException() {
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }
}
