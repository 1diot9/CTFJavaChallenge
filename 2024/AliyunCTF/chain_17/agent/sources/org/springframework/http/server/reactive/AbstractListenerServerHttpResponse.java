package org.springframework.http.server.reactive;

import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerServerHttpResponse.class */
public abstract class AbstractListenerServerHttpResponse extends AbstractServerHttpResponse {
    private final AtomicBoolean writeCalled;

    protected abstract Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor();

    public AbstractListenerServerHttpResponse(DataBufferFactory bufferFactory) {
        super(bufferFactory);
        this.writeCalled = new AtomicBoolean();
    }

    public AbstractListenerServerHttpResponse(DataBufferFactory bufferFactory, HttpHeaders headers) {
        super(bufferFactory, headers);
        this.writeCalled = new AtomicBoolean();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected final Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body) {
        return writeAndFlushWithInternal(Mono.just(body));
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected final Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        if (!this.writeCalled.compareAndSet(false, true)) {
            return Mono.error(new IllegalStateException("writeWith() or writeAndFlushWith() has already been called"));
        }
        Processor<? super Publisher<? extends DataBuffer>, Void> processor = createBodyFlushProcessor();
        return Mono.from(subscriber -> {
            body.subscribe(processor);
            processor.subscribe(subscriber);
        });
    }
}
