package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBodyReturnValueHandler.class */
public class StreamingResponseBodyReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> bodyType;
        if (StreamingResponseBody.class.isAssignableFrom(returnType.getParameterType())) {
            return true;
        }
        return ResponseEntity.class.isAssignableFrom(returnType.getParameterType()) && (bodyType = ResolvableType.forMethodParameter(returnType).getGeneric(new int[0]).resolve()) != null && StreamingResponseBody.class.isAssignableFrom(bodyType);
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        if (returnValue instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity) returnValue;
            response.setStatus(responseEntity.getStatusCode().value());
            outputMessage.getHeaders().putAll(responseEntity.getHeaders());
            returnValue = responseEntity.getBody();
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                outputMessage.flush();
                return;
            }
        }
        ServletRequest request = (ServletRequest) webRequest.getNativeRequest(ServletRequest.class);
        Assert.state(request != null, "No ServletRequest");
        ShallowEtagHeaderFilter.disableContentCaching(request);
        Assert.isInstanceOf((Class<?>) StreamingResponseBody.class, returnValue, "StreamingResponseBody expected");
        StreamingResponseBody streamingBody = (StreamingResponseBody) returnValue;
        Callable<Void> callable = new StreamingResponseBodyTask(outputMessage.getBody(), streamingBody);
        WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(callable, mavContainer);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBodyReturnValueHandler$StreamingResponseBodyTask.class */
    private static class StreamingResponseBodyTask implements Callable<Void> {
        private final OutputStream outputStream;
        private final StreamingResponseBody streamingBody;

        public StreamingResponseBodyTask(OutputStream outputStream, StreamingResponseBody streamingBody) {
            this.outputStream = outputStream;
            this.streamingBody = streamingBody;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public Void call() throws Exception {
            this.streamingBody.writeTo(this.outputStream);
            this.outputStream.flush();
            return null;
        }
    }
}
