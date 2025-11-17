package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferFactory.class */
public interface DataBufferFactory {
    @Deprecated(since = "6.0")
    DataBuffer allocateBuffer();

    DataBuffer allocateBuffer(int initialCapacity);

    DataBuffer wrap(ByteBuffer byteBuffer);

    DataBuffer wrap(byte[] bytes);

    DataBuffer join(List<? extends DataBuffer> dataBuffers);

    boolean isDirect();
}
