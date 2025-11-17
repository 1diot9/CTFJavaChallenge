package org.springframework.web.client;

import org.springframework.core.NestedRuntimeException;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RestClientException.class */
public class RestClientException extends NestedRuntimeException {
    private static final long serialVersionUID = -4084444984163796577L;

    public RestClientException(String msg) {
        super(msg);
    }

    public RestClientException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
