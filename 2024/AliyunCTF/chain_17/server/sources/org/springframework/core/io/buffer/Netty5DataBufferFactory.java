package org.springframework.core.io.buffer;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferAllocator;
import io.netty5.buffer.CompositeBuffer;
import io.netty5.buffer.DefaultBufferAllocators;
import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/Netty5DataBufferFactory.class */
public class Netty5DataBufferFactory implements DataBufferFactory {
    private final BufferAllocator bufferAllocator;

    public Netty5DataBufferFactory(BufferAllocator bufferAllocator) {
        Assert.notNull(bufferAllocator, "BufferAllocator must not be null");
        this.bufferAllocator = bufferAllocator;
    }

    public BufferAllocator getBufferAllocator() {
        return this.bufferAllocator;
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    @Deprecated
    public Netty5DataBuffer allocateBuffer() {
        Buffer buffer = this.bufferAllocator.allocate(256);
        return new Netty5DataBuffer(buffer, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public Netty5DataBuffer allocateBuffer(int initialCapacity) {
        Buffer buffer = this.bufferAllocator.allocate(initialCapacity);
        return new Netty5DataBuffer(buffer, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public Netty5DataBuffer wrap(ByteBuffer byteBuffer) {
        Buffer buffer = this.bufferAllocator.copyOf(byteBuffer);
        return new Netty5DataBuffer(buffer, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public Netty5DataBuffer wrap(byte[] bytes) {
        Buffer buffer = this.bufferAllocator.copyOf(bytes);
        return new Netty5DataBuffer(buffer, this);
    }

    public Netty5DataBuffer wrap(Buffer buffer) {
        buffer.touch("Wrap buffer");
        return new Netty5DataBuffer(buffer, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
        Assert.notEmpty(dataBuffers, "DataBuffer List must not be empty");
        if (dataBuffers.size() == 1) {
            return dataBuffers.get(0);
        }
        CompositeBuffer composite = this.bufferAllocator.compose();
        for (DataBuffer dataBuffer : dataBuffers) {
            Assert.isInstanceOf(Netty5DataBuffer.class, dataBuffer);
            composite.extendWith(((Netty5DataBuffer) dataBuffer).getNativeBuffer().send());
        }
        return new Netty5DataBuffer(composite, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public boolean isDirect() {
        return this.bufferAllocator.getAllocationType().isDirect();
    }

    public static Buffer toBuffer(DataBuffer buffer) {
        if (buffer instanceof Netty5DataBuffer) {
            Netty5DataBuffer netty5DataBuffer = (Netty5DataBuffer) buffer;
            return netty5DataBuffer.getNativeBuffer();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.readableByteCount());
        buffer.toByteBuffer(byteBuffer);
        return DefaultBufferAllocators.preferredAllocator().copyOf(byteBuffer);
    }

    public String toString() {
        return "Netty5DataBufferFactory (" + this.bufferAllocator + ")";
    }
}
