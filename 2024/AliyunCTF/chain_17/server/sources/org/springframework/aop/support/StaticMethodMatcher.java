package org.springframework.aop.support;

import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/StaticMethodMatcher.class */
public abstract class StaticMethodMatcher implements MethodMatcher {
    @Override // org.springframework.aop.MethodMatcher
    public final boolean isRuntime() {
        return false;
    }

    @Override // org.springframework.aop.MethodMatcher
    public final boolean matches(Method method, Class<?> targetClass, Object... args) {
        throw new UnsupportedOperationException("Illegal MethodMatcher usage");
    }
}
