package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/ResponseExtractor.class */
public interface ResponseExtractor<T> {
    @Nullable
    T extractData(ClientHttpResponse response) throws IOException;
}
