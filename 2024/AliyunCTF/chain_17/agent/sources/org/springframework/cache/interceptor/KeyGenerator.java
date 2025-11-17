package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/KeyGenerator.class */
public interface KeyGenerator {
    Object generate(Object target, Method method, Object... params);
}
