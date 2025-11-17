package org.springframework.http.client.reactive;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ClientHttpResponseDecorator.class */
public class ClientHttpResponseDecorator implements ClientHttpResponse {
    private final ClientHttpResponse delegate;

    public ClientHttpResponseDecorator(ClientHttpResponse delegate) {
        Assert.notNull(delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ClientHttpResponse getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public String getId() {
        return this.delegate.getId();
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return this.delegate.getStatusCode();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return this.delegate.getCookies();
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return this.delegate.getBody();
    }

    public String toString() {
        return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
    }
}
