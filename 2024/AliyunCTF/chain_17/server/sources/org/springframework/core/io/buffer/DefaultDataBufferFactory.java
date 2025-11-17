package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DefaultDataBufferFactory.class */
public class DefaultDataBufferFactory implements DataBufferFactory {
    public static final int DEFAULT_INITIAL_CAPACITY = 256;
    public static final DefaultDataBufferFactory sharedInstance = new DefaultDataBufferFactory();
    private final boolean preferDirect;
    private final int defaultInitialCapacity;

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public /* bridge */ /* synthetic */ DataBuffer join(List dataBuffers) {
        return join((List<? extends DataBuffer>) dataBuffers);
    }

    public DefaultDataBufferFactory() {
        this(false);
    }

    public DefaultDataBufferFactory(boolean preferDirect) {
        this(preferDirect, 256);
    }

    public DefaultDataBufferFactory(boolean preferDirect, int defaultInitialCapacity) {
        Assert.isTrue(defaultInitialCapacity > 0, "'defaultInitialCapacity' should be larger than 0");
        this.preferDirect = preferDirect;
        this.defaultInitialCapacity = defaultInitialCapacity;
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    @Deprecated
    public DefaultDataBuffer allocateBuffer() {
        return allocateBuffer(this.defaultInitialCapacity);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DefaultDataBuffer allocateBuffer(int initialCapacity) {
        ByteBuffer allocate;
        if (this.preferDirect) {
            allocate = ByteBuffer.allocateDirect(initialCapacity);
        } else {
            allocate = ByteBuffer.allocate(initialCapacity);
        }
        ByteBuffer byteBuffer = allocate;
        return DefaultDataBuffer.fromEmptyByteBuffer(this, byteBuffer);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DefaultDataBuffer wrap(ByteBuffer byteBuffer) {
        return DefaultDataBuffer.fromFilledByteBuffer(this, byteBuffer.slice());
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DefaultDataBuffer wrap(byte[] bytes) {
        return DefaultDataBuffer.fromFilledByteBuffer(this, ByteBuffer.wrap(bytes));
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DefaultDataBuffer join(List<? extends DataBuffer> dataBuffers) {
        Assert.notEmpty(dataBuffers, "DataBuffer List must not be empty");
        int capacity = dataBuffers.stream().mapToInt((v0) -> {
            return v0.readableByteCount();
        }).sum();
        DefaultDataBuffer result = allocateBuffer(capacity);
        Objects.requireNonNull(result);
        dataBuffers.forEach(xva$0 -> {
            result.write(xva$0);
        });
        dataBuffers.forEach(DataBufferUtils::release);
        return result;
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public boolean isDirect() {
        return this.preferDirect;
    }

    public String toString() {
        return "DefaultDataBufferFactory (preferDirect=" + this.preferDirect + ")";
    }
}
