package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBody.class */
public interface StreamingResponseBody {
    void writeTo(OutputStream outputStream) throws IOException;
}
