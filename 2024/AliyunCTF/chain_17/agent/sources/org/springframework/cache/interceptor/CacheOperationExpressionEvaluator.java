package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheOperationExpressionEvaluator.class */
class CacheOperationExpressionEvaluator extends CachedExpressionEvaluator {
    public static final Object NO_RESULT = new Object();
    public static final Object RESULT_UNAVAILABLE = new Object();
    public static final String RESULT_VARIABLE = "result";
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> keyCache = new ConcurrentHashMap(64);
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap(64);
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> unlessCache = new ConcurrentHashMap(64);
    private final CacheEvaluationContextFactory evaluationContextFactory;

    public CacheOperationExpressionEvaluator(CacheEvaluationContextFactory evaluationContextFactory) {
        this.evaluationContextFactory = evaluationContextFactory;
        this.evaluationContextFactory.setParameterNameDiscoverer(() -> {
            return this.getParameterNameDiscoverer();
        });
    }

    public EvaluationContext createEvaluationContext(Collection<? extends Cache> caches, Method method, Object[] args, Object target, Class<?> targetClass, Method targetMethod, @Nullable Object result) {
        CacheExpressionRootObject rootObject = new CacheExpressionRootObject(caches, method, args, target, targetClass);
        CacheEvaluationContext evaluationContext = this.evaluationContextFactory.forOperation(rootObject, targetMethod, args);
        if (result == RESULT_UNAVAILABLE) {
            evaluationContext.addUnavailableVariable(RESULT_VARIABLE);
        } else if (result != NO_RESULT) {
            evaluationContext.setVariable(RESULT_VARIABLE, result);
        }
        return evaluationContext;
    }

    @Nullable
    public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
    }

    public boolean condition(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(getExpression(this.conditionCache, methodKey, conditionExpression).getValue(evalContext, Boolean.class));
    }

    public boolean unless(String unlessExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(getExpression(this.unlessCache, methodKey, unlessExpression).getValue(evalContext, Boolean.class));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.keyCache.clear();
        this.conditionCache.clear();
        this.unlessCache.clear();
    }
}
