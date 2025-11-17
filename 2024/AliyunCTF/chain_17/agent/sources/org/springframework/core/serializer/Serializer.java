package org.springframework.core.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/serializer/Serializer.class */
public interface Serializer<T> {
    void serialize(T object, OutputStream outputStream) throws IOException;

    default byte[] serializeToByteArray(T object) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        serialize(object, out);
        return out.toByteArray();
    }
}
