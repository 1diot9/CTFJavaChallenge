package org.springframework.web.servlet.mvc.annotation;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/annotation/ModelAndViewResolver.class */
public interface ModelAndViewResolver {
    public static final ModelAndView UNRESOLVED = new ModelAndView();

    ModelAndView resolveModelAndView(Method handlerMethod, Class<?> handlerType, @Nullable Object returnValue, ExtendedModelMap implicitModel, NativeWebRequest webRequest);
}
