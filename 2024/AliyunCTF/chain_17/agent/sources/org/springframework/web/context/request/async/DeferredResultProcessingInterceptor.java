package org.springframework.web.context.request.async;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/DeferredResultProcessingInterceptor.class */
public interface DeferredResultProcessingInterceptor {
    default <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
    }

    default <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
    }

    default <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, @Nullable Object concurrentResult) throws Exception {
    }

    default <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        return true;
    }

    default <T> boolean handleError(NativeWebRequest request, DeferredResult<T> deferredResult, Throwable t) throws Exception {
        return true;
    }

    default <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
    }
}
