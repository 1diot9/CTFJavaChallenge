package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServerHttpRequestDecorator.class */
public class ServerHttpRequestDecorator implements ServerHttpRequest {
    private final ServerHttpRequest delegate;

    public ServerHttpRequestDecorator(ServerHttpRequest delegate) {
        Assert.notNull(delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ServerHttpRequest getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public String getId() {
        return getDelegate().getId();
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return getDelegate().getMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return getDelegate().getURI();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public RequestPath getPath() {
        return getDelegate().getPath();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, String> getQueryParams() {
        return getDelegate().getQueryParams();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return getDelegate().getHeaders();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public MultiValueMap<String, HttpCookie> getCookies() {
        return getDelegate().getCookies();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return getDelegate().getLocalAddress();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return getDelegate().getRemoteAddress();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public SslInfo getSslInfo() {
        return getDelegate().getSslInfo();
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return getDelegate().getBody();
    }

    public static <T> T getNativeRequest(ServerHttpRequest serverHttpRequest) {
        if (serverHttpRequest instanceof AbstractServerHttpRequest) {
            return (T) ((AbstractServerHttpRequest) serverHttpRequest).getNativeRequest();
        }
        if (serverHttpRequest instanceof ServerHttpRequestDecorator) {
            return (T) getNativeRequest(((ServerHttpRequestDecorator) serverHttpRequest).getDelegate());
        }
        throw new IllegalArgumentException("Can't find native request in " + serverHttpRequest.getClass().getName());
    }

    public String toString() {
        return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
    }
}
