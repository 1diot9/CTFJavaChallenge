package org.springframework.expression;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/MethodExecutor.class */
public interface MethodExecutor {
    TypedValue execute(EvaluationContext context, Object target, Object... arguments) throws AccessException;
}
