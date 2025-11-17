package org.springframework.http.server;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatusCode;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/ServerHttpResponse.class */
public interface ServerHttpResponse extends HttpOutputMessage, Flushable, Closeable {
    void setStatusCode(HttpStatusCode status);

    void flush() throws IOException;

    void close();
}
