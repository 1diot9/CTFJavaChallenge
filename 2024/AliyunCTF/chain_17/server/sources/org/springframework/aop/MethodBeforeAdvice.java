package org.springframework.aop;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/MethodBeforeAdvice.class */
public interface MethodBeforeAdvice extends BeforeAdvice {
    void before(Method method, Object[] args, @Nullable Object target) throws Throwable;
}
