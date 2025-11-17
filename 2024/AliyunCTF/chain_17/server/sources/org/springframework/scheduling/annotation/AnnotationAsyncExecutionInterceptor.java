package org.springframework.scheduling.annotation;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncExecutionInterceptor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/AnnotationAsyncExecutionInterceptor.class */
public class AnnotationAsyncExecutionInterceptor extends AsyncExecutionInterceptor {
    public AnnotationAsyncExecutionInterceptor(@Nullable Executor defaultExecutor) {
        super(defaultExecutor);
    }

    public AnnotationAsyncExecutionInterceptor(@Nullable Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
        super(defaultExecutor, exceptionHandler);
    }

    @Override // org.springframework.aop.interceptor.AsyncExecutionInterceptor, org.springframework.aop.interceptor.AsyncExecutionAspectSupport
    @Nullable
    protected String getExecutorQualifier(Method method) {
        Async async = (Async) AnnotatedElementUtils.findMergedAnnotation(method, Async.class);
        if (async == null) {
            async = (Async) AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), Async.class);
        }
        if (async != null) {
            return async.value();
        }
        return null;
    }
}
