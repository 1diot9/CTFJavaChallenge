package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/AsyncTaskMethodReturnValueHandler.class */
public class AsyncTaskMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Nullable
    private final BeanFactory beanFactory;

    public AsyncTaskMethodReturnValueHandler(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return WebAsyncTask.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        WebAsyncTask<?> webAsyncTask = (WebAsyncTask) returnValue;
        if (this.beanFactory != null) {
            webAsyncTask.setBeanFactory(this.beanFactory);
        }
        WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(webAsyncTask, mavContainer);
    }
}
