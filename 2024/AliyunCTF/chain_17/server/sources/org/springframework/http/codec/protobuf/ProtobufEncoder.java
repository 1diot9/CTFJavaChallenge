package org.springframework.http.codec.protobuf;

import com.google.protobuf.Message;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/protobuf/ProtobufEncoder.class */
public class ProtobufEncoder extends ProtobufCodecSupport implements HttpMessageEncoder<Message> {
    private static final List<MediaType> streamingMediaTypes = Arrays.stream(MIME_TYPES).map(mimeType -> {
        return new MediaType(mimeType.getType(), mimeType.getSubtype(), (Map<String, String>) Collections.singletonMap("delimited", "true"));
    }).toList();

    @Override // org.springframework.core.codec.Encoder
    public /* bridge */ /* synthetic */ DataBuffer encodeValue(Object message, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map hints) {
        return encodeValue((Message) message, bufferFactory, valueType, mimeType, (Map<String, Object>) hints);
    }

    @Override // org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Message.class.isAssignableFrom(elementType.toClass()) && supportsMimeType(mimeType);
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<? extends Message> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(message -> {
            return encodeValue(message, bufferFactory, !(inputStream instanceof Mono));
        });
    }

    public DataBuffer encodeValue(Message message, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return encodeValue(message, bufferFactory, false);
    }

    private DataBuffer encodeValue(Message message, DataBufferFactory bufferFactory, boolean delimited) {
        FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
        try {
            if (delimited) {
                message.writeDelimitedTo(bos);
            } else {
                message.writeTo(bos);
            }
            byte[] bytes = bos.toByteArrayUnsafe();
            return bufferFactory.wrap(bytes);
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected I/O error while writing to data buffer", ex);
        }
    }

    @Override // org.springframework.http.codec.HttpMessageEncoder
    public List<MediaType> getStreamingMediaTypes() {
        return streamingMediaTypes;
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes() {
        return getMimeTypes();
    }
}
