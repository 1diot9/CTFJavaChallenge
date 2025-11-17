package org.springframework.http.server.reactive;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLSession;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.xnio.channels.StreamSourceChannel;
import reactor.core.publisher.Flux;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowServerHttpRequest.class */
class UndertowServerHttpRequest extends AbstractServerHttpRequest {
    private static final AtomicLong logPrefixIndex = new AtomicLong();
    private final HttpServerExchange exchange;
    private final RequestBodyPublisher body;

    public UndertowServerHttpRequest(HttpServerExchange exchange, DataBufferFactory bufferFactory) throws URISyntaxException {
        super(HttpMethod.valueOf(exchange.getRequestMethod().toString()), initUri(exchange), "", new UndertowHeadersAdapter(exchange.getRequestHeaders()));
        this.exchange = exchange;
        this.body = new RequestBodyPublisher(exchange, bufferFactory);
        this.body.registerListeners(exchange);
    }

    private static URI initUri(HttpServerExchange exchange) throws URISyntaxException {
        Assert.notNull(exchange, "HttpServerExchange is required");
        String requestURL = exchange.getRequestURL();
        String query = exchange.getQueryString();
        String requestUriAndQuery = StringUtils.hasLength(query) ? requestURL + "?" + query : requestURL;
        return new URI(requestUriAndQuery);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (Cookie cookie : this.exchange.requestCookies()) {
            HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
            cookies.add(cookie.getName(), httpCookie);
        }
        return cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.exchange.getDestinationAddress();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.exchange.getSourceAddress();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        SSLSession session = this.exchange.getConnection().getSslSession();
        if (session != null) {
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return Flux.from(this.body);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.exchange;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected String initId() {
        return ObjectUtils.getIdentityHexString(this.exchange.getConnection()) + "-" + logPrefixIndex.incrementAndGet();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/UndertowServerHttpRequest$RequestBodyPublisher.class */
    private class RequestBodyPublisher extends AbstractListenerReadPublisher<DataBuffer> {
        private final StreamSourceChannel channel;
        private final DataBufferFactory bufferFactory;
        private final ByteBufferPool byteBufferPool;

        public RequestBodyPublisher(HttpServerExchange exchange, DataBufferFactory bufferFactory) {
            super(UndertowServerHttpRequest.this.getLogPrefix());
            this.channel = exchange.getRequestChannel();
            this.bufferFactory = bufferFactory;
            this.byteBufferPool = exchange.getConnection().getByteBufferPool();
        }

        private void registerListeners(HttpServerExchange exchange) {
            exchange.addExchangeCompleteListener((ex, next) -> {
                onAllDataRead();
                next.proceed();
            });
            this.channel.getReadSetter().set(c -> {
                onDataAvailable();
            });
            this.channel.getCloseSetter().set(c2 -> {
                onAllDataRead();
            });
            this.channel.resumeReads();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void checkOnDataAvailable() {
            this.channel.resumeReads();
            onDataAvailable();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void readingPaused() {
            this.channel.suspendReads();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        @Nullable
        public DataBuffer read() throws IOException {
            PooledByteBuffer pooledByteBuffer = this.byteBufferPool.allocate();
            try {
                ByteBuffer byteBuffer = pooledByteBuffer.getBuffer();
                int read = this.channel.read(byteBuffer);
                if (rsReadLogger.isTraceEnabled()) {
                    rsReadLogger.trace(getLogPrefix() + "Read " + read + (read != -1 ? " bytes" : ""));
                }
                if (read > 0) {
                    byteBuffer.flip();
                    DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(read);
                    dataBuffer.write(byteBuffer);
                    if (pooledByteBuffer != null) {
                        pooledByteBuffer.close();
                    }
                    return dataBuffer;
                }
                if (read == -1) {
                    onAllDataRead();
                }
                if (pooledByteBuffer != null) {
                    pooledByteBuffer.close();
                }
                return null;
            } catch (Throwable th) {
                if (pooledByteBuffer != null) {
                    try {
                        pooledByteBuffer.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void discardData() {
        }
    }
}
