package org.springframework.http;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ReactiveHttpOutputMessage.class */
public interface ReactiveHttpOutputMessage extends HttpMessage {
    DataBufferFactory bufferFactory();

    void beforeCommit(Supplier<? extends Mono<Void>> action);

    boolean isCommitted();

    Mono<Void> writeWith(Publisher<? extends DataBuffer> body);

    Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body);

    Mono<Void> setComplete();
}
