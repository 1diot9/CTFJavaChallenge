package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/DefaultServerHttpRequestBuilder.class */
public class DefaultServerHttpRequestBuilder implements ServerHttpRequest.Builder {
    private URI uri;
    private final HttpHeaders headers;
    private HttpMethod httpMethod;

    @Nullable
    private String uriPath;

    @Nullable
    private String contextPath;

    @Nullable
    private SslInfo sslInfo;

    @Nullable
    private InetSocketAddress remoteAddress;
    private final Flux<DataBuffer> body;
    private final ServerHttpRequest originalRequest;

    public DefaultServerHttpRequestBuilder(ServerHttpRequest original) {
        Assert.notNull(original, "ServerHttpRequest is required");
        this.uri = original.getURI();
        this.headers = HttpHeaders.writableHttpHeaders(original.getHeaders());
        this.httpMethod = original.getMethod();
        this.contextPath = original.getPath().contextPath().value();
        this.remoteAddress = original.getRemoteAddress();
        this.body = original.getBody();
        this.originalRequest = original;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder method(HttpMethod httpMethod) {
        Assert.notNull(httpMethod, "HttpMethod must not be null");
        this.httpMethod = httpMethod;
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder uri(URI uri) {
        this.uri = uri;
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder path(String path) {
        Assert.isTrue(path.startsWith("/"), (Supplier<String>) () -> {
            return "The path does not have a leading slash: " + path;
        });
        this.uriPath = path;
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder contextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder header(String headerName, String... headerValues) {
        this.headers.put(headerName, Arrays.asList(headerValues));
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder headers(Consumer<HttpHeaders> headersConsumer) {
        Assert.notNull(headersConsumer, "'headersConsumer' must not be null");
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder sslInfo(SslInfo sslInfo) {
        this.sslInfo = sslInfo;
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest.Builder remoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest.Builder
    public ServerHttpRequest build() {
        return new MutatedServerHttpRequest(getUriToUse(), this.contextPath, this.httpMethod, this.sslInfo, this.remoteAddress, this.headers, this.body, this.originalRequest);
    }

    private URI getUriToUse() {
        if (this.uriPath == null) {
            return this.uri;
        }
        StringBuilder uriBuilder = new StringBuilder();
        if (this.uri.getScheme() != null) {
            uriBuilder.append(this.uri.getScheme()).append(':');
        }
        if (this.uri.getRawUserInfo() != null || this.uri.getHost() != null) {
            uriBuilder.append("//");
            if (this.uri.getRawUserInfo() != null) {
                uriBuilder.append(this.uri.getRawUserInfo()).append('@');
            }
            if (this.uri.getHost() != null) {
                uriBuilder.append(this.uri.getHost());
            }
            if (this.uri.getPort() != -1) {
                uriBuilder.append(':').append(this.uri.getPort());
            }
        }
        if (StringUtils.hasLength(this.uriPath)) {
            uriBuilder.append(this.uriPath);
        }
        if (this.uri.getRawQuery() != null) {
            uriBuilder.append('?').append(this.uri.getRawQuery());
        }
        if (this.uri.getRawFragment() != null) {
            uriBuilder.append('#').append(this.uri.getRawFragment());
        }
        try {
            return new URI(uriBuilder.toString());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Invalid URI path: \"" + this.uriPath + "\"", ex);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/DefaultServerHttpRequestBuilder$MutatedServerHttpRequest.class */
    private static class MutatedServerHttpRequest extends AbstractServerHttpRequest {

        @Nullable
        private final SslInfo sslInfo;

        @Nullable
        private final InetSocketAddress remoteAddress;
        private final Flux<DataBuffer> body;
        private final ServerHttpRequest originalRequest;

        public MutatedServerHttpRequest(URI uri, @Nullable String contextPath, HttpMethod method, @Nullable SslInfo sslInfo, @Nullable InetSocketAddress remoteAddress, HttpHeaders headers, Flux<DataBuffer> body, ServerHttpRequest originalRequest) {
            super(method, uri, contextPath, headers);
            this.remoteAddress = remoteAddress != null ? remoteAddress : originalRequest.getRemoteAddress();
            this.sslInfo = sslInfo != null ? sslInfo : originalRequest.getSslInfo();
            this.body = body;
            this.originalRequest = originalRequest;
        }

        @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
        protected MultiValueMap<String, HttpCookie> initCookies() {
            return this.originalRequest.getCookies();
        }

        @Override // org.springframework.http.server.reactive.ServerHttpRequest
        @Nullable
        public InetSocketAddress getLocalAddress() {
            return this.originalRequest.getLocalAddress();
        }

        @Override // org.springframework.http.server.reactive.ServerHttpRequest
        @Nullable
        public InetSocketAddress getRemoteAddress() {
            return this.remoteAddress;
        }

        @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
        @Nullable
        protected SslInfo initSslInfo() {
            return this.sslInfo;
        }

        @Override // org.springframework.http.ReactiveHttpInputMessage
        public Flux<DataBuffer> getBody() {
            return this.body;
        }

        @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
        public <T> T getNativeRequest() {
            return (T) ServerHttpRequestDecorator.getNativeRequest(this.originalRequest);
        }

        @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest, org.springframework.http.server.reactive.ServerHttpRequest
        public String getId() {
            return this.originalRequest.getId();
        }
    }
}
