package org.springframework.web.bind.support;

import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.BindParam;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/BindParamNameResolver.class */
public final class BindParamNameResolver implements DataBinder.NameResolver {
    @Override // org.springframework.validation.DataBinder.NameResolver
    public String resolveName(MethodParameter parameter) {
        BindParam bindParam = (BindParam) parameter.getParameterAnnotation(BindParam.class);
        if (bindParam != null && StringUtils.hasText(bindParam.value())) {
            return bindParam.value();
        }
        return null;
    }
}
