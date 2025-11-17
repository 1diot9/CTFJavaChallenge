package org.springframework.expression;

import java.lang.reflect.Method;
import java.util.List;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/MethodFilter.class */
public interface MethodFilter {
    List<Method> filter(List<Method> methods);
}
