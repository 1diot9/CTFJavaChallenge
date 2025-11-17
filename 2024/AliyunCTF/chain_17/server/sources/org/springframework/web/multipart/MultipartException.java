package org.springframework.web.multipart;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/MultipartException.class */
public class MultipartException extends NestedRuntimeException {
    public MultipartException(String msg) {
        super(msg);
    }

    public MultipartException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
