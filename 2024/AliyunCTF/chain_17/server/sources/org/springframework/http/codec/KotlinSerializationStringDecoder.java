package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.StringFormat;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/KotlinSerializationStringDecoder.class */
public abstract class KotlinSerializationStringDecoder<T extends StringFormat> extends KotlinSerializationSupport<T> implements Decoder<Object> {
    private final StringDecoder stringDecoder;

    public KotlinSerializationStringDecoder(T format, MimeType... supportedMimeTypes) {
        super(format, supportedMimeTypes);
        this.stringDecoder = StringDecoder.allMimeTypes(StringDecoder.DEFAULT_DELIMITERS, false);
    }

    public void setMaxInMemorySize(int byteCount) {
        this.stringDecoder.setMaxInMemorySize(byteCount);
    }

    public int getMaxInMemorySize() {
        return this.stringDecoder.getMaxInMemorySize();
    }

    @Override // org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return canSerialize(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.Decoder
    public List<MimeType> getDecodableMimeTypes() {
        return supportedMimeTypes();
    }

    @Override // org.springframework.core.codec.Decoder
    public List<MimeType> getDecodableMimeTypes(ResolvableType targetType) {
        return supportedMimeTypes();
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.error(new UnsupportedOperationException());
    }

    @Override // org.springframework.core.codec.Decoder
    public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Mono.defer(() -> {
            KSerializer<Object> serializer = serializer(elementType);
            if (serializer == null) {
                return Mono.error(new DecodingException("Could not find KSerializer for " + elementType));
            }
            return this.stringDecoder.decodeToMono(inputStream, elementType, mimeType, hints).map(string -> {
                return ((StringFormat) format()).decodeFromString(serializer, string);
            });
        });
    }
}
