package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.support.HttpComponentsHeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/HttpComponentsClientHttpResponse.class */
final class HttpComponentsClientHttpResponse implements ClientHttpResponse {
    private final ClassicHttpResponse httpResponse;

    @Nullable
    private HttpHeaders headers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpComponentsClientHttpResponse(ClassicHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(this.httpResponse.getCode());
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() {
        return this.httpResponse.getReasonPhrase();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            MultiValueMap<String, String> adapter = new HttpComponentsHeadersAdapter(this.httpResponse);
            this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
        }
        return this.headers;
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        HttpEntity entity = this.httpResponse.getEntity();
        return entity != null ? entity.getContent() : InputStream.nullInputStream();
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            try {
                EntityUtils.consume(this.httpResponse.getEntity());
                this.httpResponse.close();
            } catch (Throwable th) {
                this.httpResponse.close();
                throw th;
            }
        } catch (IOException e) {
        }
    }
}
