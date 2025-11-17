package org.springframework.web.context.request.async;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/DeferredResult.class */
public class DeferredResult<T> {
    private static final Object RESULT_NONE = new Object();
    private static final Log logger = LogFactory.getLog((Class<?>) DeferredResult.class);

    @Nullable
    private final Long timeoutValue;
    private final Supplier<?> timeoutResult;

    @Nullable
    private Runnable timeoutCallback;

    @Nullable
    private Consumer<Throwable> errorCallback;

    @Nullable
    private Runnable completionCallback;

    @Nullable
    private DeferredResultHandler resultHandler;

    @Nullable
    private volatile Object result;
    private volatile boolean expired;

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/DeferredResult$DeferredResultHandler.class */
    public interface DeferredResultHandler {
        void handleResult(@Nullable Object result);
    }

    public DeferredResult() {
        this(null);
    }

    public DeferredResult(Long timeoutValue) {
        this(timeoutValue, (Supplier<?>) () -> {
            return RESULT_NONE;
        });
    }

    public DeferredResult(@Nullable Long timeoutValue, Object timeoutResult) {
        this(timeoutValue, (Supplier<?>) () -> {
            return timeoutResult;
        });
    }

    public DeferredResult(@Nullable Long timeoutValue, Supplier<?> timeoutResult) {
        this.result = RESULT_NONE;
        this.timeoutValue = timeoutValue;
        this.timeoutResult = timeoutResult;
    }

    public final boolean isSetOrExpired() {
        return this.result != RESULT_NONE || this.expired;
    }

    public boolean hasResult() {
        return this.result != RESULT_NONE;
    }

    @Nullable
    public Object getResult() {
        Object resultToCheck = this.result;
        if (resultToCheck != RESULT_NONE) {
            return resultToCheck;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public final Long getTimeoutValue() {
        return this.timeoutValue;
    }

    public void onTimeout(Runnable callback) {
        this.timeoutCallback = callback;
    }

    public void onError(Consumer<Throwable> callback) {
        this.errorCallback = callback;
    }

    public void onCompletion(Runnable callback) {
        this.completionCallback = callback;
    }

    public final void setResultHandler(DeferredResultHandler resultHandler) {
        Assert.notNull(resultHandler, "DeferredResultHandler is required");
        if (this.expired) {
            return;
        }
        synchronized (this) {
            if (this.expired) {
                return;
            }
            Object resultToHandle = this.result;
            if (resultToHandle == RESULT_NONE) {
                this.resultHandler = resultHandler;
                return;
            }
            try {
                resultHandler.handleResult(resultToHandle);
            } catch (Throwable ex) {
                logger.debug("Failed to process async result", ex);
            }
        }
    }

    public boolean setResult(T result) {
        return setResultInternal(result);
    }

    private boolean setResultInternal(Object result) {
        if (isSetOrExpired()) {
            return false;
        }
        synchronized (this) {
            if (isSetOrExpired()) {
                return false;
            }
            this.result = result;
            DeferredResultHandler resultHandlerToUse = this.resultHandler;
            if (resultHandlerToUse == null) {
                return true;
            }
            this.resultHandler = null;
            resultHandlerToUse.handleResult(result);
            return true;
        }
    }

    public boolean setErrorResult(Object result) {
        return setResultInternal(result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DeferredResultProcessingInterceptor getInterceptor() {
        return new DeferredResultProcessingInterceptor() { // from class: org.springframework.web.context.request.async.DeferredResult.1
            /* JADX WARN: Finally extract failed */
            @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
            public <S> boolean handleTimeout(NativeWebRequest request, DeferredResult<S> deferredResult) {
                boolean continueProcessing = true;
                try {
                    if (DeferredResult.this.timeoutCallback != null) {
                        DeferredResult.this.timeoutCallback.run();
                    }
                    Object value = DeferredResult.this.timeoutResult.get();
                    if (value != DeferredResult.RESULT_NONE) {
                        continueProcessing = false;
                        try {
                            DeferredResult.this.setResultInternal(value);
                        } catch (Throwable ex) {
                            DeferredResult.logger.debug("Failed to handle timeout result", ex);
                        }
                    }
                    return continueProcessing;
                } catch (Throwable th) {
                    Object value2 = DeferredResult.this.timeoutResult.get();
                    if (value2 != DeferredResult.RESULT_NONE) {
                        try {
                            DeferredResult.this.setResultInternal(value2);
                        } catch (Throwable ex2) {
                            DeferredResult.logger.debug("Failed to handle timeout result", ex2);
                        }
                    }
                    throw th;
                }
            }

            @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
            public <S> boolean handleError(NativeWebRequest request, DeferredResult<S> deferredResult, Throwable t) {
                try {
                    if (DeferredResult.this.errorCallback != null) {
                        DeferredResult.this.errorCallback.accept(t);
                    }
                    try {
                        return false;
                    } catch (Throwable ex) {
                        return false;
                    }
                } finally {
                    try {
                        DeferredResult.this.setResultInternal(t);
                    } catch (Throwable ex2) {
                        DeferredResult.logger.debug("Failed to handle error result", ex2);
                    }
                }
            }

            @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
            public <S> void afterCompletion(NativeWebRequest request, DeferredResult<S> deferredResult) {
                DeferredResult.this.expired = true;
                if (DeferredResult.this.completionCallback != null) {
                    DeferredResult.this.completionCallback.run();
                }
            }
        };
    }
}
