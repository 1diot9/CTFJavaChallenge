package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.jetty.client.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.support.JettyHeadersAdapter;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/JettyClientHttpResponse.class */
class JettyClientHttpResponse implements ClientHttpResponse {
    private final Response response;
    private final InputStream body;
    private final HttpHeaders headers;

    public JettyClientHttpResponse(Response response, InputStream inputStream) {
        this.response = response;
        this.body = inputStream;
        MultiValueMap<String, String> headers = new JettyHeadersAdapter(response.getHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(this.response.getStatus());
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() throws IOException {
        return this.response.getReason();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        return this.body;
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            this.body.close();
        } catch (IOException e) {
        }
    }
}
