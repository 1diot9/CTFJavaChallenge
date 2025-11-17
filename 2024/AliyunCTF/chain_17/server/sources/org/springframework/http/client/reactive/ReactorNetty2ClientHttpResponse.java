package org.springframework.http.client.reactive;

import io.netty5.handler.codec.http.headers.DefaultHttpSetCookie;
import io.netty5.handler.codec.http.headers.HttpSetCookie;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.Netty5DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.support.Netty5HeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.netty5.ChannelOperationsId;
import reactor.netty5.Connection;
import reactor.netty5.NettyInbound;
import reactor.netty5.http.client.HttpClientResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ReactorNetty2ClientHttpResponse.class */
public class ReactorNetty2ClientHttpResponse implements ClientHttpResponse {
    private static final Log logger = LogFactory.getLog((Class<?>) ReactorNetty2ClientHttpResponse.class);
    private final HttpClientResponse response;
    private final HttpHeaders headers;
    private final NettyInbound inbound;
    private final Netty5DataBufferFactory bufferFactory;
    private final AtomicInteger state = new AtomicInteger();

    public ReactorNetty2ClientHttpResponse(HttpClientResponse response, Connection connection) {
        this.response = response;
        MultiValueMap<String, String> adapter = new Netty5HeadersAdapter(response.responseHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
        this.inbound = connection.inbound();
        this.bufferFactory = new Netty5DataBufferFactory(connection.outbound().alloc());
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
        }).map(buffer -> {
            return this.bufferFactory.wrap(buffer.split());
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
            result.add(cookie.name().toString(), ResponseCookie.fromClientResponse(cookie.name().toString(), cookie.value().toString()).domain(cookie.domain() != null ? cookie.domain().toString() : null).path(cookie.path() != null ? cookie.path().toString() : null).maxAge(cookie.maxAge() != null ? cookie.maxAge().longValue() : -1L).secure(cookie.isSecure()).httpOnly(cookie.isHttpOnly()).sameSite(getSameSite(cookie)).build());
        });
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Nullable
    private static String getSameSite(HttpSetCookie cookie) {
        if (!(cookie instanceof DefaultHttpSetCookie)) {
            return null;
        }
        DefaultHttpSetCookie defaultCookie = (DefaultHttpSetCookie) cookie;
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
            this.inbound.receive().doOnNext(buffer -> {
            }).subscribe(buffer2 -> {
            }, ex -> {
            });
        }
    }

    private boolean mayHaveBody(HttpMethod method) {
        int code = getStatusCode().value();
        return ((code >= 100 && code < 200) || code == 204 || code == 205 || method.equals(HttpMethod.HEAD) || getHeaders().getContentLength() == 0) ? false : true;
    }

    public String toString() {
        return "ReactorNetty2ClientHttpResponse{request=[" + this.response.method().name() + " " + this.response.uri() + "],status=" + getStatusCode() + "}";
    }
}
