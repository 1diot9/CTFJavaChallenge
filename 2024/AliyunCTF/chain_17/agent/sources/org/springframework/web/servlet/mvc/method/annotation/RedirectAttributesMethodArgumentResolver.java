package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RedirectAttributesMethodArgumentResolver.class */
public class RedirectAttributesMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return RedirectAttributes.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        ModelMap redirectAttributes;
        Assert.state(mavContainer != null, "RedirectAttributes argument only supported on regular handler methods");
        if (binderFactory != null) {
            DataBinder dataBinder = binderFactory.createBinder(webRequest, null, DataBinder.DEFAULT_OBJECT_NAME);
            redirectAttributes = new RedirectAttributesModelMap(dataBinder);
        } else {
            redirectAttributes = new RedirectAttributesModelMap();
        }
        mavContainer.setRedirectModel(redirectAttributes);
        return redirectAttributes;
    }
}
