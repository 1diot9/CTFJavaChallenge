package org.springframework.http.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/BufferingClientHttpResponseWrapper.class */
final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
    private final ClientHttpResponse response;

    @Nullable
    private byte[] body;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
        this.response = response;
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() throws IOException {
        return this.response.getStatusCode();
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() throws IOException {
        return this.response.getStatusText();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.response.getHeaders();
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        if (this.body == null) {
            this.body = StreamUtils.copyToByteArray(this.response.getBody());
        }
        return new ByteArrayInputStream(this.body);
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.response.close();
    }
}
