package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/ContainerSerializer.class */
public abstract class ContainerSerializer<T> extends StdSerializer<T> {
    public abstract JavaType getContentType();

    public abstract JsonSerializer<?> getContentSerializer();

    public abstract boolean hasSingleElement(T t);

    protected abstract ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSerializer);

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerSerializer(Class<T> t) {
        super(t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerSerializer(JavaType fullType) {
        super(fullType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContainerSerializer(ContainerSerializer<?> src) {
        super(src._handledType, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ContainerSerializer<?> withValueTypeSerializer(TypeSerializer vts) {
        return vts == null ? this : _withValueTypeSerializer(vts);
    }

    @Deprecated
    protected boolean hasContentTypeAnnotation(SerializerProvider provider, BeanProperty property) {
        return false;
    }
}
