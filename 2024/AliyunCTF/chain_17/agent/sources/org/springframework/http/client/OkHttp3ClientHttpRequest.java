package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

@Deprecated(since = "6.1", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/OkHttp3ClientHttpRequest.class */
class OkHttp3ClientHttpRequest extends AbstractStreamingClientHttpRequest {
    private final OkHttpClient client;
    private final URI uri;
    private final HttpMethod method;

    public OkHttp3ClientHttpRequest(OkHttpClient client, URI uri, HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.client.AbstractStreamingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, @Nullable StreamingHttpOutputMessage.Body body) throws IOException {
        RequestBody requestBody;
        if (body != null) {
            requestBody = new BodyRequestBody(headers, body);
        } else if (okhttp3.internal.http.HttpMethod.requiresRequestBody(getMethod().name())) {
            String header = headers.getFirst(HttpHeaders.CONTENT_TYPE);
            MediaType contentType = header != null ? MediaType.parse(header) : null;
            requestBody = RequestBody.create(contentType, new byte[0]);
        } else {
            requestBody = null;
        }
        Request.Builder builder = new Request.Builder().url(this.uri.toURL());
        builder.method(this.method.name(), requestBody);
        headers.forEach((headerName, headerValues) -> {
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                builder.addHeader(headerName, headerValue);
            }
        });
        Request request = builder.build();
        return new OkHttp3ClientHttpResponse(this.client.newCall(request).execute());
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/OkHttp3ClientHttpRequest$BodyRequestBody.class */
    private static class BodyRequestBody extends RequestBody {
        private final HttpHeaders headers;
        private final StreamingHttpOutputMessage.Body body;

        public BodyRequestBody(HttpHeaders headers, StreamingHttpOutputMessage.Body body) {
            this.headers = headers;
            this.body = body;
        }

        public long contentLength() {
            return this.headers.getContentLength();
        }

        @Nullable
        public MediaType contentType() {
            String contentType = this.headers.getFirst(HttpHeaders.CONTENT_TYPE);
            if (StringUtils.hasText(contentType)) {
                return MediaType.parse(contentType);
            }
            return null;
        }

        public void writeTo(BufferedSink sink) throws IOException {
            this.body.writeTo(sink.outputStream());
        }

        public boolean isOneShot() {
            return !this.body.repeatable();
        }
    }
}
