package org.springframework.core.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/serializer/DefaultDeserializer.class */
public class DefaultDeserializer implements Deserializer<Object> {

    @Nullable
    private final ClassLoader classLoader;

    public DefaultDeserializer() {
        this.classLoader = null;
    }

    public DefaultDeserializer(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.core.serializer.Deserializer
    public Object deserialize(InputStream inputStream) throws IOException {
        ObjectInputStream objectInputStream = new ConfigurableObjectInputStream(inputStream, this.classLoader);
        try {
            return objectInputStream.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException("Failed to deserialize object type", ex);
        }
    }
}
