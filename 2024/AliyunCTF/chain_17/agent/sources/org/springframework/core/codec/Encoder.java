package org.springframework.core.codec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/Encoder.class */
public interface Encoder<T> {
    boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType);

    Flux<DataBuffer> encode(Publisher<? extends T> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints);

    List<MimeType> getEncodableMimeTypes();

    default DataBuffer encodeValue(T value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        throw new UnsupportedOperationException();
    }

    default List<MimeType> getEncodableMimeTypes(ResolvableType elementType) {
        return canEncode(elementType, null) ? getEncodableMimeTypes() : Collections.emptyList();
    }
}
