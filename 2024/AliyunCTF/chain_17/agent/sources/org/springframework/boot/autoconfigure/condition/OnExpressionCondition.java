package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Order(2147483627)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnExpressionCondition.class */
class OnExpressionCondition extends SpringBootCondition {
    OnExpressionCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String expression = wrapIfNecessary((String) metadata.getAnnotationAttributes(ConditionalOnExpression.class.getName()).get("value"));
        ConditionMessage.Builder messageBuilder = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnExpression.class, "(" + expression + ")");
        String expression2 = context.getEnvironment().resolvePlaceholders(expression);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (beanFactory != null) {
            boolean result = evaluateExpression(beanFactory, expression2);
            return new ConditionOutcome(result, messageBuilder.resultedIn(Boolean.valueOf(result)));
        }
        return ConditionOutcome.noMatch(messageBuilder.because("no BeanFactory available."));
    }

    private boolean evaluateExpression(ConfigurableListableBeanFactory beanFactory, String expression) {
        BeanExpressionResolver resolver = beanFactory.getBeanExpressionResolver();
        if (resolver == null) {
            resolver = new StandardBeanExpressionResolver();
        }
        BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, null);
        Object result = resolver.evaluate(expression, expressionContext);
        return result != null && ((Boolean) result).booleanValue();
    }

    private String wrapIfNecessary(String expression) {
        if (!expression.startsWith(StandardBeanExpressionResolver.DEFAULT_EXPRESSION_PREFIX)) {
            return "#{" + expression + "}";
        }
        return expression;
    }
}
