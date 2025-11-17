package org.springframework.aop.interceptor;

import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/ExposeInvocationInterceptor.class */
public final class ExposeInvocationInterceptor implements MethodInterceptor, PriorityOrdered, Serializable {
    public static final ExposeInvocationInterceptor INSTANCE = new ExposeInvocationInterceptor();
    public static final Advisor ADVISOR = new DefaultPointcutAdvisor(INSTANCE) { // from class: org.springframework.aop.interceptor.ExposeInvocationInterceptor.1
        @Override // org.springframework.aop.support.DefaultPointcutAdvisor, org.springframework.aop.support.AbstractGenericPointcutAdvisor
        public String toString() {
            return ExposeInvocationInterceptor.class.getName() + ".ADVISOR";
        }
    };
    private static final ThreadLocal<MethodInvocation> invocation = new NamedThreadLocal("Current AOP method invocation");

    public static MethodInvocation currentInvocation() throws IllegalStateException {
        MethodInvocation mi = invocation.get();
        if (mi == null) {
            throw new IllegalStateException("No MethodInvocation found: Check that an AOP invocation is in progress and that the ExposeInvocationInterceptor is upfront in the interceptor chain. Specifically, note that advices with order HIGHEST_PRECEDENCE will execute before ExposeInvocationInterceptor! In addition, ExposeInvocationInterceptor and ExposeInvocationInterceptor.currentInvocation() must be invoked from the same thread.");
        }
        return mi;
    }

    private ExposeInvocationInterceptor() {
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation mi) throws Throwable {
        MethodInvocation oldInvocation = invocation.get();
        invocation.set(mi);
        try {
            Object proceed = mi.proceed();
            invocation.set(oldInvocation);
            return proceed;
        } catch (Throwable th) {
            invocation.set(oldInvocation);
            throw th;
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return RandomValuePropertySourceEnvironmentPostProcessor.ORDER;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
