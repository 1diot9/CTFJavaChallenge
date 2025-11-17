package org.springframework.http.client.reactive;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.reactive.client.ReactiveRequest;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.support.JettyHeadersAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JettyClientHttpRequest.class */
class JettyClientHttpRequest extends AbstractClientHttpRequest {
    private final Request jettyRequest;
    private final DataBufferFactory bufferFactory;
    private final ReactiveRequest.Builder builder;

    public JettyClientHttpRequest(Request jettyRequest, DataBufferFactory bufferFactory) {
        this.jettyRequest = jettyRequest;
        this.bufferFactory = bufferFactory;
        this.builder = ReactiveRequest.newBuilder(this.jettyRequest).abortOnCancel(true);
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.jettyRequest.getMethod());
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public URI getURI() {
        return this.jettyRequest.getURI();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return doCommit();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.jettyRequest;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return Mono.create(sink -> {
            Flux concatWith = Flux.from(body).concatMapIterable(this::toContentChunks).concatWith(Mono.just(Content.Chunk.EOF));
            Objects.requireNonNull(sink);
            ReactiveRequest.Content content = (ReactiveRequest.Content) concatWith.doOnError(sink::error).as(chunks -> {
                return ReactiveRequest.Content.fromPublisher(chunks, getContentType());
            });
            this.builder.content(content);
            sink.success();
        }).then(doCommit());
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMap(Function.identity()).doOnDiscard(DataBuffer.class, DataBufferUtils::release));
    }

    private String getContentType() {
        MediaType contentType = getHeaders().getContentType();
        return contentType != null ? contentType.toString() : "application/octet-stream";
    }

    private List<Content.Chunk> toContentChunks(DataBuffer dataBuffer) {
        List<Content.Chunk> result = new ArrayList<>(1);
        DataBuffer.ByteBufferIterator iterator = dataBuffer.readableByteBuffers();
        while (iterator.hasNext()) {
            ByteBuffer byteBuffer = iterator.next();
            boolean last = !iterator.hasNext();
            Content.Chunk chunk = Content.Chunk.from(byteBuffer, false, () -> {
                if (last) {
                    iterator.close();
                    DataBufferUtils.release(dataBuffer);
                }
            });
            result.add(chunk);
        }
        return result;
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyCookies() {
        Stream map = getCookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).map(cookie -> {
            return HttpCookie.build(cookie.getName(), cookie.getValue()).build();
        });
        Request request = this.jettyRequest;
        Objects.requireNonNull(request);
        map.forEach(request::cookie);
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        this.jettyRequest.headers(fields -> {
            headers.forEach((key, value) -> {
                value.forEach(v -> {
                    fields.add(key, v);
                });
            });
            if (!headers.containsKey(HttpHeaders.ACCEPT)) {
                fields.add(HttpHeaders.ACCEPT, "*/*");
            }
        });
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected HttpHeaders initReadOnlyHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(new JettyHeadersAdapter(this.jettyRequest.getHeaders()));
    }

    public ReactiveRequest toReactiveRequest() {
        return this.builder.build();
    }
}
