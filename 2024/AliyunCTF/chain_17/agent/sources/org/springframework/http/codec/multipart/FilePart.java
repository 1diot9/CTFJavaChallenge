package org.springframework.http.codec.multipart;

import java.io.File;
import java.nio.file.Path;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/FilePart.class */
public interface FilePart extends Part {
    String filename();

    Mono<Void> transferTo(Path dest);

    default Mono<Void> transferTo(File dest) {
        return transferTo(dest.toPath());
    }
}
