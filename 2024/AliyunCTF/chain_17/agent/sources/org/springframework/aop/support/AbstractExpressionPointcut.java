package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/AbstractExpressionPointcut.class */
public abstract class AbstractExpressionPointcut implements ExpressionPointcut, Serializable {

    @Nullable
    private String location;

    @Nullable
    private String expression;

    public void setLocation(@Nullable String location) {
        this.location = location;
    }

    @Nullable
    public String getLocation() {
        return this.location;
    }

    public void setExpression(@Nullable String expression) {
        this.expression = expression;
        try {
            onSetExpression(expression);
        } catch (IllegalArgumentException ex) {
            if (this.location != null) {
                throw new IllegalArgumentException("Invalid expression at location [" + this.location + "]: " + ex);
            }
            throw ex;
        }
    }

    protected void onSetExpression(@Nullable String expression) throws IllegalArgumentException {
    }

    @Override // org.springframework.aop.support.ExpressionPointcut
    @Nullable
    public String getExpression() {
        return this.expression;
    }
}
