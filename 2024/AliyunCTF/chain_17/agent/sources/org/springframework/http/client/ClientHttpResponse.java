package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatusCode;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ClientHttpResponse.class */
public interface ClientHttpResponse extends HttpInputMessage, Closeable {
    HttpStatusCode getStatusCode() throws IOException;

    String getStatusText() throws IOException;

    void close();

    @Deprecated(since = "6.0", forRemoval = true)
    default int getRawStatusCode() throws IOException {
        return getStatusCode().value();
    }
}
