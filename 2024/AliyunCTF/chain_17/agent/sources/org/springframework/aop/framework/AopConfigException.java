package org.springframework.aop.framework;

import org.springframework.core.NestedRuntimeException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AopConfigException.class */
public class AopConfigException extends NestedRuntimeException {
    public AopConfigException(String msg) {
        super(msg);
    }

    public AopConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
