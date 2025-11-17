package org.springframework.core.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/NettyDataBufferFactory.class */
public class NettyDataBufferFactory implements DataBufferFactory {
    private final ByteBufAllocator byteBufAllocator;

    public NettyDataBufferFactory(ByteBufAllocator byteBufAllocator) {
        Assert.notNull(byteBufAllocator, "ByteBufAllocator must not be null");
        this.byteBufAllocator = byteBufAllocator;
    }

    public ByteBufAllocator getByteBufAllocator() {
        return this.byteBufAllocator;
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    @Deprecated
    public NettyDataBuffer allocateBuffer() {
        ByteBuf byteBuf = this.byteBufAllocator.buffer();
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public NettyDataBuffer allocateBuffer(int initialCapacity) {
        ByteBuf byteBuf = this.byteBufAllocator.buffer(initialCapacity);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public NettyDataBuffer wrap(ByteBuffer byteBuffer) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DataBuffer wrap(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        return new NettyDataBuffer(byteBuf, this);
    }

    public NettyDataBuffer wrap(ByteBuf byteBuf) {
        byteBuf.touch();
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
        Assert.notEmpty(dataBuffers, "DataBuffer List must not be empty");
        int bufferCount = dataBuffers.size();
        if (bufferCount == 1) {
            return dataBuffers.get(0);
        }
        CompositeByteBuf composite = this.byteBufAllocator.compositeBuffer(bufferCount);
        for (DataBuffer dataBuffer : dataBuffers) {
            Assert.isInstanceOf(NettyDataBuffer.class, dataBuffer);
            composite.addComponent(true, ((NettyDataBuffer) dataBuffer).getNativeBuffer());
        }
        return new NettyDataBuffer(composite, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public boolean isDirect() {
        return this.byteBufAllocator.isDirectBufferPooled();
    }

    public static ByteBuf toByteBuf(DataBuffer dataBuffer) {
        if (dataBuffer instanceof NettyDataBuffer) {
            NettyDataBuffer nettyDataBuffer = (NettyDataBuffer) dataBuffer;
            return nettyDataBuffer.getNativeBuffer();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
        dataBuffer.toByteBuffer(byteBuffer);
        return Unpooled.wrappedBuffer(byteBuffer);
    }

    public String toString() {
        return "NettyDataBufferFactory (" + this.byteBufAllocator + ")";
    }
}
