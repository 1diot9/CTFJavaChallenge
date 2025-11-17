package org.springframework.jmx.access;

import javax.management.JMRuntimeException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/access/InvalidInvocationException.class */
public class InvalidInvocationException extends JMRuntimeException {
    public InvalidInvocationException(String msg) {
        super(msg);
    }
}
