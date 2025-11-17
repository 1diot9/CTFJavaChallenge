package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase;
import java.io.IOException;
import java.util.List;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/IndexedListSerializer.class */
public final class IndexedListSerializer extends AsArraySerializerBase<List<?>> {
    private static final long serialVersionUID = 1;

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    /* renamed from: withResolved, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ AsArraySerializerBase<List<?>> withResolved2(BeanProperty beanProperty, TypeSerializer typeSerializer, JsonSerializer jsonSerializer, Boolean bool) {
        return withResolved(beanProperty, typeSerializer, (JsonSerializer<?>) jsonSerializer, bool);
    }

    public IndexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
        super((Class<?>) List.class, elemType, staticTyping, vts, valueSerializer);
    }

    public IndexedListSerializer(IndexedListSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public AsArraySerializerBase<List<?>> withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
        return new IndexedListSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider prov, List<?> value) {
        return value.isEmpty();
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public boolean hasSingleElement(List<?> value) {
        return value.size() == 1;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        return new IndexedListSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(List<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray(value, len);
        serializeContents(value, gen, provider);
        gen.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase
    public void serializeContents(List<?> value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (this._elementSerializer != null) {
            serializeContentsUsing(value, g, provider, this._elementSerializer);
            return;
        }
        if (this._valueTypeSerializer != null) {
            serializeTypedContents(value, g, provider);
            return;
        }
        int len = value.size();
        if (len == 0) {
            return;
        }
        int i = 0;
        try {
            PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                Object elem = value.get(i);
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
                    serializer.serialize(elem, g, provider);
                }
                i++;
            }
        } catch (Exception e) {
            wrapAndThrow(provider, e, value, i);
        }
    }

    public void serializeContentsUsing(List<?> value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException {
        int len = value.size();
        if (len == 0) {
            return;
        }
        TypeSerializer typeSer = this._valueTypeSerializer;
        for (int i = 0; i < len; i++) {
            Object elem = value.get(i);
            if (elem == null) {
                try {
                    provider.defaultSerializeNull(jgen);
                } catch (Exception e) {
                    wrapAndThrow(provider, e, value, i);
                }
            } else if (typeSer == null) {
                ser.serialize(elem, jgen, provider);
            } else {
                ser.serializeWithType(elem, jgen, provider, typeSer);
            }
        }
    }

    public void serializeTypedContents(List<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        int len = value.size();
        if (len == 0) {
            return;
        }
        int i = 0;
        try {
            TypeSerializer typeSer = this._valueTypeSerializer;
            PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                Object elem = value.get(i);
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
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
                    serializer.serializeWithType(elem, jgen, provider, typeSer);
                }
                i++;
            }
        } catch (Exception e) {
            wrapAndThrow(provider, e, value, i);
        }
    }
}
