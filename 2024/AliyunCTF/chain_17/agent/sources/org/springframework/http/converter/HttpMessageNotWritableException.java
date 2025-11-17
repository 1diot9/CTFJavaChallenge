package org.springframework.http.converter;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/HttpMessageNotWritableException.class */
public class HttpMessageNotWritableException extends HttpMessageConversionException {
    public HttpMessageNotWritableException(String msg) {
        super(msg);
    }

    public HttpMessageNotWritableException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
