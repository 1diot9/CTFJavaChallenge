package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.Job;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.reactivestreams.Publisher;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.KotlinDetector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheInterceptor.class */
public class CacheInterceptor extends CacheAspectSupport implements MethodInterceptor, Serializable {
    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        CacheOperationInvoker aopAllianceInvoker = () -> {
            try {
                if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isSuspendingFunction(method)) {
                    return KotlinDelegate.invokeSuspendingFunction(method, invocation.getThis(), invocation.getArguments());
                }
                return invocation.proceed();
            } catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapper(ex);
            }
        };
        Object target = invocation.getThis();
        Assert.state(target != null, "Target must not be null");
        try {
            return execute(aopAllianceInvoker, target, method, invocation.getArguments());
        } catch (CacheOperationInvoker.ThrowableWrapper th) {
            throw th.getOriginal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheInterceptor$KotlinDelegate.class */
    public static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static Publisher<?> invokeSuspendingFunction(Method method, Object target, Object... args) {
            Continuation<?> continuation = (Continuation) args[args.length - 1];
            CoroutineContext coroutineContext = continuation.getContext().minusKey(Job.Key);
            return CoroutinesUtils.invokeSuspendingFunction(coroutineContext, method, target, args);
        }
    }
}
