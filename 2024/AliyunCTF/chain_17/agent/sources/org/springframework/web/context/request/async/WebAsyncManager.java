package org.springframework.web.context.request.async;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/WebAsyncManager.class */
public final class WebAsyncManager {
    private static final Object RESULT_NONE = new Object();
    private static final AsyncTaskExecutor DEFAULT_TASK_EXECUTOR = new SimpleAsyncTaskExecutor(WebAsyncManager.class.getSimpleName());
    private static final Log logger = LogFactory.getLog((Class<?>) WebAsyncManager.class);
    private static final CallableProcessingInterceptor timeoutCallableInterceptor = new TimeoutCallableProcessingInterceptor();
    private static final DeferredResultProcessingInterceptor timeoutDeferredResultInterceptor = new TimeoutDeferredResultProcessingInterceptor();

    @Nullable
    private AsyncWebRequest asyncWebRequest;

    @Nullable
    private volatile Object[] concurrentResultContext;
    private volatile boolean errorHandlingInProgress;
    private AsyncTaskExecutor taskExecutor = DEFAULT_TASK_EXECUTOR;

    @Nullable
    private volatile Object concurrentResult = RESULT_NONE;
    private final Map<Object, CallableProcessingInterceptor> callableInterceptors = new LinkedHashMap();
    private final Map<Object, DeferredResultProcessingInterceptor> deferredResultInterceptors = new LinkedHashMap();

