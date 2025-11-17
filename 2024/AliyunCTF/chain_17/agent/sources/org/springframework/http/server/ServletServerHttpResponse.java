package org.springframework.http.server;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/ServletServerHttpResponse.class */
public class ServletServerHttpResponse implements ServerHttpResponse {
    private final HttpServletResponse servletResponse;
    private final HttpHeaders headers;
    private boolean headersWritten = false;
    private boolean bodyUsed = false;

    @Nullable
    private HttpHeaders readOnlyHeaders;

    public ServletServerHttpResponse(HttpServletResponse servletResponse) {
        Assert.notNull(servletResponse, "HttpServletResponse must not be null");
        this.servletResponse = servletResponse;
        this.headers = new ServletResponseHttpHeaders();
    }

    public HttpServletResponse getServletResponse() {
        return this.servletResponse;
    }

    @Override // org.springframework.http.server.ServerHttpResponse
    public void setStatusCode(HttpStatusCode status) {
        Assert.notNull(status, "HttpStatusCode must not be null");
        this.servletResponse.setStatus(status.value());
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        if (this.readOnlyHeaders != null) {
            return this.readOnlyHeaders;
        }
        if (this.headersWritten) {
            this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
            return this.readOnlyHeaders;
        }
        return this.headers;
    }

    @Override // org.springframework.http.HttpOutputMessage
    public OutputStream getBody() throws IOException {
        this.bodyUsed = true;
        writeHeaders();
        return this.servletResponse.getOutputStream();
    }

    @Override // org.springframework.http.server.ServerHttpResponse, java.io.Flushable
    public void flush() throws IOException {
        writeHeaders();
        if (this.bodyUsed) {
            this.servletResponse.flushBuffer();
        }
    }

    @Override // org.springframework.http.server.ServerHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        writeHeaders();
    }

    private void writeHeaders() {
        if (!this.headersWritten) {
            getHeaders().forEach((headerName, headerValues) -> {
                Iterator it = headerValues.iterator();
                while (it.hasNext()) {
                    String headerValue = (String) it.next();
                    this.servletResponse.addHeader(headerName, headerValue);
                }
            });
            if (this.servletResponse.getContentType() == null && this.headers.getContentType() != null) {
                this.servletResponse.setContentType(this.headers.getContentType().toString());
            }
            if (this.servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers.getContentType().getCharset() != null) {
                this.servletResponse.setCharacterEncoding(this.headers.getContentType().getCharset().name());
            }
            long contentLength = getHeaders().getContentLength();
            if (contentLength != -1) {
                this.servletResponse.setContentLengthLong(contentLength);
            }
            this.headersWritten = true;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/ServletServerHttpResponse$ServletResponseHttpHeaders.class */
    private class ServletResponseHttpHeaders extends HttpHeaders {
        private static final long serialVersionUID = 3410708522401046302L;

        private ServletResponseHttpHeaders() {
        }

        @Override // org.springframework.http.HttpHeaders, java.util.Map
        public boolean containsKey(Object key) {
            return super.containsKey(key) || get(key) != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
        @Nullable
        public String getFirst(String headerName) {
            if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
                String value = super.getFirst(headerName);
                return value != null ? value : ServletServerHttpResponse.this.servletResponse.getHeader(headerName);
            }
            String value2 = ServletServerHttpResponse.this.servletResponse.getHeader(headerName);
            return value2 != null ? value2 : super.getFirst(headerName);
        }

        @Override // org.springframework.http.HttpHeaders, java.util.Map
        public List<String> get(Object key) {
            Assert.isInstanceOf((Class<?>) String.class, key, "Key must be a String-based header name");
            String headerName = (String) key;
            if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
                return Collections.singletonList(getFirst(headerName));
            }
            Collection<? extends String> values1 = ServletServerHttpResponse.this.servletResponse.getHeaders(headerName);
            if (ServletServerHttpResponse.this.headersWritten) {
                return new ArrayList(values1);
            }
            boolean isEmpty1 = CollectionUtils.isEmpty(values1);
            Collection<? extends String> values2 = super.get(key);
            boolean isEmpty2 = CollectionUtils.isEmpty(values2);
            if (isEmpty1 && isEmpty2) {
                return null;
            }
            List<String> values = new ArrayList<>();
            if (!isEmpty1) {
                values.addAll(values1);
            }
            if (!isEmpty2) {
                values.addAll(values2);
            }
            return values;
        }
    }
}
