package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.BeanProperty;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/TypeSerializer.class */
public abstract class TypeSerializer {
    public abstract TypeSerializer forProperty(BeanProperty beanProperty);

    public abstract JsonTypeInfo.As getTypeInclusion();

    public abstract String getPropertyName();

    public abstract TypeIdResolver getTypeIdResolver();

    public abstract WritableTypeId writeTypePrefix(JsonGenerator jsonGenerator, WritableTypeId writableTypeId) throws IOException;

    public abstract WritableTypeId writeTypeSuffix(JsonGenerator jsonGenerator, WritableTypeId writableTypeId) throws IOException;

    public WritableTypeId typeId(Object value, JsonToken valueShape) {
        WritableTypeId typeIdDef = new WritableTypeId(value, valueShape);
        switch (getTypeInclusion()) {
            case EXISTING_PROPERTY:
                typeIdDef.include = WritableTypeId.Inclusion.PAYLOAD_PROPERTY;
                typeIdDef.asProperty = getPropertyName();
                break;
            case EXTERNAL_PROPERTY:
                typeIdDef.include = WritableTypeId.Inclusion.PARENT_PROPERTY;
                typeIdDef.asProperty = getPropertyName();
                break;
            case PROPERTY:
                typeIdDef.include = WritableTypeId.Inclusion.METADATA_PROPERTY;
                typeIdDef.asProperty = getPropertyName();
                break;
            case WRAPPER_ARRAY:
                typeIdDef.include = WritableTypeId.Inclusion.WRAPPER_ARRAY;
                break;
            case WRAPPER_OBJECT:
                typeIdDef.include = WritableTypeId.Inclusion.WRAPPER_OBJECT;
                break;
            default:
                VersionUtil.throwInternal();
                break;
        }
        return typeIdDef;
    }

    public WritableTypeId typeId(Object value, JsonToken valueShape, Object id) {
        WritableTypeId typeId = typeId(value, valueShape);
        typeId.id = id;
        return typeId;
    }

    public WritableTypeId typeId(Object value, Class<?> typeForId, JsonToken valueShape) {
        WritableTypeId typeId = typeId(value, valueShape);
        typeId.forValueType = typeForId;
        return typeId;
    }

    @Deprecated
    public void writeTypePrefixForScalar(Object value, JsonGenerator g) throws IOException {
        writeTypePrefix(g, typeId(value, JsonToken.VALUE_STRING));
    }

    @Deprecated
    public void writeTypePrefixForObject(Object value, JsonGenerator g) throws IOException {
        writeTypePrefix(g, typeId(value, JsonToken.START_OBJECT));
    }

    @Deprecated
    public void writeTypePrefixForArray(Object value, JsonGenerator g) throws IOException {
        writeTypePrefix(g, typeId(value, JsonToken.START_ARRAY));
    }

    @Deprecated
    public void writeTypeSuffixForScalar(Object value, JsonGenerator g) throws IOException {
        _writeLegacySuffix(g, typeId(value, JsonToken.VALUE_STRING));
    }

    @Deprecated
    public void writeTypeSuffixForObject(Object value, JsonGenerator g) throws IOException {
        _writeLegacySuffix(g, typeId(value, JsonToken.START_OBJECT));
    }

    @Deprecated
    public void writeTypeSuffixForArray(Object value, JsonGenerator g) throws IOException {
        _writeLegacySuffix(g, typeId(value, JsonToken.START_ARRAY));
    }

    @Deprecated
    public void writeTypePrefixForScalar(Object value, JsonGenerator g, Class<?> type) throws IOException {
        writeTypePrefix(g, typeId(value, type, JsonToken.VALUE_STRING));
    }

    @Deprecated
    public void writeTypePrefixForObject(Object value, JsonGenerator g, Class<?> type) throws IOException {
        writeTypePrefix(g, typeId(value, type, JsonToken.START_OBJECT));
    }

    @Deprecated
    public void writeTypePrefixForArray(Object value, JsonGenerator g, Class<?> type) throws IOException {
        writeTypePrefix(g, typeId(value, type, JsonToken.START_ARRAY));
    }

    @Deprecated
    public void writeCustomTypePrefixForScalar(Object value, JsonGenerator g, String typeId) throws IOException {
        writeTypePrefix(g, typeId(value, JsonToken.VALUE_STRING, typeId));
    }

    @Deprecated
    public void writeCustomTypePrefixForObject(Object value, JsonGenerator g, String typeId) throws IOException {
        writeTypePrefix(g, typeId(value, JsonToken.START_OBJECT, typeId));
    }

    @Deprecated
    public void writeCustomTypePrefixForArray(Object value, JsonGenerator g, String typeId) throws IOException {
        writeTypePrefix(g, typeId(value, JsonToken.START_ARRAY, typeId));
    }

    @Deprecated
    public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator g, String typeId) throws IOException {
        _writeLegacySuffix(g, typeId(value, JsonToken.VALUE_STRING, typeId));
    }

    @Deprecated
    public void writeCustomTypeSuffixForObject(Object value, JsonGenerator g, String typeId) throws IOException {
        _writeLegacySuffix(g, typeId(value, JsonToken.START_OBJECT, typeId));
    }

    @Deprecated
    public void writeCustomTypeSuffixForArray(Object value, JsonGenerator g, String typeId) throws IOException {
        _writeLegacySuffix(g, typeId(value, JsonToken.START_ARRAY, typeId));
    }

    protected final void _writeLegacySuffix(JsonGenerator g, WritableTypeId typeId) throws IOException {
        typeId.wrapperWritten = !g.canWriteTypeId();
        writeTypeSuffix(g, typeId);
    }
}
