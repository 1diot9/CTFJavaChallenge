package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/AsExternalTypeSerializer.class */
public class AsExternalTypeSerializer extends TypeSerializerBase {
    protected final String _typePropertyName;

    public AsExternalTypeSerializer(TypeIdResolver idRes, BeanProperty property, String propName) {
        super(idRes, property);
        this._typePropertyName = propName;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public AsExternalTypeSerializer forProperty(BeanProperty prop) {
        return this._property == prop ? this : new AsExternalTypeSerializer(this._idResolver, prop, this._typePropertyName);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.TypeSerializerBase, com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public String getPropertyName() {
        return this._typePropertyName;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.TypeSerializerBase, com.fasterxml.jackson.databind.jsontype.TypeSerializer
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXTERNAL_PROPERTY;
    }

    protected final void _writeScalarPrefix(Object value, JsonGenerator g) throws IOException {
    }

    protected final void _writeObjectPrefix(Object value, JsonGenerator g) throws IOException {
        g.writeStartObject();
    }

    protected final void _writeArrayPrefix(Object value, JsonGenerator g) throws IOException {
        g.writeStartArray();
    }

    protected final void _writeScalarSuffix(Object value, JsonGenerator g, String typeId) throws IOException {
        if (typeId != null) {
            g.writeStringField(this._typePropertyName, typeId);
        }
    }

    protected final void _writeObjectSuffix(Object value, JsonGenerator g, String typeId) throws IOException {
        g.writeEndObject();
        if (typeId != null) {
            g.writeStringField(this._typePropertyName, typeId);
        }
    }

    protected final void _writeArraySuffix(Object value, JsonGenerator g, String typeId) throws IOException {
        g.writeEndArray();
        if (typeId != null) {
            g.writeStringField(this._typePropertyName, typeId);
        }
    }
}
