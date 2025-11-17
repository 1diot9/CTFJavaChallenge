package org.springframework.expression.spel.ast;

import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/ast/BeanReference.class */
public class BeanReference extends SpelNodeImpl {
    private static final String FACTORY_BEAN_PREFIX = "&";
    private final String beanName;

    public BeanReference(int startPos, int endPos, String beanName) {
        super(startPos, endPos, new SpelNodeImpl[0]);
        this.beanName = beanName;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        BeanResolver beanResolver = state.getEvaluationContext().getBeanResolver();
        if (beanResolver == null) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.NO_BEAN_RESOLVER_REGISTERED, this.beanName);
        }
        try {
            return new TypedValue(beanResolver.resolve(state.getEvaluationContext(), this.beanName));
        } catch (AccessException ex) {
            throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_BEAN_RESOLUTION, this.beanName, ex.getMessage());
        }
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringBuilder sb = new StringBuilder();
        if (!this.beanName.startsWith("&")) {
            sb.append('@');
        }
        if (!this.beanName.contains(".")) {
            sb.append(this.beanName);
        } else {
            sb.append('\'').append(this.beanName).append('\'');
        }
        return sb.toString();
    }
}
