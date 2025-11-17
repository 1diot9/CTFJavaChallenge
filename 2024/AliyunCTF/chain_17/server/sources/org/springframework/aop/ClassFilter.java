package org.springframework.aop;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/ClassFilter.class */
public interface ClassFilter {
    public static final ClassFilter TRUE = TrueClassFilter.INSTANCE;

    boolean matches(Class<?> clazz);
}
