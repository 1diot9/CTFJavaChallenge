package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/HttpComponentsClientHttpRequest.class */
final class HttpComponentsClientHttpRequest extends AbstractStreamingClientHttpRequest {
    private final HttpClient httpClient;
    private final ClassicHttpRequest httpRequest;
    private final HttpContext httpContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpComponentsClientHttpRequest(HttpClient client, ClassicHttpRequest request, HttpContext context) {
        this.httpClient = client;
        this.httpRequest = request;
        this.httpContext = context;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.httpRequest.getMethod());
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        try {
            return this.httpRequest.getUri();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override // org.springframework.http.client.AbstractStreamingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, @Nullable StreamingHttpOutputMessage.Body body) throws IOException {
        addHeaders(this.httpRequest, headers);
        if (body != null) {
            HttpEntity requestEntity = new BodyEntity(headers, body);
            this.httpRequest.setEntity(requestEntity);
        }
        ClassicHttpResponse httpResponse = this.httpClient.executeOpen((HttpHost) null, this.httpRequest, this.httpContext);
        return new HttpComponentsClientHttpResponse(httpResponse);
    }

    static void addHeaders(ClassicHttpRequest httpRequest, HttpHeaders headers) {
        headers.forEach((headerName, headerValues) -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {
                String headerValue = StringUtils.collectionToDelimitedString(headerValues, "; ");
                httpRequest.addHeader(headerName, headerValue);
            } else if (!HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(headerName) && !"Transfer-Encoding".equalsIgnoreCase(headerName)) {
                Iterator it = headerValues.iterator();
                while (it.hasNext()) {
                    String headerValue2 = (String) it.next();
                    httpRequest.addHeader(headerName, headerValue2);
                }
            }
        });
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/HttpComponentsClientHttpRequest$BodyEntity.class */
    private static class BodyEntity implements HttpEntity {
        private final HttpHeaders headers;
        private final StreamingHttpOutputMessage.Body body;

        public BodyEntity(HttpHeaders headers, StreamingHttpOutputMessage.Body body) {
            this.headers = headers;
            this.body = body;
        }

        public long getContentLength() {
            return this.headers.getContentLength();
        }

        @Nullable
        public String getContentType() {
            return this.headers.getFirst(HttpHeaders.CONTENT_TYPE);
        }

        public InputStream getContent() {
            throw new UnsupportedOperationException();
        }

        public void writeTo(OutputStream outStream) throws IOException {
            this.body.writeTo(outStream);
        }

        public boolean isRepeatable() {
            return this.body.repeatable();
        }

        public boolean isStreaming() {
            return false;
        }

        @Nullable
        public Supplier<List<? extends Header>> getTrailers() {
            return null;
        }

        @Nullable
        public String getContentEncoding() {
            return this.headers.getFirst(HttpHeaders.CONTENT_ENCODING);
        }

        public boolean isChunked() {
            return false;
        }

        @Nullable
        public Set<String> getTrailerNames() {
            return null;
        }

        public void close() {
        }
    }
}
