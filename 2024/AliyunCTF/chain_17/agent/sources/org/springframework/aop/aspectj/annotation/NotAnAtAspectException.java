package org.springframework.aop.aspectj.annotation;

import org.springframework.aop.framework.AopConfigException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/NotAnAtAspectException.class */
public class NotAnAtAspectException extends AopConfigException {
    private final Class<?> nonAspectClass;

    public NotAnAtAspectException(Class<?> nonAspectClass) {
        super(nonAspectClass.getName() + " is not an @AspectJ aspect");
        this.nonAspectClass = nonAspectClass;
    }

    public Class<?> getNonAspectClass() {
        return this.nonAspectClass;
    }
}
