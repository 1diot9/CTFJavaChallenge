package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/JSONPObject.class */
public class JSONPObject implements JsonSerializable {
    protected final String _function;
    protected final Object _value;
    protected final JavaType _serializationType;

    public JSONPObject(String function, Object value) {
        this(function, value, (JavaType) null);
    }

    public JSONPObject(String function, Object value, JavaType asType) {
        this._function = function;
        this._value = value;
        this._serializationType = asType;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        serialize(gen, provider);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeRaw(this._function);
        gen.writeRaw('(');
        if (this._value == null) {
            provider.defaultSerializeNull(gen);
        } else {
            boolean override = gen.getCharacterEscapes() == null;
            if (override) {
                gen.setCharacterEscapes(JsonpCharacterEscapes.instance());
            }
            try {
                if (this._serializationType != null) {
                    provider.findTypedValueSerializer(this._serializationType, true, (BeanProperty) null).serialize(this._value, gen, provider);
                } else {
                    provider.findTypedValueSerializer(this._value.getClass(), true, (BeanProperty) null).serialize(this._value, gen, provider);
                }
            } finally {
                if (override) {
                    gen.setCharacterEscapes(null);
                }
            }
        }
        gen.writeRaw(')');
    }

    public String getFunction() {
        return this._function;
    }

    public Object getValue() {
        return this._value;
    }

    public JavaType getSerializationType() {
        return this._serializationType;
    }
}
