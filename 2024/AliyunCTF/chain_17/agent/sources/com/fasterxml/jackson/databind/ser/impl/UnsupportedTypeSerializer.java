package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/impl/UnsupportedTypeSerializer.class */
public class UnsupportedTypeSerializer extends StdSerializer<Object> {
    private static final long serialVersionUID = 1;
    protected final JavaType _type;
    protected final String _message;

    public UnsupportedTypeSerializer(JavaType t, String msg) {
        super(Object.class);
        this._type = t;
        this._message = msg;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Object value, JsonGenerator g, SerializerProvider ctxt) throws IOException {
        ctxt.reportBadDefinition(this._type, this._message);
    }
}
