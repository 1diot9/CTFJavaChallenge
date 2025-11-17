package org.springframework.aop;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/AfterReturningAdvice.class */
public interface AfterReturningAdvice extends AfterAdvice {
    void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable;
}
