package org.springframework.core.io.buffer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/CloseableDataBuffer.class */
public interface CloseableDataBuffer extends DataBuffer, AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();
}
