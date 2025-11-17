package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase;
import java.io.IOException;
import java.util.Iterator;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/IteratorSerializer.class */
public class IteratorSerializer extends AsArraySerializerBase<Iterator<?>> {
    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    /* renamed from: withResolved, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ AsArraySerializerBase<Iterator<?>> withResolved2(BeanProperty beanProperty, TypeSerializer typeSerializer, JsonSerializer jsonSerializer, Boolean bool) {
        return withResolved(beanProperty, typeSerializer, (JsonSerializer<?>) jsonSerializer, bool);
    }

    public IteratorSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts) {
        super((Class<?>) Iterator.class, elemType, staticTyping, vts, (JsonSerializer<Object>) null);
    }

    public IteratorSerializer(IteratorSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider prov, Iterator<?> value) {
        return !value.hasNext();
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public boolean hasSingleElement(Iterator<?> value) {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return new IteratorSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public AsArraySerializerBase<Iterator<?>> withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
        return new IteratorSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(Iterator<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray(value);
        serializeContents(value, gen, provider);
        gen.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public void serializeContents(Iterator<?> value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (!value.hasNext()) {
            return;
        }
        JsonSerializer<Object> serializer = this._elementSerializer;
        if (serializer == null) {
            _serializeDynamicContents(value, g, provider);
            return;
        }
        TypeSerializer typeSer = this._valueTypeSerializer;
        do {
            Object elem = value.next();
            if (elem == null) {
                provider.defaultSerializeNull(g);
            } else if (typeSer == null) {
                serializer.serialize(elem, g, provider);
            } else {
                serializer.serializeWithType(elem, g, provider, typeSer);
            }
        } while (value.hasNext());
    }

    protected void _serializeDynamicContents(Iterator<?> value, JsonGenerator g, SerializerProvider provider) throws IOException {
        TypeSerializer typeSer = this._valueTypeSerializer;
        PropertySerializerMap serializers = this._dynamicSerializers;
        do {
            Object elem = value.next();
            if (elem == null) {
                provider.defaultSerializeNull(g);
            } else {
                Class<?> cc = elem.getClass();
                JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                if (serializer == null) {
                    if (this._elementType.hasGenericTypes()) {
                        serializer = _findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider);
                    } else {
                        serializer = _findAndAddDynamic(serializers, cc, provider);
                    }
                    serializers = this._dynamicSerializers;
                }
                if (typeSer == null) {
                    serializer.serialize(elem, g, provider);
                } else {
                    serializer.serializeWithType(elem, g, provider, typeSer);
                }
            }
        } while (value.hasNext());
    }
}
