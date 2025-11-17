package org.springframework.expression;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/ConstructorExecutor.class */
public interface ConstructorExecutor {
    TypedValue execute(EvaluationContext context, Object... arguments) throws AccessException;
}
