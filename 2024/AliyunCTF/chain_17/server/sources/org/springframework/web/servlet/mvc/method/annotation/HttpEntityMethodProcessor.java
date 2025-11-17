package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ProblemDetail;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/HttpEntityMethodProcessor.class */
public class HttpEntityMethodProcessor extends AbstractMessageConverterMethodProcessor {
    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, null, requestResponseBodyAdvice);
    }

    public HttpEntityMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return HttpEntity.class == parameter.getParameterType() || RequestEntity.class == parameter.getParameterType();
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public boolean supportsReturnType(MethodParameter returnType) {
        Class<?> type = returnType.getParameterType();
        return (HttpEntity.class.isAssignableFrom(type) && !RequestEntity.class.isAssignableFrom(type)) || ErrorResponse.class.isAssignableFrom(type) || ProblemDetail.class.isAssignableFrom(type);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws IOException, HttpMediaTypeNotSupportedException {
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        Type paramType = getHttpEntityType(parameter);
        if (paramType == null) {
            throw new IllegalArgumentException("HttpEntity parameter '" + parameter.getParameterName() + "' in method " + parameter.getMethod() + " is not parameterized");
        }
        Object body = readWithMessageConverters(webRequest, parameter, paramType);
        if (RequestEntity.class == parameter.getParameterType()) {
            return new RequestEntity(body, inputMessage.getHeaders(), inputMessage.getMethod(), inputMessage.getURI());
        }
        return new HttpEntity(body, inputMessage.getHeaders());
    }

    @Nullable
    private Type getHttpEntityType(MethodParameter parameter) {
        Assert.isAssignable(HttpEntity.class, parameter.getParameterType());
        Type parameterType = parameter.getGenericParameterType();
        if (parameterType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) parameterType;
            if (type.getActualTypeArguments().length != 1) {
                throw new IllegalArgumentException("Expected single generic parameter on '" + parameter.getParameterName() + "' in method " + parameter.getMethod());
            }
            return type.getActualTypeArguments()[0];
        }
        if (parameterType instanceof Class) {
            return Object.class;
        }
        return null;
    }

    @Override // org.springframework.web.method.support.HandlerMethodReturnValueHandler
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        HttpEntity<?> httpEntity;
        String location;
        mavContainer.setRequestHandled(true);
        if (returnValue == null) {
            return;
        }
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        if (returnValue instanceof ErrorResponse) {
            ErrorResponse response = (ErrorResponse) returnValue;
            httpEntity = new ResponseEntity<>(response.getBody(), response.getHeaders(), response.getStatusCode());
        } else if (returnValue instanceof ProblemDetail) {
            httpEntity = ResponseEntity.of((ProblemDetail) returnValue).build();
        } else {
            Assert.isInstanceOf(HttpEntity.class, returnValue);
            httpEntity = (HttpEntity) returnValue;
        }
        Object body = httpEntity.getBody();
        if (body instanceof ProblemDetail) {
            ProblemDetail detail = (ProblemDetail) body;
            if (detail.getInstance() == null) {
                URI path = URI.create(inputMessage.getServletRequest().getRequestURI());
                detail.setInstance(path);
            }
            if (this.logger.isWarnEnabled() && (httpEntity instanceof ResponseEntity)) {
                ResponseEntity<?> responseEntity = (ResponseEntity) httpEntity;
                if (responseEntity.getStatusCode().value() != detail.getStatus()) {
                    this.logger.warn(returnType.getExecutable().toGenericString() + " returned ResponseEntity: " + responseEntity + ", but its status doesn't match the ProblemDetail status: " + detail.getStatus());
                }
            }
        }
        HttpHeaders outputHeaders = outputMessage.getHeaders();
        HttpHeaders entityHeaders = httpEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            entityHeaders.forEach((key, value) -> {
                if (HttpHeaders.VARY.equals(key) && outputHeaders.containsKey(HttpHeaders.VARY)) {
                    List<String> values = getVaryRequestHeadersToAdd(outputHeaders, entityHeaders);
                    if (!values.isEmpty()) {
                        outputHeaders.setVary(values);
                        return;
                    }
                    return;
                }
                outputHeaders.put(key, (List<String>) value);
            });
        }
        if (httpEntity instanceof ResponseEntity) {
            int returnStatus = ((ResponseEntity) httpEntity).getStatusCode().value();
            outputMessage.getServletResponse().setStatus(returnStatus);
            if (returnStatus == 200) {
                HttpMethod method = inputMessage.getMethod();
                if ((HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method)) && isResourceNotModified(inputMessage, outputMessage)) {
                    outputMessage.flush();
                    return;
                }
            } else if (returnStatus / 100 == 3 && (location = outputHeaders.getFirst("location")) != null) {
                saveFlashAttributes(mavContainer, webRequest, location);
            }
        }
        writeWithMessageConverters(httpEntity.getBody(), returnType, inputMessage, outputMessage);
        outputMessage.flush();
    }

    private List<String> getVaryRequestHeadersToAdd(HttpHeaders responseHeaders, HttpHeaders entityHeaders) {
        List<String> entityHeadersVary = entityHeaders.getVary();
        List<String> vary = responseHeaders.get(HttpHeaders.VARY);
        if (vary != null) {
            List<String> result = new ArrayList<>(entityHeadersVary);
            for (String header : vary) {
                for (String existing : StringUtils.tokenizeToStringArray(header, ",")) {
                    if ("*".equals(existing)) {
                        return Collections.emptyList();
                    }
                    for (String value : entityHeadersVary) {
                        if (value.equalsIgnoreCase(existing)) {
                            result.remove(value);
                        }
                    }
                }
            }
            return result;
        }
        return entityHeadersVary;
    }

    private boolean isResourceNotModified(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request.getServletRequest(), response.getServletResponse());
        HttpHeaders responseHeaders = response.getHeaders();
        String etag = responseHeaders.getETag();
        long lastModifiedTimestamp = responseHeaders.getLastModified();
        if (request.getMethod() == HttpMethod.GET || request.getMethod() == HttpMethod.HEAD) {
            responseHeaders.remove(HttpHeaders.ETAG);
            responseHeaders.remove(HttpHeaders.LAST_MODIFIED);
        }
        return servletWebRequest.checkNotModified(etag, lastModifiedTimestamp);
    }

    private void saveFlashAttributes(ModelAndViewContainer mav, NativeWebRequest request, String location) {
        mav.setRedirectModelScenario(true);
        Cloneable model = mav.getModel();
        if (model instanceof RedirectAttributes) {
            RedirectAttributes redirectAttributes = (RedirectAttributes) model;
            Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();
            if (!CollectionUtils.isEmpty(flashAttributes)) {
                HttpServletRequest req = (HttpServletRequest) request.getNativeRequest(HttpServletRequest.class);
                HttpServletResponse res = (HttpServletResponse) request.getNativeResponse(HttpServletResponse.class);
                if (req != null) {
                    RequestContextUtils.getOutputFlashMap(req).putAll(flashAttributes);
                    if (res != null) {
                        RequestContextUtils.saveOutputFlashMap(location, req, res);
                    }
                }
            }
        }
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor
    protected Class<?> getReturnValueType(@Nullable Object returnValue, MethodParameter returnType) {
        if (returnValue != null) {
            return returnValue.getClass();
        }
        Type type = getHttpEntityType(returnType);
        return ResolvableType.forMethodParameter(returnType, type != null ? type : Object.class).toClass();
    }
}
