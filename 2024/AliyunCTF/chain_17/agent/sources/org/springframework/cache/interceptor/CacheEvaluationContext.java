package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheEvaluationContext.class */
class CacheEvaluationContext extends MethodBasedEvaluationContext {
    private final Set<String> unavailableVariables;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
        this.unavailableVariables = new HashSet(1);
    }

    public void addUnavailableVariable(String name) {
        this.unavailableVariables.add(name);
    }

    @Override // org.springframework.context.expression.MethodBasedEvaluationContext, org.springframework.expression.spel.support.StandardEvaluationContext, org.springframework.expression.EvaluationContext
    @Nullable
    public Object lookupVariable(String name) {
        if (this.unavailableVariables.contains(name)) {
            throw new VariableNotAvailableException(name);
        }
        return super.lookupVariable(name);
    }
}
