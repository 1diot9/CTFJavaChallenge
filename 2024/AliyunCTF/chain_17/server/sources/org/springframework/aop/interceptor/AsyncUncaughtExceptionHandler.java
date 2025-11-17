package org.springframework.aop.interceptor;

import java.lang.reflect.Method;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/AsyncUncaughtExceptionHandler.class */
public interface AsyncUncaughtExceptionHandler {
    void handleUncaughtException(Throwable ex, Method method, Object... params);
}
