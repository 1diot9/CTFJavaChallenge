package org.springframework.http.server.reactive;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.ssl.SslHandler;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLSession;
import org.apache.commons.logging.Log;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.support.Netty4HeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.ChannelOperationsId;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ReactorServerHttpRequest.class */
class ReactorServerHttpRequest extends AbstractServerHttpRequest {
    private static final Log logger = HttpLogging.forLogName(ReactorServerHttpRequest.class);
    private static final AtomicLong logPrefixIndex = new AtomicLong();
    private final HttpServerRequest request;
    private final NettyDataBufferFactory bufferFactory;

    public ReactorServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory bufferFactory) throws URISyntaxException {
        super(HttpMethod.valueOf(request.method().name()), ReactorUriHelper.createUri(request), "", new Netty4HeadersAdapter(request.requestHeaders()));
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (CharSequence name : this.request.allCookies().keySet()) {
            for (Cookie cookie : (List) this.request.allCookies().get(name)) {
                HttpCookie httpCookie = new HttpCookie(name.toString(), cookie.value());
                cookies.add(name.toString(), httpCookie);
            }
        }
        return cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.request.hostAddress();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.request.remoteAddress();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        Channel channel = this.request.channel();
        SslHandler sslHandler = channel.pipeline().get(SslHandler.class);
        if (sslHandler == null && channel.parent() != null) {
            sslHandler = (SslHandler) channel.parent().pipeline().get(SslHandler.class);
        }
        if (sslHandler != null) {
            SSLSession session = sslHandler.engine().getSession();
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        ByteBufFlux retain = this.request.receive().retain();
        NettyDataBufferFactory nettyDataBufferFactory = this.bufferFactory;
        Objects.requireNonNull(nettyDataBufferFactory);
        return retain.map(nettyDataBufferFactory::wrap);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected String initId() {
        Connection connection = this.request;
        if (connection instanceof Connection) {
            Connection connection2 = connection;
            return connection2.channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return null;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected String initLogPrefix() {
        String id = null;
        ChannelOperationsId channelOperationsId = this.request;
        if (channelOperationsId instanceof ChannelOperationsId) {
            ChannelOperationsId operationsId = channelOperationsId;
            id = logger.isDebugEnabled() ? operationsId.asLongText() : operationsId.asShortText();
        }
        if (id != null) {
            return id;
        }
        Connection connection = this.request;
        if (connection instanceof Connection) {
            Connection connection2 = connection;
            return connection2.channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return getId();
    }
}
