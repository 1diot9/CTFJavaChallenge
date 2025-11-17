package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RequestResponseBodyMethodProcessor.class */
public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {
    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, null, requestResponseBodyAdvice);
    }

    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) || returnType.hasMethodAnnotation(ResponseBody.class);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        MethodParameter parameter2 = parameter.nestedIfOptional();
        Object arg = readWithMessageConverters(webRequest, parameter2, parameter2.getNestedGenericParameterType());
        if (binderFactory != null) {
            String name = Conventions.getVariableNameForParameter(parameter2);
            ResolvableType type = ResolvableType.forMethodParameter(parameter2);
            WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name, type);
            if (arg != null) {
                validateIfApplicable(binder, parameter2);
                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter2)) {
                    throw new MethodArgumentNotValidException(parameter2, binder.getBindingResult());
                }
            }
            if (mavContainer != null) {
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
            }
        }
        return adaptArgumentIfNecessary(arg, parameter2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver
    public <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        Object arg = readWithMessageConverters(inputMessage, parameter, paramType);
        if (arg == null && checkRequired(parameter)) {
            throw new HttpMessageNotReadableException("Required request body is missing: " + parameter.getExecutable().toGenericString(), inputMessage);
        }
        return arg;
    }

    protected boolean checkRequired(MethodParameter parameter) {
        RequestBody requestBody = (RequestBody) parameter.getParameterAnnotation(RequestBody.class);
        return (requestBody == null || !requestBody.required() || parameter.isOptional()) ? false : true;
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        mavContainer.setRequestHandled(true);
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        if (returnValue instanceof ProblemDetail) {
            ProblemDetail detail = (ProblemDetail) returnValue;
            outputMessage.setStatusCode(HttpStatusCode.valueOf(detail.getStatus()));
            if (detail.getInstance() == null) {
                URI path = URI.create(inputMessage.getServletRequest().getRequestURI());
                detail.setInstance(path);
            }
        }
        writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
    }
}
