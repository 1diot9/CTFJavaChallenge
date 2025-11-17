package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.function.Consumer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServerHttpRequest.class */
public interface ServerHttpRequest extends HttpRequest, ReactiveHttpInputMessage {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServerHttpRequest$Builder.class */
    public interface Builder {
        Builder method(HttpMethod httpMethod);

        Builder uri(URI uri);

        Builder path(String path);

        Builder contextPath(String contextPath);

        Builder header(String headerName, String... headerValues);

        Builder headers(Consumer<HttpHeaders> headersConsumer);

        Builder sslInfo(SslInfo sslInfo);

        Builder remoteAddress(InetSocketAddress remoteAddress);

        ServerHttpRequest build();
    }

    String getId();

    RequestPath getPath();

    MultiValueMap<String, String> getQueryParams();

    MultiValueMap<String, HttpCookie> getCookies();

    @Nullable
    default InetSocketAddress getLocalAddress() {
        return null;
    }

    @Nullable
    default InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Nullable
    default SslInfo getSslInfo() {
        return null;
    }

    default Builder mutate() {
        return new DefaultServerHttpRequestBuilder(this);
    }
}
