package org.aopalliance.intercept;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/aopalliance/intercept/MethodInterceptor.class */
public interface MethodInterceptor extends Interceptor {
    @Nullable
    Object invoke(@Nonnull MethodInvocation invocation) throws Throwable;
}
