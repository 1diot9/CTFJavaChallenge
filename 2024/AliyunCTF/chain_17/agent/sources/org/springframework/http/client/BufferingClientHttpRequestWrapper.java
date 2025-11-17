package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.StreamUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/BufferingClientHttpRequestWrapper.class */
final class BufferingClientHttpRequestWrapper extends AbstractBufferingClientHttpRequest {
    private final ClientHttpRequest request;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BufferingClientHttpRequestWrapper(ClientHttpRequest request) {
        this.request = request;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.request.getMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.request.getURI();
    }

    @Override // org.springframework.http.client.AbstractBufferingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        this.request.getHeaders().putAll(headers);
        ClientHttpRequest clientHttpRequest = this.request;
        if (clientHttpRequest instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingHttpOutputMessage = (StreamingHttpOutputMessage) clientHttpRequest;
            streamingHttpOutputMessage.setBody(new StreamingHttpOutputMessage.Body() { // from class: org.springframework.http.client.BufferingClientHttpRequestWrapper.1
                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public void writeTo(OutputStream outputStream) throws IOException {
                    StreamUtils.copy(bufferedOutput, outputStream);
                }

                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public boolean repeatable() {
                    return true;
                }
            });
        } else {
            StreamUtils.copy(bufferedOutput, this.request.getBody());
        }
        ClientHttpResponse response = this.request.execute();
        return new BufferingClientHttpResponseWrapper(response);
    }
}
