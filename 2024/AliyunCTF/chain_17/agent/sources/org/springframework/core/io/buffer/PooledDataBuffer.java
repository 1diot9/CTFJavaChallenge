package org.springframework.core.io.buffer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/PooledDataBuffer.class */
public interface PooledDataBuffer extends TouchableDataBuffer {
    boolean isAllocated();

    PooledDataBuffer retain();

    @Override // org.springframework.core.io.buffer.TouchableDataBuffer
    PooledDataBuffer touch(Object hint);

    boolean release();
}
