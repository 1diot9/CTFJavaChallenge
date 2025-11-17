package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/TypeSerializerBase.class */
public abstract class TypeSerializerBase extends TypeSerializer {
    protected final TypeIdResolver _idResolver;
    protected final BeanProperty _property;

    @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public abstract JsonTypeInfo.As getTypeInclusion();

    /* JADX INFO: Access modifiers changed from: protected */
    public TypeSerializerBase(TypeIdResolver idRes, BeanProperty property) {
        this._idResolver = idRes;
        this._property = property;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public String getPropertyName() {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public TypeIdResolver getTypeIdResolver() {
        return this._idResolver;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
        _generateTypeId(idMetadata);
        if (idMetadata.id == null) {
            return null;
        }
        return g.writeTypePrefix(idMetadata);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
        if (idMetadata == null) {
            return null;
        }
        return g.writeTypeSuffix(idMetadata);
    }

    protected void _generateTypeId(WritableTypeId idMetadata) {
        Object id;
        Object id2 = idMetadata.id;
        if (id2 == null) {
            Object value = idMetadata.forValue;
            Class<?> typeForId = idMetadata.forValueType;
            if (typeForId == null) {
                id = idFromValue(value);
            } else {
                id = idFromValueAndType(value, typeForId);
            }
            idMetadata.id = id;
        }
    }

    protected String idFromValue(Object value) {
        String id = this._idResolver.idFromValue(value);
        if (id == null) {
            handleMissingId(value);
        }
        return id;
    }

    protected String idFromValueAndType(Object value, Class<?> type) {
        String id = this._idResolver.idFromValueAndType(value, type);
        if (id == null) {
            handleMissingId(value);
        }
        return id;
    }

    protected void handleMissingId(Object value) {
    }
}
