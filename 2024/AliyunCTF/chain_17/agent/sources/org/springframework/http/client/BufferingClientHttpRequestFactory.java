package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/BufferingClientHttpRequestFactory.class */
public class BufferingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {
    public BufferingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override // org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper
    protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) throws IOException {
        ClientHttpRequest request = requestFactory.createRequest(uri, httpMethod);
        if (shouldBuffer(uri, httpMethod)) {
            return new BufferingClientHttpRequestWrapper(request);
        }
        return request;
    }

    protected boolean shouldBuffer(URI uri, HttpMethod httpMethod) {
        return true;
    }
}
