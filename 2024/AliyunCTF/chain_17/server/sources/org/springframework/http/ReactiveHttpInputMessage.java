package org.springframework.http;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ReactiveHttpInputMessage.class */
public interface ReactiveHttpInputMessage extends HttpMessage {
    Flux<DataBuffer> getBody();
}
