package org.springframework.expression.spel.support;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/support/StandardOperatorOverloader.class */
public class StandardOperatorOverloader implements OperatorOverloader {
    static final StandardOperatorOverloader INSTANCE = new StandardOperatorOverloader();

    @Override // org.springframework.expression.OperatorOverloader
    public boolean overridesOperation(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand) throws EvaluationException {
        return false;
    }

    @Override // org.springframework.expression.OperatorOverloader
    public Object operate(Operation operation, @Nullable Object leftOperand, @Nullable Object rightOperand) throws EvaluationException {
        throw new EvaluationException("No operation overloaded by default");
    }
}
