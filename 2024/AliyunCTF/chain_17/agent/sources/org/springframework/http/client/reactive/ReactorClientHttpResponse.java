package org.springframework.http.client.reactive;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.support.Netty4HeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.netty.ChannelOperationsId;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.http.client.HttpClientResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ReactorClientHttpResponse.class */
public class ReactorClientHttpResponse implements ClientHttpResponse {
    private static final Log logger = LogFactory.getLog((Class<?>) ReactorClientHttpResponse.class);
    private final HttpClientResponse response;
    private final HttpHeaders headers;
    private final NettyInbound inbound;
    private final NettyDataBufferFactory bufferFactory;
    private final AtomicInteger state = new AtomicInteger();

    public ReactorClientHttpResponse(HttpClientResponse response, Connection connection) {
        this.response = response;
        MultiValueMap<String, String> adapter = new Netty4HeadersAdapter(response.responseHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
        this.inbound = connection.inbound();
        this.bufferFactory = new NettyDataBufferFactory(connection.outbound().alloc());
    }

    @Deprecated
    public ReactorClientHttpResponse(HttpClientResponse response, NettyInbound inbound, ByteBufAllocator alloc) {
        this.response = response;
        MultiValueMap<String, String> adapter = new Netty4HeadersAdapter(response.responseHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
        this.inbound = inbound;
        this.bufferFactory = new NettyDataBufferFactory(alloc);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public String getId() {
        String id = null;
        ChannelOperationsId channelOperationsId = this.response;
        if (channelOperationsId instanceof ChannelOperationsId) {
            ChannelOperationsId operationsId = channelOperationsId;
            id = logger.isDebugEnabled() ? operationsId.asLongText() : operationsId.asShortText();
        }
        if (id == null) {
            Connection connection = this.response;
            if (connection instanceof Connection) {
                Connection connection2 = connection;
                id = connection2.channel().id().asShortText();
            }
        }
        return id != null ? id : ObjectUtils.getIdentityHexString(this);
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return this.inbound.receive().doOnSubscribe(s -> {
            if (!this.state.compareAndSet(0, 1) && this.state.get() == 2) {
                throw new IllegalStateException("The client response body has been released already due to cancellation.");
            }
        }).map(byteBuf -> {
            byteBuf.retain();
            return this.bufferFactory.wrap(byteBuf);
        });
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(this.response.status().code());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        MultiValueMap<String, ResponseCookie> result = new LinkedMultiValueMap<>();
        this.response.cookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).forEach(cookie -> {
            result.add(cookie.name(), ResponseCookie.fromClientResponse(cookie.name(), cookie.value()).domain(cookie.domain()).path(cookie.path()).maxAge(cookie.maxAge()).secure(cookie.isSecure()).httpOnly(cookie.isHttpOnly()).sameSite(getSameSite(cookie)).build());
        });
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Nullable
    private static String getSameSite(Cookie cookie) {
        if (!(cookie instanceof DefaultCookie)) {
            return null;
        }
        DefaultCookie defaultCookie = (DefaultCookie) cookie;
        if (defaultCookie.sameSite() != null) {
            return defaultCookie.sameSite().name();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void releaseAfterCancel(HttpMethod method) {
        if (mayHaveBody(method) && this.state.compareAndSet(0, 2)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[" + getId() + "]Releasing body, not yet subscribed.");
            }
            this.inbound.receive().doOnNext(byteBuf -> {
            }).subscribe(byteBuf2 -> {
            }, ex -> {
            });
        }
    }

    private boolean mayHaveBody(HttpMethod method) {
        int code = getStatusCode().value();
        return ((code >= 100 && code < 200) || code == 204 || code == 205 || method.equals(HttpMethod.HEAD) || getHeaders().getContentLength() == 0) ? false : true;
    }

    public String toString() {
        return "ReactorClientHttpResponse{request=[" + this.response.method().name() + " " + this.response.uri() + "],status=" + getStatusCode() + "}";
    }
}
