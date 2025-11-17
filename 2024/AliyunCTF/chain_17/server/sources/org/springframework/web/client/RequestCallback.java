package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/RequestCallback.class */
public interface RequestCallback {
    void doWithRequest(ClientHttpRequest request) throws IOException;
}
