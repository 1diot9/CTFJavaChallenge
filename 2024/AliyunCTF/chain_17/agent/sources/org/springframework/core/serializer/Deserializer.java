package org.springframework.core.serializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/serializer/Deserializer.class */
public interface Deserializer<T> {
    T deserialize(InputStream inputStream) throws IOException;

    default T deserializeFromByteArray(byte[] serialized) throws IOException {
        return deserialize(new ByteArrayInputStream(serialized));
    }
}
