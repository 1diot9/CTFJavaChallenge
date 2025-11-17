package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletRequest;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ExtendedServletRequestDataBinder.class */
public class ExtendedServletRequestDataBinder extends ServletRequestDataBinder {
    public ExtendedServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public ExtendedServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    @Override // org.springframework.web.bind.ServletRequestDataBinder
    protected ServletRequestDataBinder.ServletRequestValueResolver createValueResolver(ServletRequest request) {
        return new ExtendedServletRequestValueResolver(request, this);
    }

    @Override // org.springframework.web.bind.ServletRequestDataBinder
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        Map<String, String> uriVars = getUriVars(request);
        if (uriVars != null) {
            uriVars.forEach((name, value) -> {
                if (mpvs.contains(name)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("URI variable '" + name + "' overridden by request bind value.");
                        return;
                    }
                    return;
                }
                mpvs.addPropertyValue(name, value);
            });
        }
    }

    @Nullable
    private static Map<String, String> getUriVars(ServletRequest request) {
        String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        return (Map) request.getAttribute(attr);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ExtendedServletRequestDataBinder$ExtendedServletRequestValueResolver.class */
    private static class ExtendedServletRequestValueResolver extends ServletRequestDataBinder.ServletRequestValueResolver {
        ExtendedServletRequestValueResolver(ServletRequest request, WebDataBinder dataBinder) {
            super(request, dataBinder);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.bind.ServletRequestDataBinder.ServletRequestValueResolver
        public Object getRequestParameter(String name, Class<?> type) {
            Map<String, String> uriVars;
            Object value = super.getRequestParameter(name, type);
            if (value == null && (uriVars = ExtendedServletRequestDataBinder.getUriVars(getRequest())) != null) {
                value = uriVars.get(name);
            }
            return value;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.bind.ServletRequestDataBinder.ServletRequestValueResolver
        public Set<String> initParameterNames(ServletRequest request) {
            Set<String> set = super.initParameterNames(request);
            Map<String, String> uriVars = ExtendedServletRequestDataBinder.getUriVars(getRequest());
            if (uriVars != null) {
                set.addAll(uriVars.keySet());
            }
            return set;
        }
    }
}
