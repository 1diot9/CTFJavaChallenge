package org.springframework.web.servlet.mvc.method.annotation;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/DeferredResultMethodReturnValueHandler.class */
public class DeferredResultMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> type = returnType.getParameterType();
        return DeferredResult.class.isAssignableFrom(type) || ListenableFuture.class.isAssignableFrom(type) || CompletionStage.class.isAssignableFrom(type);
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        DeferredResult<?> result;
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        if (returnValue instanceof DeferredResult) {
            DeferredResult<?> deferredResult = (DeferredResult) returnValue;
            result = deferredResult;
        } else if (returnValue instanceof ListenableFuture) {
            ListenableFuture<?> listenableFuture = (ListenableFuture) returnValue;
            result = adaptListenableFuture(listenableFuture);
        } else if (returnValue instanceof CompletionStage) {
            CompletionStage<?> completionStage = (CompletionStage) returnValue;
            result = adaptCompletionStage(completionStage);
        } else {
            throw new IllegalStateException("Unexpected return value type: " + returnValue);
        }
        WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(result, mavContainer);
    }

    private DeferredResult<Object> adaptListenableFuture(ListenableFuture<?> future) {
        final DeferredResult<Object> result = new DeferredResult<>();
        future.addCallback(new ListenableFutureCallback<Object>() { // from class: org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler.1
            @Override // org.springframework.util.concurrent.SuccessCallback
            public void onSuccess(@Nullable Object value) {
                result.setResult(value);
            }

            @Override // org.springframework.util.concurrent.FailureCallback
            public void onFailure(Throwable ex) {
                result.setErrorResult(ex);
            }
        });
        return result;
    }

    private DeferredResult<Object> adaptCompletionStage(CompletionStage<?> future) {
        DeferredResult<Object> result = new DeferredResult<>();
        future.whenComplete((value, ex) -> {
            if (ex != null) {
                if ((ex instanceof CompletionException) && ex.getCause() != null) {
                    ex = ex.getCause();
                }
                result.setErrorResult(ex);
                return;
            }
            result.setResult(value);
        });
        return result;
    }
}
