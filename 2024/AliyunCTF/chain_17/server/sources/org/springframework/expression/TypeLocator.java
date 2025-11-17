package org.springframework.expression;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/TypeLocator.class */
public interface TypeLocator {
    Class<?> findType(String typeName) throws EvaluationException;
}
