package ch.qos.logback.core.util;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/PropertySetterException.class */
public class PropertySetterException extends Exception {
    private static final long serialVersionUID = -2771077768281663949L;

    public PropertySetterException(String msg) {
        super(msg);
    }

    public PropertySetterException(Throwable rootCause) {
        super(rootCause);
    }

    public PropertySetterException(String message, Throwable cause) {
        super(message, cause);
    }
}
