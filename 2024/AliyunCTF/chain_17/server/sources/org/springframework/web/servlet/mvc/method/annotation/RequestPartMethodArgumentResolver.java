package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.multipart.support.RequestPartServletServerHttpRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RequestPartMethodArgumentResolver.class */
public class RequestPartMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {
    public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
    }

    public RequestPartMethodArgumentResolver(List<HttpMessageConverter<?>> messageConverters, List<Object> requestResponseBodyAdvice) {
        super(messageConverters, requestResponseBodyAdvice);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestPart.class)) {
            return true;
        }
        if (parameter.hasParameterAnnotation(RequestParam.class)) {
            return false;
        }
        return MultipartResolutionDelegate.isMultipartArgument(parameter.nestedIfOptional());
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) request.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        RequestPart requestPart = (RequestPart) parameter.getParameterAnnotation(RequestPart.class);
        boolean isRequired = (requestPart == null || requestPart.required()) && !parameter.isOptional();
        String name = getPartName(parameter, requestPart);
        MethodParameter parameter2 = parameter.nestedIfOptional();
        Object arg = null;
        Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter2, servletRequest);
        if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
            arg = mpArg;
        } else {
            try {
                HttpInputMessage inputMessage = new RequestPartServletServerHttpRequest(servletRequest, name);
                arg = readWithMessageConverters(inputMessage, parameter2, parameter2.getNestedGenericParameterType());
                if (binderFactory != null) {
                    ResolvableType type = ResolvableType.forMethodParameter(parameter2);
                    WebDataBinder binder = binderFactory.createBinder(request, arg, name, type);
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
            } catch (MultipartException | MissingServletRequestPartException ex) {
                if (isRequired) {
                    throw ex;
                }
            }
        }
        if (arg == null && isRequired) {
            if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            }
            throw new MissingServletRequestPartException(name);
        }
        return adaptArgumentIfNecessary(arg, parameter2);
    }

    private String getPartName(MethodParameter methodParam, @Nullable RequestPart requestPart) {
        String partName = requestPart != null ? requestPart.name() : "";
        if (partName.isEmpty()) {
            partName = methodParam.getParameterName();
            if (partName == null) {
                throw new IllegalArgumentException("Request part name for argument type [" + methodParam.getNestedParameterType().getName() + "] not specified, and parameter name information not found in class file either.");
            }
        }
        return partName;
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver
    void closeStreamIfNecessary(InputStream body) {
        try {
            body.close();
        } catch (IOException e) {
        }
    }
}
