package org.springframework.http.client.reactive;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JdkClientHttpRequest.class */
class JdkClientHttpRequest extends AbstractClientHttpRequest {
    private final HttpMethod method;
    private final URI uri;
    private final DataBufferFactory bufferFactory;
    private final HttpRequest.Builder builder;

    public JdkClientHttpRequest(HttpMethod httpMethod, URI uri, DataBufferFactory bufferFactory) {
        Assert.notNull(httpMethod, "HttpMethod is required");
        Assert.notNull(uri, "URI is required");
        Assert.notNull(bufferFactory, "DataBufferFactory is required");
        this.method = httpMethod;
        this.uri = uri;
        this.bufferFactory = bufferFactory;
        this.builder = HttpRequest.newBuilder(uri);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        return this.method;
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
        return (T) this.builder.build();
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyHeaders() {
        for (Map.Entry<String, List<String>> entry : getHeaders().entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                for (String value : entry.getValue()) {
                    this.builder.header(entry.getKey(), value);
                }
            }
        }
        if (!getHeaders().containsKey(HttpHeaders.ACCEPT)) {
            this.builder.header(HttpHeaders.ACCEPT, "*/*");
        }
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyCookies() {
        this.builder.header(HttpHeaders.COOKIE, (String) getCookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).map((v0) -> {
            return v0.toString();
        }).collect(Collectors.joining(";")));
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return doCommit(() -> {
            this.builder.method(this.method.name(), toBodyPublisher(body));
            return Mono.empty();
        });
    }

    private HttpRequest.BodyPublisher toBodyPublisher(Publisher<? extends DataBuffer> body) {
        Mono map;
        if (body instanceof Mono) {
            map = Mono.from(body).map(this::toByteBuffer);
        } else {
            map = Flux.from(body).map(this::toByteBuffer);
        }
        Flow.Publisher<ByteBuffer> bodyFlow = JdkFlowAdapter.publisherToFlowPublisher(map);
        if (getHeaders().getContentLength() > 0) {
            return HttpRequest.BodyPublishers.fromPublisher(bodyFlow, getHeaders().getContentLength());
        }
        return HttpRequest.BodyPublishers.fromPublisher(bodyFlow);
    }

    private ByteBuffer toByteBuffer(DataBuffer dataBuffer) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
        dataBuffer.toByteBuffer(byteBuffer);
        return byteBuffer;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(final Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMap(Function.identity()));
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return doCommit(() -> {
            this.builder.method(this.method.name(), HttpRequest.BodyPublishers.noBody());
            return Mono.empty();
        });
    }
}
