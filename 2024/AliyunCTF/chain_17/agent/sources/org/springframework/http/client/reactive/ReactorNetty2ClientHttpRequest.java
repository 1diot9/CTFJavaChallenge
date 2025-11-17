package org.springframework.http.client.reactive;

import io.netty5.buffer.Buffer;
import io.netty5.handler.codec.http.headers.DefaultHttpCookiePair;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.Netty5DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.support.Netty5HeadersAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty5.NettyOutbound;
import reactor.netty5.http.client.HttpClientRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ReactorNetty2ClientHttpRequest.class */
class ReactorNetty2ClientHttpRequest extends AbstractClientHttpRequest implements ZeroCopyHttpOutputMessage {
    private final HttpMethod httpMethod;
    private final URI uri;
    private final HttpClientRequest request;
    private final NettyOutbound outbound;
    private final Netty5DataBufferFactory bufferFactory;

    public ReactorNetty2ClientHttpRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound outbound) {
        this.httpMethod = method;
        this.uri = uri;
        this.request = request;
        this.outbound = outbound;
        this.bufferFactory = new Netty5DataBufferFactory(outbound.alloc());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        return this.httpMethod;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return doCommit(() -> {
            if (body instanceof Mono) {
                Mono<Buffer> bufferMono = Mono.from(body).map(Netty5DataBufferFactory::toBuffer);
                return this.outbound.send(bufferMono).then();
            }
            Flux<Buffer> bufferFlux = Flux.from(body).map(Netty5DataBufferFactory::toBuffer);
            return this.outbound.send(bufferFlux).then();
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        Flux map = Flux.from(body).map(ReactorNetty2ClientHttpRequest::toBuffers);
        return doCommit(() -> {
            return this.outbound.sendGroups(map).then();
        });
    }

    private static Publisher<Buffer> toBuffers(Publisher<? extends DataBuffer> dataBuffers) {
        return Flux.from(dataBuffers).map(Netty5DataBufferFactory::toBuffer);
    }

    @Override // org.springframework.http.ZeroCopyHttpOutputMessage
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() -> {
            return this.outbound.sendFile(file, position, count).then();
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        NettyOutbound nettyOutbound = this.outbound;
        Objects.requireNonNull(nettyOutbound);
        return doCommit(nettyOutbound::then);
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyHeaders() {
        getHeaders().forEach((key, value) -> {
            this.request.requestHeaders().set(key, value);
        });
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyCookies() {
        getCookies().values().forEach(values -> {
            values.forEach(value -> {
                DefaultHttpCookiePair cookie = new DefaultHttpCookiePair(value.getName(), value.getValue());
                this.request.addCookie(cookie);
            });
        });
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected HttpHeaders initReadOnlyHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(new Netty5HeadersAdapter(this.request.requestHeaders()));
    }
}
