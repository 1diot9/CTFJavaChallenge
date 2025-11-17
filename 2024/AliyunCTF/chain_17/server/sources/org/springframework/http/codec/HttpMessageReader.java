package org.springframework.http.codec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/HttpMessageReader.class */
public interface HttpMessageReader<T> {
    List<MediaType> getReadableMediaTypes();

    boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType);

    Flux<T> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints);

    Mono<T> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints);

    default List<MediaType> getReadableMediaTypes(ResolvableType elementType) {
        return canRead(elementType, null) ? getReadableMediaTypes() : Collections.emptyList();
    }

    default Flux<T> read(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return read(elementType, request, hints);
    }

    default Mono<T> readMono(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return readMono(elementType, request, hints);
    }
}
