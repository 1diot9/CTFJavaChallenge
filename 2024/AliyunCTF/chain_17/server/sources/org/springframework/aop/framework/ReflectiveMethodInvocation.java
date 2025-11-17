package org.springframework.aop.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/ReflectiveMethodInvocation.class */
public class ReflectiveMethodInvocation implements ProxyMethodInvocation, Cloneable {
    protected final Object proxy;

    @Nullable
    protected final Object target;
    protected final Method method;
    protected Object[] arguments;

    @Nullable
    private final Class<?> targetClass;

    @Nullable
    private Map<String, Object> userAttributes;
    protected final List<?> interceptorsAndDynamicMethodMatchers;
    private int currentInterceptorIndex = -1;

    /* JADX INFO: Access modifiers changed from: protected */
    public ReflectiveMethodInvocation(Object proxy, @Nullable Object target, Method method, @Nullable Object[] arguments, @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.arguments = AopProxyUtils.adaptArgumentsIfNecessary(method, arguments);
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    @Override // org.springframework.aop.ProxyMethodInvocation
    public final Object getProxy() {
        return this.proxy;
    }

    @Override // org.aopalliance.intercept.Joinpoint
    @Nullable
    public final Object getThis() {
        return this.target;
    }

    @Override // org.aopalliance.intercept.Joinpoint
    public final AccessibleObject getStaticPart() {
        return this.method;
    }

    @Override // org.aopalliance.intercept.MethodInvocation
    public final Method getMethod() {
        return this.method;
    }

    @Override // org.aopalliance.intercept.Invocation
    public final Object[] getArguments() {
        return this.arguments;
    }

    @Override // org.springframework.aop.ProxyMethodInvocation
    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }

    @Override // org.aopalliance.intercept.Joinpoint
    @Nullable
    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return invokeJoinpoint();
        }
        List<?> list = this.interceptorsAndDynamicMethodMatchers;
        int i = this.currentInterceptorIndex + 1;
        this.currentInterceptorIndex = i;
        Object interceptorOrInterceptionAdvice = list.get(i);
        if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
            InterceptorAndDynamicMethodMatcher dm = (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
            Class<?> targetClass = this.targetClass != null ? this.targetClass : this.method.getDeclaringClass();
            if (dm.matcher().matches(this.method, targetClass, this.arguments)) {
                return dm.interceptor().invoke(this);
            }
            return proceed();
        }
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
    }

    @Nullable
    protected Object invokeJoinpoint() throws Throwable {
        return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
    }

    @Override // org.springframework.aop.ProxyMethodInvocation
    public MethodInvocation invocableClone() {
        Object[] cloneArguments = this.arguments;
        if (this.arguments.length > 0) {
            cloneArguments = (Object[]) this.arguments.clone();
        }
        return invocableClone(cloneArguments);
    }

    @Override // org.springframework.aop.ProxyMethodInvocation
    public MethodInvocation invocableClone(Object... arguments) {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap();
        }
        try {
            ReflectiveMethodInvocation clone = (ReflectiveMethodInvocation) clone();
            clone.arguments = arguments;
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Should be able to clone object of type [" + getClass() + "]: " + ex);
        }
    }

    @Override // org.springframework.aop.ProxyMethodInvocation
    public void setUserAttribute(String key, @Nullable Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap();
            }
            this.userAttributes.put(key, value);
        } else if (this.userAttributes != null) {
            this.userAttributes.remove(key);
        }
    }

    @Override // org.springframework.aop.ProxyMethodInvocation
    @Nullable
    public Object getUserAttribute(String key) {
        if (this.userAttributes != null) {
            return this.userAttributes.get(key);
        }
        return null;
    }

    public Map<String, Object> getUserAttributes() {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap();
        }
        return this.userAttributes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ReflectiveMethodInvocation: ");
        sb.append(this.method).append("; ");
        if (this.target == null) {
            sb.append("target is null");
        } else {
            sb.append("target is of class [").append(this.target.getClass().getName()).append(']');
        }
        return sb.toString();
    }
}
