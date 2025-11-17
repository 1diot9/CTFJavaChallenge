package org.springframework.aop.framework;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.MethodMatcher;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/InterceptorAndDynamicMethodMatcher.class */
final class InterceptorAndDynamicMethodMatcher extends Record {
    private final MethodInterceptor interceptor;
    private final MethodMatcher matcher;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InterceptorAndDynamicMethodMatcher(MethodInterceptor interceptor, MethodMatcher matcher) {
        this.interceptor = interceptor;
        this.matcher = matcher;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, InterceptorAndDynamicMethodMatcher.class), InterceptorAndDynamicMethodMatcher.class, "interceptor;matcher", "FIELD:Lorg/springframework/aop/framework/InterceptorAndDynamicMethodMatcher;->interceptor:Lorg/aopalliance/intercept/MethodInterceptor;", "FIELD:Lorg/springframework/aop/framework/InterceptorAndDynamicMethodMatcher;->matcher:Lorg/springframework/aop/MethodMatcher;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, InterceptorAndDynamicMethodMatcher.class), InterceptorAndDynamicMethodMatcher.class, "interceptor;matcher", "FIELD:Lorg/springframework/aop/framework/InterceptorAndDynamicMethodMatcher;->interceptor:Lorg/aopalliance/intercept/MethodInterceptor;", "FIELD:Lorg/springframework/aop/framework/InterceptorAndDynamicMethodMatcher;->matcher:Lorg/springframework/aop/MethodMatcher;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, InterceptorAndDynamicMethodMatcher.class, Object.class), InterceptorAndDynamicMethodMatcher.class, "interceptor;matcher", "FIELD:Lorg/springframework/aop/framework/InterceptorAndDynamicMethodMatcher;->interceptor:Lorg/aopalliance/intercept/MethodInterceptor;", "FIELD:Lorg/springframework/aop/framework/InterceptorAndDynamicMethodMatcher;->matcher:Lorg/springframework/aop/MethodMatcher;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public MethodInterceptor interceptor() {
        return this.interceptor;
    }

    public MethodMatcher matcher() {
        return this.matcher;
    }
}
