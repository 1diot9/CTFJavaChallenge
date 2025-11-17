package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/AsyncSupportConfigurer.class */
public class AsyncSupportConfigurer {

    @Nullable
    private AsyncTaskExecutor taskExecutor;

    @Nullable
    private Long timeout;
    private final List<CallableProcessingInterceptor> callableInterceptors = new ArrayList();
    private final List<DeferredResultProcessingInterceptor> deferredResultInterceptors = new ArrayList();

    public AsyncSupportConfigurer setTaskExecutor(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        return this;
    }

    public AsyncSupportConfigurer setDefaultTimeout(long timeout) {
        this.timeout = Long.valueOf(timeout);
        return this;
    }

    public AsyncSupportConfigurer registerCallableInterceptors(CallableProcessingInterceptor... interceptors) {
        this.callableInterceptors.addAll(Arrays.asList(interceptors));
        return this;
    }

    public AsyncSupportConfigurer registerDeferredResultInterceptors(DeferredResultProcessingInterceptor... interceptors) {
        this.deferredResultInterceptors.addAll(Arrays.asList(interceptors));
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public AsyncTaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Long getTimeout() {
        return this.timeout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<CallableProcessingInterceptor> getCallableInterceptors() {
        return this.callableInterceptors;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<DeferredResultProcessingInterceptor> getDeferredResultInterceptors() {
        return this.deferredResultInterceptors;
    }
}
