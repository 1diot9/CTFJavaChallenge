package org.springframework.web.context.request.async;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/DeferredResultInterceptorChain.class */
class DeferredResultInterceptorChain {
    private static final Log logger = LogFactory.getLog((Class<?>) DeferredResultInterceptorChain.class);
    private final List<DeferredResultProcessingInterceptor> interceptors;
    private int preProcessingIndex = -1;

    public DeferredResultInterceptorChain(List<DeferredResultProcessingInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void applyBeforeConcurrentHandling(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.beforeConcurrentHandling(request, deferredResult);
        }
    }

    public void applyPreProcess(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.preProcess(request, deferredResult);
            this.preProcessingIndex++;
        }
    }

    @Nullable
    public Object applyPostProcess(NativeWebRequest request, DeferredResult<?> deferredResult, @Nullable Object concurrentResult) {
        try {
            for (int i = this.preProcessingIndex; i >= 0; i--) {
                this.interceptors.get(i).postProcess(request, deferredResult, concurrentResult);
            }
            return concurrentResult;
        } catch (Throwable ex) {
            return ex;
        }
    }

    public void triggerAfterTimeout(NativeWebRequest request, DeferredResult<?> deferredResult) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            if (deferredResult.isSetOrExpired() || !interceptor.handleTimeout(request, deferredResult)) {
                return;
            }
        }
    }

    public boolean triggerAfterError(NativeWebRequest request, DeferredResult<?> deferredResult, Throwable ex) throws Exception {
        for (DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            if (deferredResult.isSetOrExpired() || !interceptor.handleError(request, deferredResult, ex)) {
                return false;
            }
        }
        return true;
    }

    public void triggerAfterCompletion(NativeWebRequest request, DeferredResult<?> deferredResult) {
        for (int i = this.preProcessingIndex; i >= 0; i--) {
            try {
                this.interceptors.get(i).afterCompletion(request, deferredResult);
            } catch (Throwable ex) {
                logger.trace("Ignoring failure in afterCompletion method", ex);
            }
        }
    }
}
