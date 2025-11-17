package org.springframework.http.codec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/HttpMessageWriter.class */
public interface HttpMessageWriter<T> {
    List<MediaType> getWritableMediaTypes();

    boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType);

    Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints);

    default List<MediaType> getWritableMediaTypes(ResolvableType elementType) {
        return canWrite(elementType, null) ? getWritableMediaTypes() : Collections.emptyList();
    }

    default Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return write(inputStream, elementType, mediaType, response, hints);
    }
}
