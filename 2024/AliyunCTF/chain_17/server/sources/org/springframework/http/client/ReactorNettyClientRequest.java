package org.springframework.http.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.OutputStreamPublisher;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ReactorNettyClientRequest.class */
final class ReactorNettyClientRequest extends AbstractStreamingClientHttpRequest {
    private final HttpClient httpClient;
    private final HttpMethod method;
    private final URI uri;
    private final Duration exchangeTimeout;
    private final Duration readTimeout;

    public ReactorNettyClientRequest(HttpClient httpClient, URI uri, HttpMethod method, Duration exchangeTimeout, Duration readTimeout) {
        this.httpClient = httpClient;
        this.method = method;
        this.uri = uri;
        this.exchangeTimeout = exchangeTimeout;
        this.readTimeout = readTimeout;
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
        HttpClient.RequestSender requestSender = this.httpClient.request(io.netty.handler.codec.http.HttpMethod.valueOf(this.method.name()));
        try {
            ReactorNettyClientResponse result = (ReactorNettyClientResponse) (this.uri.isAbsolute() ? (HttpClient.RequestSender) requestSender.uri(this.uri) : requestSender.uri(this.uri.toString())).send((reactorRequest, nettyOutbound) -> {
                return send(headers, body, reactorRequest, nettyOutbound);
            }).responseConnection((reactorResponse, connection) -> {
                return Mono.just(new ReactorNettyClientResponse(reactorResponse, connection, this.readTimeout));
            }).next().block(this.exchangeTimeout);
            if (result == null) {
                throw new IOException("HTTP exchange resulted in no result");
            }
            return result;
        } catch (RuntimeException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof UncheckedIOException) {
                UncheckedIOException uioEx = (UncheckedIOException) cause;
                throw uioEx.getCause();
            }
            if (cause instanceof IOException) {
                IOException ioEx = (IOException) cause;
                throw ioEx;
            }
            throw ex;
        }
    }

    private Publisher<Void> send(HttpHeaders headers, @Nullable StreamingHttpOutputMessage.Body body, HttpClientRequest reactorRequest, NettyOutbound nettyOutbound) {
        headers.forEach((key, value) -> {
            reactorRequest.requestHeaders().set(key, value);
        });
        if (body != null) {
            AtomicReference<Executor> executor = new AtomicReference<>();
            return nettyOutbound.withConnection(connection -> {
                executor.set(connection.channel().eventLoop());
            }).send(FlowAdapters.toPublisher(OutputStreamPublisher.create(outputStream -> {
                body.writeTo(StreamUtils.nonClosing(outputStream));
            }, new ByteBufMapper(nettyOutbound.alloc()), executor.getAndSet(null))));
        }
        return nettyOutbound;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ReactorNettyClientRequest$ByteBufMapper.class */
    public static final class ByteBufMapper implements OutputStreamPublisher.ByteMapper<ByteBuf> {
        private final ByteBufAllocator allocator;

        public ByteBufMapper(ByteBufAllocator allocator) {
            this.allocator = allocator;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.client.OutputStreamPublisher.ByteMapper
        public ByteBuf map(int b) {
            ByteBuf byteBuf = this.allocator.buffer(1);
            byteBuf.writeByte(b);
            return byteBuf;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.client.OutputStreamPublisher.ByteMapper
        public ByteBuf map(byte[] b, int off, int len) {
            ByteBuf byteBuf = this.allocator.buffer(len);
            byteBuf.writeBytes(b, off, len);
            return byteBuf;
        }
    }
}
