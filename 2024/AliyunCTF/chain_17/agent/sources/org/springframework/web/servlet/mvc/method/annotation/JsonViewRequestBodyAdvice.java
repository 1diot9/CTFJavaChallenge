package org.springframework.web.servlet.mvc.method.annotation;

import com.fasterxml.jackson.annotation.JsonView;
import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/JsonViewRequestBodyAdvice.class */
public class JsonViewRequestBodyAdvice extends RequestBodyAdviceAdapter {
    @Override // org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType) && methodParameter.getParameterAnnotation(JsonView.class) != null;
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter, org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> selectedConverterType) throws IOException {
        JsonView ann = (JsonView) methodParameter.getParameterAnnotation(JsonView.class);
        Assert.state(ann != null, "No JsonView annotation");
        Class<?>[] classes = ann.value();
        if (classes.length != 1) {
            throw new IllegalArgumentException("@JsonView only supported for request body advice with exactly 1 class argument: " + methodParameter);
        }
        return new MappingJacksonInputMessage(inputMessage.getBody(), inputMessage.getHeaders(), classes[0]);
    }
}
