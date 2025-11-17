package org.springframework.context.expression;

import java.util.Map;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/expression/CachedExpressionEvaluator.class */
public abstract class CachedExpressionEvaluator {
    private final SpelExpressionParser parser;
    private final ParameterNameDiscoverer parameterNameDiscoverer;

    protected CachedExpressionEvaluator(SpelExpressionParser parser) {
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        Assert.notNull(parser, "SpelExpressionParser must not be null");
        this.parser = parser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CachedExpressionEvaluator() {
        this(new SpelExpressionParser());
    }

    protected SpelExpressionParser getParser() {
        return this.parser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Expression getExpression(Map<ExpressionKey, Expression> cache, AnnotatedElementKey elementKey, String expression) {
        ExpressionKey expressionKey = createKey(elementKey, expression);
        Expression expr = cache.get(expressionKey);
        if (expr == null) {
            expr = parseExpression(expression);
            cache.put(expressionKey, expr);
        }
        return expr;
    }

    protected Expression parseExpression(String expression) {
        return getParser().parseExpression(expression);
    }

    private ExpressionKey createKey(AnnotatedElementKey elementKey, String expression) {
        return new ExpressionKey(elementKey, expression);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/expression/CachedExpressionEvaluator$ExpressionKey.class */
    public static class ExpressionKey implements Comparable<ExpressionKey> {
        private final AnnotatedElementKey element;
        private final String expression;

        protected ExpressionKey(AnnotatedElementKey element, String expression) {
            Assert.notNull(element, "AnnotatedElementKey must not be null");
            Assert.notNull(expression, "Expression must not be null");
            this.element = element;
            this.expression = expression;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ExpressionKey) {
                    ExpressionKey that = (ExpressionKey) other;
                    if (!this.element.equals(that.element) || !ObjectUtils.nullSafeEquals(this.expression, that.expression)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.element.hashCode() * 29) + this.expression.hashCode();
        }

        public String toString() {
            return this.element + " with expression \"" + this.expression + "\"";
        }

        @Override // java.lang.Comparable
        public int compareTo(ExpressionKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0) {
                result = this.expression.compareTo(other.expression);
            }
            return result;
        }
    }
}
