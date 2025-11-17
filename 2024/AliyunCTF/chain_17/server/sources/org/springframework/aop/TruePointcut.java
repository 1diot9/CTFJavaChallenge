package org.springframework.aop;

import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/TruePointcut.class */
final class TruePointcut implements Pointcut, Serializable {
    public static final TruePointcut INSTANCE = new TruePointcut();

    private TruePointcut() {
    }

    @Override // org.springframework.aop.Pointcut
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override // org.springframework.aop.Pointcut
    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.TRUE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public String toString() {
        return "Pointcut.TRUE";
    }
}
