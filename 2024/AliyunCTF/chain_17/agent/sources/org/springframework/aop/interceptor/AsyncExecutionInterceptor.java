package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/AsyncExecutionInterceptor.class */
public class AsyncExecutionInterceptor extends AsyncExecutionAspectSupport implements MethodInterceptor, Ordered {
    public AsyncExecutionInterceptor(@Nullable Executor defaultExecutor) {
        super(defaultExecutor);
    }

    public AsyncExecutionInterceptor(@Nullable Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
        super(defaultExecutor, exceptionHandler);
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null;
        Method userMethod = BridgeMethodResolver.getMostSpecificMethod(invocation.getMethod(), targetClass);
        AsyncTaskExecutor executor = determineAsyncExecutor(userMethod);
        if (executor == null) {
            throw new IllegalStateException("No executor specified and no default executor set on AsyncExecutionInterceptor either");
        }
        Callable<Object> task = () -> {
            try {
                Object result = invocation.proceed();
                if (result instanceof Future) {
                    Future<?> future = (Future) result;
                    return future.get();
                }
                return null;
            } catch (ExecutionException ex) {
                handleError(ex.getCause(), userMethod, invocation.getArguments());
                return null;
            } catch (Throwable ex2) {
                handleError(ex2, userMethod, invocation.getArguments());
                return null;
            }
        };
        return doSubmit(task, executor, invocation.getMethod().getReturnType());
    }

    @Override // org.springframework.aop.interceptor.AsyncExecutionAspectSupport
    @Nullable
    protected String getExecutorQualifier(Method method) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.interceptor.AsyncExecutionAspectSupport
    @Nullable
    public Executor getDefaultExecutor(@Nullable BeanFactory beanFactory) {
        Executor defaultExecutor = super.getDefaultExecutor(beanFactory);
        return defaultExecutor != null ? defaultExecutor : new SimpleAsyncTaskExecutor();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
