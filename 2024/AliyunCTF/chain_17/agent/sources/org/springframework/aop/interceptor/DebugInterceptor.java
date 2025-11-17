package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/DebugInterceptor.class */
public class DebugInterceptor extends SimpleTraceInterceptor {
    private volatile long count;

    public DebugInterceptor() {
    }

    public DebugInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    @Override // org.springframework.aop.interceptor.AbstractTraceInterceptor, org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        synchronized (this) {
            this.count++;
        }
        return super.invoke(invocation);
    }

    @Override // org.springframework.aop.interceptor.SimpleTraceInterceptor
    protected String getInvocationDescription(MethodInvocation invocation) {
        return invocation + "; count=" + this.count;
    }

    public long getCount() {
        return this.count;
    }

    public synchronized void resetCount() {
        this.count = 0L;
    }
}
