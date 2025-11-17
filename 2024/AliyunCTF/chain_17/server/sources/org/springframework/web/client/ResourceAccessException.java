package org.springframework.web.client;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/ResourceAccessException.class */
public class ResourceAccessException extends RestClientException {
    private static final long serialVersionUID = -8513182514355844870L;

    public ResourceAccessException(String msg) {
        super(msg);
    }

    public ResourceAccessException(String msg, IOException ex) {
        super(msg, ex);
    }
}
