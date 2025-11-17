package org.springframework.web.servlet.mvc.method.annotation;

import java.util.concurrent.Callable;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/CallableMethodReturnValueHandler.class */
public class CallableMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return Callable.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
        } else {
            Callable<?> callable = (Callable) returnValue;
            WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(callable, mavContainer);
        }
    }
}
