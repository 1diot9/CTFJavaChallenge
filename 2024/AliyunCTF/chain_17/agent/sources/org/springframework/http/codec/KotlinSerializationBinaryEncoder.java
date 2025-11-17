package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import kotlinx.serialization.BinaryFormat;
import kotlinx.serialization.KSerializer;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/KotlinSerializationBinaryEncoder.class */
public abstract class KotlinSerializationBinaryEncoder<T extends BinaryFormat> extends KotlinSerializationSupport<T> implements Encoder<Object> {
    private final ByteArrayEncoder byteArrayEncoder;

    /* JADX INFO: Access modifiers changed from: protected */
    public KotlinSerializationBinaryEncoder(T format, MimeType... supportedMimeTypes) {
        super(format, supportedMimeTypes);
        this.byteArrayEncoder = new ByteArrayEncoder();
    }

    @Override // org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return canSerialize(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes() {
        return supportedMimeTypes();
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes(ResolvableType elementType) {
        return supportedMimeTypes();
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<? extends Object> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (inputStream instanceof Mono) {
            return Mono.from(inputStream).map(value -> {
                return encodeValue(value, bufferFactory, elementType, mimeType, hints);
            }).flux();
        }
        ResolvableType listType = ResolvableType.forClassWithGenerics((Class<?>) List.class, elementType);
        return Flux.from(inputStream).collectList().map(list -> {
            return encodeValue(list, bufferFactory, listType, mimeType, hints);
        }).flux();
    }

    @Override // org.springframework.core.codec.Encoder
    public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        KSerializer<Object> serializer = serializer(valueType);
        if (serializer == null) {
            throw new EncodingException("Could not find KSerializer for " + valueType);
        }
        byte[] bytes = ((BinaryFormat) format()).encodeToByteArray(serializer, value);
        return this.byteArrayEncoder.encodeValue(bytes, bufferFactory, valueType, mimeType, (Map<String, Object>) null);
    }
}
