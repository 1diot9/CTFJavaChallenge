package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ClientHttpRequestDecorator.class */
public class ClientHttpRequestDecorator implements ClientHttpRequest {
    private final ClientHttpRequest delegate;

    public ClientHttpRequestDecorator(ClientHttpRequest delegate) {
        Assert.notNull(delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ClientHttpRequest getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        return this.delegate.getMethod();
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public URI getURI() {
        return this.delegate.getURI();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public MultiValueMap<String, HttpCookie> getCookies() {
        return this.delegate.getCookies();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return this.delegate.bufferFactory();
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.delegate.getNativeRequest();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        this.delegate.beforeCommit(action);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public boolean isCommitted() {
        return this.delegate.isCommitted();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return this.delegate.writeWith(body);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return this.delegate.writeAndFlushWith(body);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return this.delegate.setComplete();
    }

    public String toString() {
        return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
    }
}
