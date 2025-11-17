package org.springframework.http;

import java.io.File;
import java.nio.file.Path;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ZeroCopyHttpOutputMessage.class */
public interface ZeroCopyHttpOutputMessage extends ReactiveHttpOutputMessage {
    Mono<Void> writeWith(Path file, long position, long count);

    default Mono<Void> writeWith(File file, long position, long count) {
        return writeWith(file.toPath(), position, count);
    }
}
