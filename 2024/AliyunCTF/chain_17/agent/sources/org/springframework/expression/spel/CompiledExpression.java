package org.springframework.expression.spel;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/CompiledExpression.class */
public abstract class CompiledExpression {
    public abstract Object getValue(@Nullable Object target, @Nullable EvaluationContext context) throws EvaluationException;
}
