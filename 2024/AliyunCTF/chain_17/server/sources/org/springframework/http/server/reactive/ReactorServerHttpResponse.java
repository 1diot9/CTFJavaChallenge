package org.springframework.http.server.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.support.Netty4HeadersAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ChannelOperationsId;
import reactor.netty.http.server.HttpServerResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ReactorServerHttpResponse.class */
class ReactorServerHttpResponse extends AbstractServerHttpResponse implements ZeroCopyHttpOutputMessage {
    private static final Log logger = LogFactory.getLog((Class<?>) ReactorServerHttpResponse.class);
    private final HttpServerResponse response;

    public ReactorServerHttpResponse(HttpServerResponse response, DataBufferFactory bufferFactory) {
        super(bufferFactory, new HttpHeaders(new Netty4HeadersAdapter(((HttpServerResponse) Objects.requireNonNull(response, "HttpServerResponse must not be null")).responseHeaders())));
        this.response = response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    public HttpStatusCode getStatusCode() {
        HttpStatusCode status = super.getStatusCode();
        return status != null ? status : HttpStatusCode.valueOf(this.response.status().code());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse, org.springframework.http.server.reactive.ServerHttpResponse
    @Deprecated
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return Integer.valueOf(status != null ? status.intValue() : this.response.status().code());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        HttpStatusCode status = super.getStatusCode();
        if (status != null) {
            this.response.status(status.value());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
        return this.response.send(toByteBufs(publisher)).then();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> publisher) {
        return this.response.sendGroups(Flux.from(publisher).map(this::toByteBufs)).then();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        for (String name : getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List) getCookies().get(name)) {
                DefaultCookie cookie = new DefaultCookie(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookie.setMaxAge(httpCookie.getMaxAge().getSeconds());
                }
                if (httpCookie.getDomain() != null) {
                    cookie.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookie.setPath(httpCookie.getPath());
                }
                cookie.setSecure(httpCookie.isSecure());
                cookie.setHttpOnly(httpCookie.isHttpOnly());
                if (httpCookie.getSameSite() != null) {
                    cookie.setSameSite(CookieHeaderNames.SameSite.valueOf(httpCookie.getSameSite()));
                }
                this.response.addCookie(cookie);
            }
        }
    }

    @Override // org.springframework.http.ZeroCopyHttpOutputMessage
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() -> {
            return this.response.sendFile(file, position, count).then();
        });
    }

    private Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        if (dataBuffers instanceof Mono) {
            return Mono.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
        }
        return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void touchDataBuffer(DataBuffer buffer) {
        if (logger.isDebugEnabled()) {
            ChannelOperationsId channelOperationsId = this.response;
            if (channelOperationsId instanceof ChannelOperationsId) {
                ChannelOperationsId operationsId = channelOperationsId;
                DataBufferUtils.touch(buffer, "Channel id: " + operationsId.asLongText());
            } else {
                this.response.withConnection(connection -> {
                    ChannelId id = connection.channel().id();
                    DataBufferUtils.touch(buffer, "Channel id: " + id.asShortText());
                });
            }
        }
    }
}
