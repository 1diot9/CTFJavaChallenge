package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.function.Supplier;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheEvaluationContextFactory.class */
class CacheEvaluationContextFactory {
    private final StandardEvaluationContext originalContext;

    @Nullable
    private Supplier<ParameterNameDiscoverer> parameterNameDiscoverer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheEvaluationContextFactory(StandardEvaluationContext originalContext) {
        this.originalContext = originalContext;
    }

    public void setParameterNameDiscoverer(Supplier<ParameterNameDiscoverer> parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        if (this.parameterNameDiscoverer == null) {
            this.parameterNameDiscoverer = SingletonSupplier.of(new DefaultParameterNameDiscoverer());
        }
        return this.parameterNameDiscoverer.get();
    }

    public CacheEvaluationContext forOperation(CacheExpressionRootObject rootObject, Method targetMethod, Object[] args) {
        CacheEvaluationContext evaluationContext = new CacheEvaluationContext(rootObject, targetMethod, args, getParameterNameDiscoverer());
        this.originalContext.applyDelegatesTo(evaluationContext);
        return evaluationContext;
    }
}
