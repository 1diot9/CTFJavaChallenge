package org.springframework.scheduling;

import org.springframework.core.NestedRuntimeException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/SchedulingException.class */
public class SchedulingException extends NestedRuntimeException {
    public SchedulingException(String msg) {
        super(msg);
    }

    public SchedulingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
