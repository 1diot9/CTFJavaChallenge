package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/AbstractSingleValueEncoder.class */
public abstract class AbstractSingleValueEncoder<T> extends AbstractEncoder<T> {
    protected abstract Flux<DataBuffer> encode(T t, DataBufferFactory dataBufferFactory, ResolvableType type, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints);

    public AbstractSingleValueEncoder(MimeType... supportedMimeTypes) {
        super(supportedMimeTypes);
    }

    @Override // org.springframework.core.codec.Encoder
    public final Flux<DataBuffer> encode(Publisher<? extends T> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).take(1L).concatMap(obj -> {
            return encode((AbstractSingleValueEncoder<T>) obj, bufferFactory, elementType, mimeType, (Map<String, Object>) hints);
        }).doOnDiscard(DataBuffer.class, DataBufferUtils::release);
    }
}
