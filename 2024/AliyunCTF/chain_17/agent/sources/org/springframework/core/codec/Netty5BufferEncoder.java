package org.springframework.core.codec;

import io.netty5.buffer.Buffer;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.Netty5DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/Netty5BufferEncoder.class */
public class Netty5BufferEncoder extends AbstractEncoder<Buffer> {
    @Override // org.springframework.core.codec.Encoder
    public /* bridge */ /* synthetic */ DataBuffer encodeValue(Object buffer, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map hints) {
        return encodeValue((Buffer) buffer, bufferFactory, valueType, mimeType, (Map<String, Object>) hints);
    }

    public Netty5BufferEncoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractEncoder, org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType type, @Nullable MimeType mimeType) {
        Class<?> clazz = type.toClass();
        return super.canEncode(type, mimeType) && Buffer.class.isAssignableFrom(clazz);
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<? extends Buffer> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(byteBuffer -> {
            return encodeValue(byteBuffer, bufferFactory, elementType, mimeType, (Map<String, Object>) hints);
        });
    }

    public DataBuffer encodeValue(Buffer buffer, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
            String logPrefix = Hints.getLogPrefix(hints);
            this.logger.debug(logPrefix + "Writing " + buffer.readableBytes() + " bytes");
        }
        if (bufferFactory instanceof Netty5DataBufferFactory) {
            Netty5DataBufferFactory netty5DataBufferFactory = (Netty5DataBufferFactory) bufferFactory;
            return netty5DataBufferFactory.wrap(buffer);
        }
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes, 0, bytes.length);
        buffer.close();
        return bufferFactory.wrap(bytes);
    }
}