    public void setAsyncWebRequest(AsyncWebRequest asyncWebRequest) {
        Assert.notNull(asyncWebRequest, "AsyncWebRequest must not be null");
        this.asyncWebRequest = asyncWebRequest;
        this.asyncWebRequest.addCompletionHandler(() -> {
            asyncWebRequest.removeAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, 0);
        });
    }

    public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public boolean isConcurrentHandlingStarted() {
        return this.asyncWebRequest != null && this.asyncWebRequest.isAsyncStarted();
    }

    public boolean hasConcurrentResult() {
        return this.concurrentResult != RESULT_NONE;
    }

    public Object getConcurrentResult() {
        return this.concurrentResult;
    }

    @Nullable
    public Object[] getConcurrentResultContext() {
        return this.concurrentResultContext;
    }

    @Nullable
    public CallableProcessingInterceptor getCallableInterceptor(Object key) {
        return this.callableInterceptors.get(key);
    }

    @Nullable
    public DeferredResultProcessingInterceptor getDeferredResultInterceptor(Object key) {
        return this.deferredResultInterceptors.get(key);
    }

    public void registerCallableInterceptor(Object key, CallableProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "CallableProcessingInterceptor is required");
        this.callableInterceptors.put(key, interceptor);
    }

    public void registerCallableInterceptors(CallableProcessingInterceptor... interceptors) {
        Assert.notNull(interceptors, "A CallableProcessingInterceptor is required");
        for (CallableProcessingInterceptor interceptor : interceptors) {
            String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.callableInterceptors.put(key, interceptor);
        }
    }

    public void registerDeferredResultInterceptor(Object key, DeferredResultProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "DeferredResultProcessingInterceptor is required");
        this.deferredResultInterceptors.put(key, interceptor);
    }

    public void registerDeferredResultInterceptors(DeferredResultProcessingInterceptor... interceptors) {
        Assert.notNull(interceptors, "A DeferredResultProcessingInterceptor is required");
        for (DeferredResultProcessingInterceptor interceptor : interceptors) {
            String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.deferredResultInterceptors.put(key, interceptor);
        }
    }

    public void clearConcurrentResult() {
        synchronized (this) {
            this.concurrentResult = RESULT_NONE;
            this.concurrentResultContext = null;
        }
    }

    public void startCallableProcessing(Callable<?> callable, Object... processingContext) throws Exception {
        Assert.notNull(callable, "Callable must not be null");
        startCallableProcessing(new WebAsyncTask<>(callable), processingContext);
    }

    public void startCallableProcessing(final WebAsyncTask<?> webAsyncTask, Object... processingContext) throws Exception {
        Assert.notNull(webAsyncTask, "WebAsyncTask must not be null");
        Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");
        Long timeout = webAsyncTask.getTimeout();
        if (timeout != null) {
            this.asyncWebRequest.setTimeout(timeout);
        }
        AsyncTaskExecutor executor = webAsyncTask.getExecutor();
        if (executor != null) {
            this.taskExecutor = executor;
        }
        List<CallableProcessingInterceptor> interceptors = new ArrayList<>();
        interceptors.add(webAsyncTask.getInterceptor());
        interceptors.addAll(this.callableInterceptors.values());
        interceptors.add(timeoutCallableInterceptor);
        Callable<?> callable = webAsyncTask.getCallable();
        CallableInterceptorChain interceptorChain = new CallableInterceptorChain(interceptors);
        this.asyncWebRequest.addTimeoutHandler(() -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Async request timeout for " + formatRequestUri());
            }
            Object result = interceptorChain.triggerAfterTimeout(this.asyncWebRequest, callable);
            if (result != CallableProcessingInterceptor.RESULT_NONE) {
                setConcurrentResultAndDispatch(result);
            }
        });
        this.asyncWebRequest.addErrorHandler(ex -> {
            if (!this.errorHandlingInProgress) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Async request error for " + formatRequestUri() + ": " + ex);
                }
                Object result = interceptorChain.triggerAfterError(this.asyncWebRequest, callable, ex);
                setConcurrentResultAndDispatch(result != CallableProcessingInterceptor.RESULT_NONE ? result : ex);
            }
        });
        this.asyncWebRequest.addCompletionHandler(() -> {
            interceptorChain.triggerAfterCompletion(this.asyncWebRequest, callable);
        });
        interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, callable);
        startAsyncProcessing(processingContext);
        try {
            Future<?> future = this.taskExecutor.submit(() -> {
                Object result;
                Object result2 = null;
                try {
                    interceptorChain.applyPreProcess(this.asyncWebRequest, callable);
                    result2 = callable.call();
                    result = interceptorChain.applyPostProcess(this.asyncWebRequest, callable, result2);
                } catch (Throwable th) {
                    interceptorChain.applyPostProcess(this.asyncWebRequest, callable, result2);
                    throw th;
                }
                setConcurrentResultAndDispatch(result);
            });
            interceptorChain.setTaskFuture(future);
        } catch (Throwable ex2) {
            Object result = interceptorChain.applyPostProcess(this.asyncWebRequest, callable, ex2);
            setConcurrentResultAndDispatch(result);
        }
    }

    private String formatRequestUri() {
        HttpServletRequest request = (HttpServletRequest) this.asyncWebRequest.getNativeRequest(HttpServletRequest.class);
        return request != null ? request.getRequestURI() : "servlet container";
    }

    private void setConcurrentResultAndDispatch(@Nullable Object result) {
        synchronized (this) {
            if (this.concurrentResult != RESULT_NONE) {
                return;
            }
            this.concurrentResult = result;
            this.errorHandlingInProgress = result instanceof Throwable;
            if (this.asyncWebRequest.isAsyncComplete()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Async result set but request already complete: " + formatRequestUri());
                }
            } else {
                if (logger.isDebugEnabled()) {
                    boolean isError = result instanceof Throwable;
                    logger.debug("Async " + (isError ? "error" : "result set") + ", dispatch to " + formatRequestUri());
                }
                this.asyncWebRequest.dispatch();
            }
        }
    }

    public void startDeferredResultProcessing(final DeferredResult<?> deferredResult, Object... processingContext) throws Exception {
        Assert.notNull(deferredResult, "DeferredResult must not be null");
        Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");
        Long timeout = deferredResult.getTimeoutValue();
        if (timeout != null) {
            this.asyncWebRequest.setTimeout(timeout);
        }
        List<DeferredResultProcessingInterceptor> interceptors = new ArrayList<>();
        interceptors.add(deferredResult.getInterceptor());
        interceptors.addAll(this.deferredResultInterceptors.values());
        interceptors.add(timeoutDeferredResultInterceptor);
        DeferredResultInterceptorChain interceptorChain = new DeferredResultInterceptorChain(interceptors);
        this.asyncWebRequest.addTimeoutHandler(() -> {
            try {
                interceptorChain.triggerAfterTimeout(this.asyncWebRequest, deferredResult);
            } catch (Throwable ex) {
                setConcurrentResultAndDispatch(ex);
            }
        });
        this.asyncWebRequest.addErrorHandler(ex -> {
            if (!this.errorHandlingInProgress) {
                try {
                    if (!interceptorChain.triggerAfterError(this.asyncWebRequest, deferredResult, ex)) {
                        return;
                    }
                    deferredResult.setErrorResult(ex);
                } catch (Throwable interceptorEx) {
                    setConcurrentResultAndDispatch(interceptorEx);
                }
            }
        });
        this.asyncWebRequest.addCompletionHandler(() -> {
            interceptorChain.triggerAfterCompletion(this.asyncWebRequest, deferredResult);
        });
        interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, deferredResult);
        startAsyncProcessing(processingContext);
        try {
            interceptorChain.applyPreProcess(this.asyncWebRequest, deferredResult);
            deferredResult.setResultHandler(result -> {
                setConcurrentResultAndDispatch(interceptorChain.applyPostProcess(this.asyncWebRequest, deferredResult, result));
            });
        } catch (Throwable ex2) {
            setConcurrentResultAndDispatch(ex2);
        }
    }

    private void startAsyncProcessing(Object[] processingContext) {
        synchronized (this) {
            this.concurrentResult = RESULT_NONE;
            this.concurrentResultContext = processingContext;
            this.errorHandlingInProgress = false;
        }
        this.asyncWebRequest.startAsync();
        if (logger.isDebugEnabled()) {
            logger.debug("Started async request");
        }
    }
}
