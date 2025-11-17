package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.support.Netty4HeadersAdapter;
import org.springframework.lang.Nullable;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ReactorNettyClientResponse.class */
final class ReactorNettyClientResponse implements ClientHttpResponse {
    private final HttpClientResponse response;
    private final Connection connection;
    private final HttpHeaders headers;
    private final Duration readTimeout;

    @Nullable
    private volatile InputStream body;

    public ReactorNettyClientResponse(HttpClientResponse response, Connection connection, Duration readTimeout) {
        this.response = response;
        this.connection = connection;
        this.readTimeout = readTimeout;
        this.headers = HttpHeaders.readOnlyHttpHeaders(new Netty4HeadersAdapter(response.responseHeaders()));
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(this.response.status().code());
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() {
        return this.response.status().reasonPhrase();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        InputStream body = this.body;
        if (body != null) {
            return body;
        }
        InputStream body2 = (InputStream) this.connection.inbound().receive().aggregate().asInputStream().block(this.readTimeout);
        if (body2 == null) {
            throw new IOException("Could not receive body");
        }
        this.body = body2;
        return body2;
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.connection.dispose();
    }
}
