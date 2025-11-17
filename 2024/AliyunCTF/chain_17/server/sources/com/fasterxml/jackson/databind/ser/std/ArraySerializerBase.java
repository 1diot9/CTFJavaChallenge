package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/ArraySerializerBase.class */
public abstract class ArraySerializerBase<T> extends ContainerSerializer<T> implements ContextualSerializer {
    protected final BeanProperty _property;
    protected final Boolean _unwrapSingle;

    public abstract JsonSerializer<?> _withResolved(BeanProperty beanProperty, Boolean bool);

    protected abstract void serializeContents(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public ArraySerializerBase(Class<T> cls) {
        super(cls);
        this._property = null;
        this._unwrapSingle = null;
    }

    @Deprecated
    protected ArraySerializerBase(Class<T> cls, BeanProperty property) {
        super(cls);
        this._property = property;
        this._unwrapSingle = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArraySerializerBase(ArraySerializerBase<?> src) {
        super(src._handledType, false);
        this._property = src._property;
        this._unwrapSingle = src._unwrapSingle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArraySerializerBase(ArraySerializerBase<?> src, BeanProperty property, Boolean unwrapSingle) {
        super(src._handledType, false);
        this._property = property;
        this._unwrapSingle = unwrapSingle;
    }

    @Deprecated
    protected ArraySerializerBase(ArraySerializerBase<?> src, BeanProperty property) {
        super(src._handledType, false);
        this._property = property;
        this._unwrapSingle = src._unwrapSingle;
    }

    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value format;
        if (property != null && (format = findFormatOverrides(serializers, property, handledType())) != null) {
            Boolean unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
            if (!Objects.equals(unwrapSingle, this._unwrapSingle)) {
                return _withResolved(property, unwrapSingle);
            }
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (_shouldUnwrapSingle(provider) && hasSingleElement(value)) {
            serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray(value);
        serializeContents(value, gen, provider);
        gen.writeEndArray();
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public final void serializeWithType(T value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
        g.setCurrentValue(value);
        serializeContents(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean _shouldUnwrapSingle(SerializerProvider provider) {
        if (this._unwrapSingle == null) {
            return provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        }
        return this._unwrapSingle.booleanValue();
    }
}
