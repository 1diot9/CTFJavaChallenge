package org.springframework.context.expression;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.core.KotlinDetector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/expression/MethodBasedEvaluationContext.class */
public class MethodBasedEvaluationContext extends StandardEvaluationContext {
    private final Method method;
    private final Object[] arguments;
    private final ParameterNameDiscoverer parameterNameDiscoverer;
    private boolean argumentsLoaded;

    public MethodBasedEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject);
        this.argumentsLoaded = false;
        this.method = method;
        this.arguments = KotlinDetector.isSuspendingFunction(method) ? Arrays.copyOf(arguments, arguments.length - 1) : arguments;
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Override // org.springframework.expression.spel.support.StandardEvaluationContext, org.springframework.expression.EvaluationContext
    @Nullable
    public Object lookupVariable(String name) {
        Object variable = super.lookupVariable(name);
        if (variable != null) {
            return variable;
        }
        if (!this.argumentsLoaded) {
            lazyLoadArguments();
            this.argumentsLoaded = true;
            variable = super.lookupVariable(name);
        }
        return variable;
    }

    protected void lazyLoadArguments() {
        if (ObjectUtils.isEmpty(this.arguments)) {
            return;
        }
        String[] paramNames = this.parameterNameDiscoverer.getParameterNames(this.method);
        int paramCount = paramNames != null ? paramNames.length : this.method.getParameterCount();
        int argsCount = this.arguments.length;
        for (int i = 0; i < paramCount; i++) {
            Object value = null;
            if (argsCount > paramCount && i == paramCount - 1) {
                value = Arrays.copyOfRange(this.arguments, i, argsCount);
            } else if (argsCount > i) {
                value = this.arguments[i];
            }
            setVariable("a" + i, value);
            setVariable("p" + i, value);
            if (paramNames != null && paramNames[i] != null) {
                setVariable(paramNames[i], value);
            }
        }
    }
}
