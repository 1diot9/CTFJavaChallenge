package org.springframework.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/NettyByteBufDecoder.class */
public class NettyByteBufDecoder extends AbstractDataBufferDecoder<ByteBuf> {
    @Override // org.springframework.core.codec.Decoder
    public /* bridge */ /* synthetic */ Object decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map hints) throws DecodingException {
        return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
    }

    public NettyByteBufDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return ByteBuf.class.isAssignableFrom(elementType.toClass()) && super.canDecode(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.Decoder
    public ByteBuf decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Read " + dataBuffer.readableByteCount() + " bytes");
        }
        if (dataBuffer instanceof NettyDataBuffer) {
            NettyDataBuffer nettyDataBuffer = (NettyDataBuffer) dataBuffer;
            return nettyDataBuffer.getNativeBuffer();
        }
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        DataBufferUtils.release(dataBuffer);
        return byteBuf;
    }
}
