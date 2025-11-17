package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletRequest;
import java.util.Collections;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletModelAttributeMethodProcessor.class */
public class ServletModelAttributeMethodProcessor extends ModelAttributeMethodProcessor {
    public ServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.method.annotation.ModelAttributeMethodProcessor
    public final Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        Object attribute;
        String value = getRequestValueForAttribute(attributeName, request);
        if (value != null && (attribute = createAttributeFromRequestValue(value, attributeName, parameter, binderFactory, request)) != null) {
            return attribute;
        }
        return super.createAttribute(attributeName, parameter, binderFactory, request);
    }

    @Nullable
    protected String getRequestValueForAttribute(String attributeName, NativeWebRequest request) {
        Map<String, String> variables = getUriTemplateVariables(request);
        String variableValue = variables.get(attributeName);
        if (StringUtils.hasText(variableValue)) {
            return variableValue;
        }
        String parameterValue = request.getParameter(attributeName);
        if (StringUtils.hasText(parameterValue)) {
            return parameterValue;
        }
        return null;
    }

    protected final Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
        Map<String, String> variables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        return variables != null ? variables : Collections.emptyMap();
    }

    @Nullable
    protected Object createAttributeFromRequestValue(String sourceValue, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        DataBinder binder = binderFactory.createBinder(request, null, attributeName);
        ConversionService conversionService = binder.getConversionService();
        if (conversionService != null) {
            TypeDescriptor source = TypeDescriptor.valueOf(String.class);
            TypeDescriptor target = new TypeDescriptor(parameter);
            if (conversionService.canConvert(source, target)) {
                return binder.convertIfNecessary(sourceValue, parameter.getParameterType(), parameter);
            }
            return null;
        }
        return null;
    }

    @Override // org.springframework.web.method.annotation.ModelAttributeMethodProcessor
    protected void constructAttribute(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = (ServletRequest) request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
        servletBinder.construct(servletRequest);
    }

    @Override // org.springframework.web.method.annotation.ModelAttributeMethodProcessor
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = (ServletRequest) request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
        servletBinder.bind(servletRequest);
    }
}
