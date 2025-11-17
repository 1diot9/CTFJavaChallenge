package org.springframework.aop;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/TrueClassFilter.class */
final class TrueClassFilter implements ClassFilter, Serializable {
    public static final TrueClassFilter INSTANCE = new TrueClassFilter();

    private TrueClassFilter() {
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        return true;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public String toString() {
        return "ClassFilter.TRUE";
    }
}
