package org.aopalliance.intercept;

import javax.annotation.Nonnull;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/aopalliance/intercept/ConstructorInterceptor.class */
public interface ConstructorInterceptor extends Interceptor {
    @Nonnull
    Object construct(ConstructorInvocation invocation) throws Throwable;
}
