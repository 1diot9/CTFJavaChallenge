package org.springframework.jmx;

import org.springframework.core.NestedRuntimeException;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/JmxException.class */
public class JmxException extends NestedRuntimeException {
    public JmxException(String msg) {
        super(msg);
    }

    public JmxException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
