package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import org.springframework.context.MessageSource;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod.class */
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {
    private static final Method CALLABLE_METHOD = ClassUtils.getMethod(Callable.class, "call", new Class[0]);

    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    public ServletInvocableHandlerMethod(Object handler, Method method) {
        super(handler, method);
    }

    public ServletInvocableHandlerMethod(Object handler, Method method, @Nullable MessageSource messageSource) {
        super(handler, method, messageSource);
    }

    public ServletInvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
        setResponseStatus(webRequest);
        if (returnValue == null) {
            if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
                disableContentCachingIfNecessary(webRequest);
                mavContainer.setRequestHandled(true);
                return;
            }
        } else if (StringUtils.hasText(getResponseStatusReason())) {
            mavContainer.setRequestHandled(true);
            return;
        }
        mavContainer.setRequestHandled(false);
        Assert.state(this.returnValueHandlers != null, "No return value handlers");
        try {
            this.returnValueHandlers.handleReturnValue(returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
        } catch (Exception ex) {
            if (logger.isTraceEnabled()) {
                logger.trace(formatErrorForReturnValue(returnValue), ex);
            }
            throw ex;
        }
    }

    private void setResponseStatus(ServletWebRequest webRequest) throws IOException {
        HttpStatusCode status = getResponseStatus();
        if (status == null) {
            return;
        }
        HttpServletResponse response = webRequest.getResponse();
        if (response != null) {
            String reason = getResponseStatusReason();
            if (StringUtils.hasText(reason)) {
                response.sendError(status.value(), reason);
            } else {
                response.setStatus(status.value());
            }
        }
        webRequest.getRequest().setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, status);
    }

    private boolean isRequestNotModified(ServletWebRequest webRequest) {
        return webRequest.isNotModified();
    }

    private void disableContentCachingIfNecessary(ServletWebRequest webRequest) {
        if (isRequestNotModified(webRequest)) {
            HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse(HttpServletResponse.class);
            Assert.notNull(response, "Expected HttpServletResponse");
            if (StringUtils.hasText(response.getHeader(HttpHeaders.ETAG))) {
                HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
                Assert.notNull(request, "Expected HttpServletRequest");
            }
        }
    }

    private String formatErrorForReturnValue(@Nullable Object returnValue) {
        return "Error handling return value=[" + returnValue + "]" + (returnValue != null ? ", type=" + returnValue.getClass().getName() : "") + " in " + toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServletInvocableHandlerMethod wrapConcurrentResult(Object result) {
        return new ConcurrentResultHandlerMethod(result, new ConcurrentResultMethodParameter(result));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod$ConcurrentResultHandlerMethod.class */
    public class ConcurrentResultHandlerMethod extends ServletInvocableHandlerMethod {
        private final MethodParameter returnType;

        public ConcurrentResultHandlerMethod(final Object result, ConcurrentResultMethodParameter returnType) {
            super(() -> {
                if (result instanceof Exception) {
                    Exception exception = (Exception) result;
                    throw exception;
                }
                if (result instanceof Throwable) {
                    Throwable throwable = (Throwable) result;
                    throw new ServletException("Async processing failed: " + result, throwable);
                }
                return result;
            }, ServletInvocableHandlerMethod.CALLABLE_METHOD);
            if (ServletInvocableHandlerMethod.this.returnValueHandlers != null) {
                setHandlerMethodReturnValueHandlers(ServletInvocableHandlerMethod.this.returnValueHandlers);
            }
            this.returnType = returnType;
        }

        @Override // org.springframework.web.method.HandlerMethod
        public Class<?> getBeanType() {
            return ServletInvocableHandlerMethod.this.getBeanType();
        }

        @Override // org.springframework.core.annotation.AnnotatedMethod
        public MethodParameter getReturnValueType(@Nullable Object returnValue) {
            return this.returnType;
        }

        @Override // org.springframework.core.annotation.AnnotatedMethod
        public <A extends Annotation> A getMethodAnnotation(Class<A> cls) {
            return (A) ServletInvocableHandlerMethod.this.getMethodAnnotation(cls);
        }

        @Override // org.springframework.core.annotation.AnnotatedMethod
        public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
            return ServletInvocableHandlerMethod.this.hasMethodAnnotation(annotationType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod$ConcurrentResultMethodParameter.class */
    public class ConcurrentResultMethodParameter extends AnnotatedMethod.AnnotatedMethodParameter {

        @Nullable
        private final Object returnValue;
        private final ResolvableType returnType;

        public ConcurrentResultMethodParameter(Object returnValue) {
            super(-1);
            ResolvableType generic;
            this.returnValue = returnValue;
            if (returnValue instanceof ReactiveTypeHandler.CollectedValuesList) {
                ReactiveTypeHandler.CollectedValuesList cvList = (ReactiveTypeHandler.CollectedValuesList) returnValue;
                generic = cvList.getReturnType();
            } else if (KotlinDetector.isSuspendingFunction(super.getMethod())) {
                generic = ResolvableType.forMethodParameter(ServletInvocableHandlerMethod.this.getReturnType());
            } else {
                generic = ResolvableType.forType(super.getGenericParameterType()).getGeneric(new int[0]);
            }
            this.returnType = generic;
        }

        public ConcurrentResultMethodParameter(ConcurrentResultMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
            this.returnType = original.returnType;
        }

        @Override // org.springframework.core.MethodParameter
        public Class<?> getParameterType() {
            if (this.returnValue != null) {
                return this.returnValue.getClass();
            }
            if (!ResolvableType.NONE.equals(this.returnType)) {
                return this.returnType.toClass();
            }
            return super.getParameterType();
        }

        @Override // org.springframework.core.MethodParameter
        public Type getGenericParameterType() {
            return this.returnType.getType();
        }

        @Override // org.springframework.core.annotation.AnnotatedMethod.AnnotatedMethodParameter, org.springframework.core.MethodParameter
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return super.hasMethodAnnotation(annotationType) || (annotationType == ResponseBody.class && (this.returnValue instanceof ReactiveTypeHandler.CollectedValuesList));
        }

        @Override // org.springframework.core.annotation.AnnotatedMethod.AnnotatedMethodParameter, org.springframework.core.annotation.SynthesizingMethodParameter, org.springframework.core.MethodParameter
        /* renamed from: clone */
        public ConcurrentResultMethodParameter mo2452clone() {
            return new ConcurrentResultMethodParameter(this);
        }
    }
}
