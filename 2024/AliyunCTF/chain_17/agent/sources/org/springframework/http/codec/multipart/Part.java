package org.springframework.http.codec.multipart;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/Part.class */
public interface Part {
    String name();

    HttpHeaders headers();

    Flux<DataBuffer> content();

    default Mono<Void> delete() {
        return Mono.empty();
    }
}
