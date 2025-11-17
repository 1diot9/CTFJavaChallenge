package org.springframework.aop.interceptor;

import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrencyThrottleSupport;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/ConcurrencyThrottleInterceptor.class */
public class ConcurrencyThrottleInterceptor extends ConcurrencyThrottleSupport implements MethodInterceptor, Serializable {
    public ConcurrencyThrottleInterceptor() {
        setConcurrencyLimit(1);
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        beforeAccess();
        try {
            return methodInvocation.proceed();
        } finally {
            afterAccess();
        }
    }
}
