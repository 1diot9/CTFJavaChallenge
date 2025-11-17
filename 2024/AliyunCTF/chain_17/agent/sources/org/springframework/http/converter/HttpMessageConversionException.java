package org.springframework.http.converter;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/HttpMessageConversionException.class */
public class HttpMessageConversionException extends NestedRuntimeException {
    public HttpMessageConversionException(String msg) {
        super(msg);
    }

    public HttpMessageConversionException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
