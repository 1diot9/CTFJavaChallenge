package org.springframework.http.client.reactive;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.reactive.ReactiveEntityProducer;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.support.HttpComponentsHeadersAdapter;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/HttpComponentsClientHttpRequest.class */
class HttpComponentsClientHttpRequest extends AbstractClientHttpRequest {
    private final HttpRequest httpRequest;
    private final DataBufferFactory dataBufferFactory;
    private final HttpClientContext context;

    @Nullable
    private Flux<ByteBuffer> byteBufferFlux;
    private transient long contentLength = -1;

    public HttpComponentsClientHttpRequest(HttpMethod method, URI uri, HttpClientContext context, DataBufferFactory dataBufferFactory) {
        this.context = context;
        this.httpRequest = new BasicHttpRequest(method.name(), uri);
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.httpRequest.getMethod());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public URI getURI() {
        try {
            return this.httpRequest.getUri();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URI syntax: " + ex.getMessage());
        }
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.httpRequest;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return doCommit(() -> {
            this.byteBufferFlux = Flux.from(body).map(dataBuffer -> {
                ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
                dataBuffer.toByteBuffer(byteBuffer);
                return byteBuffer;
            });
            return Mono.empty();
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMap(Function.identity()));
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return doCommit();
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        headers.entrySet().stream().filter(entry -> {
            return !HttpHeaders.CONTENT_LENGTH.equals(entry.getKey());
        }).forEach(entry2 -> {
            ((List) entry2.getValue()).forEach(v -> {
                this.httpRequest.addHeader((String) entry2.getKey(), v);
            });
        });
        if (!this.httpRequest.containsHeader(HttpHeaders.ACCEPT)) {
            this.httpRequest.addHeader(HttpHeaders.ACCEPT, "*/*");
        }
        this.contentLength = headers.getContentLength();
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyCookies() {
        if (getCookies().isEmpty()) {
            return;
        }
        CookieStore cookieStore = this.context.getCookieStore();
        getCookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).forEach(cookie -> {
            BasicClientCookie clientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
            clientCookie.setDomain(getURI().getHost());
            clientCookie.setPath(getURI().getPath());
            cookieStore.addCookie(clientCookie);
        });
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected HttpHeaders initReadOnlyHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(new HttpComponentsHeadersAdapter(this.httpRequest));
    }

    public AsyncRequestProducer toRequestProducer() {
        ReactiveEntityProducer reactiveEntityProducer = null;
        if (this.byteBufferFlux != null) {
            String contentEncoding = getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
            ContentType contentType = null;
            if (getHeaders().getContentType() != null) {
                contentType = ContentType.parse(getHeaders().getContentType().toString());
            }
            reactiveEntityProducer = new ReactiveEntityProducer(this.byteBufferFlux, this.contentLength, contentType, contentEncoding);
        }
        return new BasicRequestProducer(this.httpRequest, reactiveEntityProducer);
    }
}
