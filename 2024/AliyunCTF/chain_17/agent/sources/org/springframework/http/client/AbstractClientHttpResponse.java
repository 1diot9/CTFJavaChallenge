package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpStatusCode;

@Deprecated(since = "6.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/AbstractClientHttpResponse.class */
public abstract class AbstractClientHttpResponse implements ClientHttpResponse {
    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(getRawStatusCode());
    }
}
