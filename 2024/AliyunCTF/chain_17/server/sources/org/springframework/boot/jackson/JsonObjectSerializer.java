package org.springframework.boot.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jackson/JsonObjectSerializer.class */
public abstract class JsonObjectSerializer<T> extends JsonSerializer<T> {
    protected abstract void serializeObject(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException;

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        try {
            jgen.writeStartObject();
            serializeObject(value, jgen, provider);
            jgen.writeEndObject();
        } catch (Exception ex) {
            if (ex instanceof IOException) {
                IOException ioException = (IOException) ex;
                throw ioException;
            }
            throw new JsonMappingException(jgen, "Object serialize error", ex);
        }
    }
}
