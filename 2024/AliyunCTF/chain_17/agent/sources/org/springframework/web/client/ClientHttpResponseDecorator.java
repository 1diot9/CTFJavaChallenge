package org.springframework.web.client;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/ClientHttpResponseDecorator.class */
public class ClientHttpResponseDecorator implements ClientHttpResponse {
    private final ClientHttpResponse delegate;

    public ClientHttpResponseDecorator(ClientHttpResponse delegate) {
        Assert.notNull(delegate, "ClientHttpResponse delegate is required");
        this.delegate = delegate;
    }

    public ClientHttpResponse getDelegate() {
        return this.delegate;
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() throws IOException {
        return this.delegate.getStatusCode();
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() throws IOException {
        return this.delegate.getStatusText();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        return this.delegate.getBody();
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.delegate.close();
    }
}
