package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/JdkClientHttpResponse.class */
class JdkClientHttpResponse implements ClientHttpResponse {
    private final HttpResponse<InputStream> response;
    private final HttpHeaders headers;
    private final InputStream body;

    public JdkClientHttpResponse(HttpResponse<InputStream> response) {
        this.response = response;
        this.headers = adaptHeaders(response);
        InputStream inputStream = (InputStream) response.body();
        this.body = inputStream != null ? inputStream : InputStream.nullInputStream();
    }

    private static HttpHeaders adaptHeaders(HttpResponse<?> response) {
        Map<? extends String, ? extends String> rawHeaders = response.headers().map();
        Map<String, List<String>> map = new LinkedCaseInsensitiveMap<>(rawHeaders.size(), Locale.ENGLISH);
        MultiValueMap<String, String> multiValueMap = CollectionUtils.toMultiValueMap(map);
        multiValueMap.putAll(rawHeaders);
        return HttpHeaders.readOnlyHttpHeaders(multiValueMap);
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(this.response.statusCode());
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() {
        HttpStatusCode statusCode = getStatusCode();
        if (statusCode instanceof HttpStatus) {
            HttpStatus status = (HttpStatus) statusCode;
            return status.getReasonPhrase();
        }
        return "";
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
            try {
                StreamUtils.drain(this.body);
                this.body.close();
            } catch (Throwable th) {
                this.body.close();
                throw th;
            }
        } catch (IOException e) {
        }
    }
}
