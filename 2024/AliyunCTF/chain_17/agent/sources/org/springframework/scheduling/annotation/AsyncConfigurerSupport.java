package org.springframework.scheduling.annotation;

import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.Nullable;

@Deprecated(since = "6.0")
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/AsyncConfigurerSupport.class */
public class AsyncConfigurerSupport implements AsyncConfigurer {
    @Override // org.springframework.scheduling.annotation.AsyncConfigurer
    public Executor getAsyncExecutor() {
        return null;
    }

    @Override // org.springframework.scheduling.annotation.AsyncConfigurer
    @Nullable
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
