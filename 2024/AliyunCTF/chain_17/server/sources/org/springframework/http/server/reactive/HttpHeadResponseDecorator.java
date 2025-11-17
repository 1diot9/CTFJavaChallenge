package org.springframework.http.server.reactive;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/HttpHeadResponseDecorator.class */
public class HttpHeadResponseDecorator extends ServerHttpResponseDecorator {
    public HttpHeadResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponseDecorator, org.springframework.http.ReactiveHttpOutputMessage
    public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (shouldSetContentLength() && (body instanceof Mono)) {
            return ((Mono) body).doOnSuccess(buffer -> {
                if (buffer != null) {
                    getHeaders().setContentLength(buffer.readableByteCount());
                    DataBufferUtils.release(buffer);
                } else {
                    getHeaders().setContentLength(0L);
                }
            }).then();
        }
        return Flux.from(body).doOnNext(DataBufferUtils::release).then();
    }

    private boolean shouldSetContentLength() {
        return getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH) == null && getHeaders().getFirst("Transfer-Encoding") == null;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponseDecorator, org.springframework.http.ReactiveHttpOutputMessage
    public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return setComplete();
    }
}
