package org.springframework.core.io.buffer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/TouchableDataBuffer.class */
public interface TouchableDataBuffer extends DataBuffer {
    TouchableDataBuffer touch(Object hint);
}
