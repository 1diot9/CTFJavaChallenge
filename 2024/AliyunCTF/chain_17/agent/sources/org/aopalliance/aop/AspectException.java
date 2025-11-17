package org.aopalliance.aop;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/aopalliance/aop/AspectException.class */
public class AspectException extends RuntimeException {
    public AspectException(String message) {
        super(message);
    }

    public AspectException(String message, Throwable cause) {
        super(message, cause);
    }
}
