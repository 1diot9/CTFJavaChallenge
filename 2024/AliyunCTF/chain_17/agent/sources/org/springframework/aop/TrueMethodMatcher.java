package org.springframework.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/TrueMethodMatcher.class */
final class TrueMethodMatcher implements MethodMatcher, Serializable {
    public static final TrueMethodMatcher INSTANCE = new TrueMethodMatcher();

    private TrueMethodMatcher() {
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean isRuntime() {
        return false;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        return true;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "MethodMatcher.TRUE";
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
