package org.springframework.web.method.annotation;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/MethodArgumentConversionNotSupportedException.class */
public class MethodArgumentConversionNotSupportedException extends ConversionNotSupportedException {
    private final String name;
    private final MethodParameter parameter;

    public MethodArgumentConversionNotSupportedException(@Nullable Object value, @Nullable Class<?> requiredType, String name, MethodParameter param, Throwable cause) {
        super(value, requiredType, cause);
        this.name = name;
        this.parameter = param;
        initPropertyName(name);
    }

    public String getName() {
        return this.name;
    }

    public MethodParameter getParameter() {
        return this.parameter;
    }
}
