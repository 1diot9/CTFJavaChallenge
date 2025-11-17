package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/JSONWrappedObject.class */
public class JSONWrappedObject implements JsonSerializable {
    protected final String _prefix;
    protected final String _suffix;
    protected final Object _value;
    protected final JavaType _serializationType;

    public JSONWrappedObject(String prefix, String suffix, Object value) {
        this(prefix, suffix, value, (JavaType) null);
    }

    public JSONWrappedObject(String prefix, String suffix, Object value, JavaType asType) {
        this._prefix = prefix;
        this._suffix = suffix;
        this._value = value;
        this._serializationType = asType;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        serialize(jgen, provider);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (this._prefix != null) {
            jgen.writeRaw(this._prefix);
        }
        if (this._value == null) {
            provider.defaultSerializeNull(jgen);
        } else if (this._serializationType != null) {
            provider.findTypedValueSerializer(this._serializationType, true, (BeanProperty) null).serialize(this._value, jgen, provider);
        } else {
            Class<?> cls = this._value.getClass();
            provider.findTypedValueSerializer(cls, true, (BeanProperty) null).serialize(this._value, jgen, provider);
        }
        if (this._suffix != null) {
            jgen.writeRaw(this._suffix);
        }
    }

    public String getPrefix() {
        return this._prefix;
    }

    public String getSuffix() {
        return this._suffix;
    }

    public Object getValue() {
        return this._value;
    }

    public JavaType getSerializationType() {
        return this._serializationType;
    }
}
