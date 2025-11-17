package org.springframework.core.serializer.support;

import java.io.ByteArrayInputStream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/serializer/support/DeserializingConverter.class */
public class DeserializingConverter implements Converter<byte[], Object> {
    private final Deserializer<Object> deserializer;

    public DeserializingConverter() {
        this.deserializer = new DefaultDeserializer();
    }

    public DeserializingConverter(@Nullable ClassLoader classLoader) {
        this.deserializer = new DefaultDeserializer(classLoader);
    }

    public DeserializingConverter(Deserializer<Object> deserializer) {
        Assert.notNull(deserializer, "Deserializer must not be null");
        this.deserializer = deserializer;
    }

    @Override // org.springframework.core.convert.converter.Converter
    public Object convert(byte[] source) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(source);
        try {
            return this.deserializer.deserialize(byteStream);
        } catch (Throwable ex) {
            throw new SerializationFailedException("Failed to deserialize payload. Is the byte array a result of corresponding serialization for " + this.deserializer.getClass().getSimpleName() + "?", ex);
        }
    }
}
