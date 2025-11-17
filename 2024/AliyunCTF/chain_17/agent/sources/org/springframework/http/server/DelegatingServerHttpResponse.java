package org.springframework.http.server;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/DelegatingServerHttpResponse.class */
public class DelegatingServerHttpResponse implements ServerHttpResponse {
    private final ServerHttpResponse delegate;

    public DelegatingServerHttpResponse(ServerHttpResponse delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    public ServerHttpResponse getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.http.server.ServerHttpResponse
    public void setStatusCode(HttpStatusCode status) {
        this.delegate.setStatusCode(status);
    }

    @Override // org.springframework.http.server.ServerHttpResponse, java.io.Flushable
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override // org.springframework.http.server.ServerHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.delegate.close();
    }

    @Override // org.springframework.http.HttpOutputMessage
    public OutputStream getBody() throws IOException {
        return this.delegate.getBody();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }
}
