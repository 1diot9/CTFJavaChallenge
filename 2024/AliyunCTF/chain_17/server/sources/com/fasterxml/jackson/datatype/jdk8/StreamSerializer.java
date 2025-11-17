package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.stream.Stream;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/StreamSerializer.class */
public class StreamSerializer extends StdSerializer<Stream<?>> implements ContextualSerializer {
    private static final long serialVersionUID = 1;
    private final JavaType elemType;
    private final transient JsonSerializer<Object> elemSerializer;

    public StreamSerializer(JavaType streamType, JavaType elemType) {
        this(streamType, elemType, null);
    }

    public StreamSerializer(JavaType streamType, JavaType elemType, JsonSerializer<Object> elemSerializer) {
        super(streamType);
        this.elemType = elemType;
        this.elemSerializer = elemSerializer;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        if (!this.elemType.hasRawClass(Object.class) && (provider.isEnabled(MapperFeature.USE_STATIC_TYPING) || this.elemType.isFinal())) {
            return new StreamSerializer(provider.getTypeFactory().constructParametricType(Stream.class, this.elemType), this.elemType, provider.findPrimaryPropertySerializer(this.elemType, property));
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Stream<?> stream, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        Throwable th = null;
        try {
            try {
                jgen.writeStartArray();
                stream.forEachOrdered(elem -> {
                    try {
                        if (this.elemSerializer == null) {
                            provider.defaultSerializeValue(elem, jgen);
                        } else {
                            this.elemSerializer.serialize(elem, jgen, provider);
                        }
                    } catch (IOException e) {
                        throw new WrappedIOException(e);
                    }
                });
                jgen.writeEndArray();
                if (stream != null) {
                    if (0 != 0) {
                        try {
                            stream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        stream.close();
                    }
                }
            } finally {
            }
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
    }
}
