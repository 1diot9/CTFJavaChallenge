package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/WebAsyncTask.class */
public class WebAsyncTask<V> implements BeanFactoryAware {
    private final Callable<V> callable;

    @Nullable
    private final Long timeout;

    @Nullable
    private final AsyncTaskExecutor executor;

    @Nullable
    private final String executorName;
    private BeanFactory beanFactory;
    private Callable<V> timeoutCallback;
    private Callable<V> errorCallback;
    private Runnable completionCallback;

    public WebAsyncTask(Callable<V> callable) {
        Assert.notNull(callable, "Callable must not be null");
        this.callable = callable;
        this.timeout = null;
        this.executor = null;
        this.executorName = null;
    }

    public WebAsyncTask(long timeout, Callable<V> callable) {
        Assert.notNull(callable, "Callable must not be null");
        this.callable = callable;
        this.timeout = Long.valueOf(timeout);
        this.executor = null;
        this.executorName = null;
    }

    public WebAsyncTask(@Nullable Long timeout, String executorName, Callable<V> callable) {
        Assert.notNull(callable, "Callable must not be null");
        Assert.notNull(executorName, "Executor name must not be null");
        this.callable = callable;
        this.timeout = timeout;
        this.executor = null;
        this.executorName = executorName;
    }

    public WebAsyncTask(@Nullable Long timeout, AsyncTaskExecutor executor, Callable<V> callable) {
        Assert.notNull(callable, "Callable must not be null");
        Assert.notNull(executor, "Executor must not be null");
        this.callable = callable;
        this.timeout = timeout;
        this.executor = executor;
        this.executorName = null;
    }

    public Callable<?> getCallable() {
        return this.callable;
    }

    @Nullable
    public Long getTimeout() {
        return this.timeout;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    public AsyncTaskExecutor getExecutor() {
        if (this.executor != null) {
            return this.executor;
        }
        if (this.executorName != null) {
            Assert.state(this.beanFactory != null, "BeanFactory is required to look up an executor bean by name");
            return (AsyncTaskExecutor) this.beanFactory.getBean(this.executorName, AsyncTaskExecutor.class);
        }
        return null;
    }

    public void onTimeout(Callable<V> callback) {
        this.timeoutCallback = callback;
    }

    public void onError(Callable<V> callback) {
        this.errorCallback = callback;
    }

    public void onCompletion(Runnable callback) {
        this.completionCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallableProcessingInterceptor getInterceptor() {
        return new CallableProcessingInterceptor() { // from class: org.springframework.web.context.request.async.WebAsyncTask.1
            @Override // org.springframework.web.context.request.async.CallableProcessingInterceptor
            public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
                return WebAsyncTask.this.timeoutCallback != null ? WebAsyncTask.this.timeoutCallback.call() : CallableProcessingInterceptor.RESULT_NONE;
            }

            @Override // org.springframework.web.context.request.async.CallableProcessingInterceptor
            public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
                return WebAsyncTask.this.errorCallback != null ? WebAsyncTask.this.errorCallback.call() : CallableProcessingInterceptor.RESULT_NONE;
            }

            @Override // org.springframework.web.context.request.async.CallableProcessingInterceptor
            public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
                if (WebAsyncTask.this.completionCallback != null) {
                    WebAsyncTask.this.completionCallback.run();
                }
            }
        };
    }
}
