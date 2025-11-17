package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated(since = "6.1", forRemoval = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/OkHttp3ClientHttpResponse.class */
class OkHttp3ClientHttpResponse implements ClientHttpResponse {
    private final Response response;

    @Nullable
    private volatile HttpHeaders headers;

    public OkHttp3ClientHttpResponse(Response response) {
        Assert.notNull(response, "Response must not be null");
        this.response = response;
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(this.response.code());
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() {
        return this.response.message();
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        ResponseBody body = this.response.body();
        return body != null ? body.byteStream() : InputStream.nullInputStream();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        HttpHeaders headers = this.headers;
        if (headers == null) {
            headers = new HttpHeaders();
            for (String headerName : this.response.headers().names()) {
                for (String headerValue : this.response.headers(headerName)) {
                    headers.add(headerName, headerValue);
                }
            }
            this.headers = headers;
        }
        return headers;
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        ResponseBody body = this.response.body();
        if (body != null) {
            body.close();
        }
    }
}
