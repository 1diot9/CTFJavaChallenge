package org.springframework.web.method.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/AbstractWebArgumentResolverAdapter.class */
public abstract class AbstractWebArgumentResolverAdapter implements HandlerMethodArgumentResolver {
    private final Log logger = LogFactory.getLog(getClass());
    private final WebArgumentResolver adaptee;

    protected abstract NativeWebRequest getWebRequest();

    public AbstractWebArgumentResolverAdapter(WebArgumentResolver adaptee) {
        Assert.notNull(adaptee, "'adaptee' must not be null");
        this.adaptee = adaptee;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        try {
            NativeWebRequest webRequest = getWebRequest();
            Object result = this.adaptee.resolveArgument(parameter, webRequest);
            if (result == WebArgumentResolver.UNRESOLVED) {
                return false;
            }
            return ClassUtils.isAssignableValue(parameter.getParameterType(), result);
        } catch (Exception ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Error in checking support for parameter [" + parameter + "]: " + ex.getMessage());
                return false;
            }
            return false;
        }
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        Object result = this.adaptee.resolveArgument(parameter, webRequest);
        if (result == WebArgumentResolver.UNRESOLVED || !ClassUtils.isAssignableValue(paramType, result)) {
            throw new IllegalStateException("Standard argument type [" + paramType.getName() + "] in method " + parameter.getMethod() + "resolved to incompatible value of type [" + (result != null ? result.getClass() : null) + "]. Consider declaring the argument type in a less specific fashion.");
        }
        return result;
    }
}
