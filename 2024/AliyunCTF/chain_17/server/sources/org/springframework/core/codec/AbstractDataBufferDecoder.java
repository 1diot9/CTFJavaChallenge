package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/AbstractDataBufferDecoder.class */
public abstract class AbstractDataBufferDecoder<T> extends AbstractDecoder<T> {
    private int maxInMemorySize;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDataBufferDecoder(MimeType... supportedMimeTypes) {
        super(supportedMimeTypes);
        this.maxInMemorySize = 262144;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<T> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(input).map(buffer -> {
            return decodeDataBuffer(buffer, elementType, mimeType, hints);
        });
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public Mono<T> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(input, this.maxInMemorySize).map(buffer -> {
            return decodeDataBuffer(buffer, elementType, mimeType, hints);
        });
    }

    @Nullable
    @Deprecated
    protected T decodeDataBuffer(DataBuffer buffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return decode(buffer, elementType, mimeType, hints);
    }
}
