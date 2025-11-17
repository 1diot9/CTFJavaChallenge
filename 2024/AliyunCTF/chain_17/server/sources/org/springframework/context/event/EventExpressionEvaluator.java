package org.springframework.context.event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/EventExpressionEvaluator.class */
class EventExpressionEvaluator extends CachedExpressionEvaluator {
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap(64);
    private final StandardEvaluationContext originalEvaluationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventExpressionEvaluator(StandardEvaluationContext originalEvaluationContext) {
        this.originalEvaluationContext = originalEvaluationContext;
    }

    public boolean condition(String conditionExpression, ApplicationEvent event, Method targetMethod, AnnotatedElementKey methodKey, Object[] args) {
        EventExpressionRootObject rootObject = new EventExpressionRootObject(event, args);
        EvaluationContext evaluationContext = createEvaluationContext(rootObject, targetMethod, args);
        return Boolean.TRUE.equals(getExpression(this.conditionCache, methodKey, conditionExpression).getValue(evaluationContext, Boolean.class));
    }

    private EvaluationContext createEvaluationContext(EventExpressionRootObject rootObject, Method method, Object[] args) {
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(rootObject, method, args, getParameterNameDiscoverer());
        this.originalEvaluationContext.applyDelegatesTo(evaluationContext);
        return evaluationContext;
    }
}
