package org.springframework.core.codec;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/NettyByteBufEncoder.class */
public class NettyByteBufEncoder extends AbstractEncoder<ByteBuf> {
    @Override // org.springframework.core.codec.Encoder
    public /* bridge */ /* synthetic */ DataBuffer encodeValue(Object byteBuf, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map hints) {
        return encodeValue((ByteBuf) byteBuf, bufferFactory, valueType, mimeType, (Map<String, Object>) hints);
    }

    public NettyByteBufEncoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractEncoder, org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType type, @Nullable MimeType mimeType) {
        Class<?> clazz = type.toClass();
        return super.canEncode(type, mimeType) && ByteBuf.class.isAssignableFrom(clazz);
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<? extends ByteBuf> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(byteBuffer -> {
            return encodeValue(byteBuffer, bufferFactory, elementType, mimeType, (Map<String, Object>) hints);
        });
    }

    public DataBuffer encodeValue(ByteBuf byteBuf, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
            String logPrefix = Hints.getLogPrefix(hints);
            this.logger.debug(logPrefix + "Writing " + byteBuf.readableBytes() + " bytes");
        }
        if (bufferFactory instanceof NettyDataBufferFactory) {
            NettyDataBufferFactory nettyDataBufferFactory = (NettyDataBufferFactory) bufferFactory;
            return nettyDataBufferFactory.wrap(byteBuf);
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        byteBuf.release();
        return bufferFactory.wrap(bytes);
    }
}
