package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/AbstractClientHttpRequest.class */
public abstract class AbstractClientHttpRequest implements ClientHttpRequest {
    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;

    @Nullable
    private HttpHeaders readOnlyHeaders;

    protected abstract OutputStream getBodyInternal(HttpHeaders headers) throws IOException;

    protected abstract ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException;

    @Override // org.springframework.http.HttpMessage
    public final HttpHeaders getHeaders() {
        if (this.readOnlyHeaders != null) {
            return this.readOnlyHeaders;
        }
        if (this.executed) {
            this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
            return this.readOnlyHeaders;
        }
        return this.headers;
    }

    @Override // org.springframework.http.HttpOutputMessage
    public final OutputStream getBody() throws IOException {
        assertNotExecuted();
        return getBodyInternal(this.headers);
    }

    @Override // org.springframework.http.client.ClientHttpRequest
    public final ClientHttpResponse execute() throws IOException {
        assertNotExecuted();
        ClientHttpResponse result = executeInternal(this.headers);
        this.executed = true;
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }
}
