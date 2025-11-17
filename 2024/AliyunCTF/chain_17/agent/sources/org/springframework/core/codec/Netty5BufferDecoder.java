package org.springframework.core.codec;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.DefaultBufferAllocators;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.Netty5DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/Netty5BufferDecoder.class */
public class Netty5BufferDecoder extends AbstractDataBufferDecoder<Buffer> {
    @Override // org.springframework.core.codec.Decoder
    public /* bridge */ /* synthetic */ Object decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map hints) throws DecodingException {
        return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
    }

    public Netty5BufferDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Buffer.class.isAssignableFrom(elementType.toClass()) && super.canDecode(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.Decoder
    public Buffer decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(Hints.getLogPrefix(hints) + "Read " + dataBuffer.readableByteCount() + " bytes");
        }
        if (dataBuffer instanceof Netty5DataBuffer) {
            Netty5DataBuffer netty5DataBuffer = (Netty5DataBuffer) dataBuffer;
            return netty5DataBuffer.getNativeBuffer();
        }
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        Buffer buffer = DefaultBufferAllocators.preferredAllocator().copyOf(bytes);
        DataBufferUtils.release(dataBuffer);
        return buffer;
    }
}
