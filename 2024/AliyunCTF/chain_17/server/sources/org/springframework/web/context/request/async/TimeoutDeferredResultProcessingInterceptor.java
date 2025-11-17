package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/TimeoutDeferredResultProcessingInterceptor.class */
public class TimeoutDeferredResultProcessingInterceptor implements DeferredResultProcessingInterceptor {
    @Override // org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> result) throws Exception {
        result.setErrorResult(new AsyncRequestTimeoutException());
        return false;
    }
}
