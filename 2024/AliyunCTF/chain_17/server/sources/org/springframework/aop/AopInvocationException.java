package org.springframework.aop;

import org.springframework.core.NestedRuntimeException;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/AopInvocationException.class */
public class AopInvocationException extends NestedRuntimeException {
    public AopInvocationException(String msg) {
        super(msg);
    }

    public AopInvocationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
