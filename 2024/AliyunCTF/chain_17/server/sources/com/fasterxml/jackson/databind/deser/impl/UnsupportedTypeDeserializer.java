package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/UnsupportedTypeDeserializer.class */
public class UnsupportedTypeDeserializer extends StdDeserializer<Object> {
    private static final long serialVersionUID = 1;
    protected final JavaType _type;
    protected final String _message;

    public UnsupportedTypeDeserializer(JavaType t, String m) {
        super(t);
        this._type = t;
        this._message = m;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object value;
        if (p.currentToken() == JsonToken.VALUE_EMBEDDED_OBJECT && ((value = p.getEmbeddedObject()) == null || this._type.getRawClass().isAssignableFrom(value.getClass()))) {
            return value;
        }
        ctxt.reportBadDefinition(this._type, this._message);
        return null;
    }
}
