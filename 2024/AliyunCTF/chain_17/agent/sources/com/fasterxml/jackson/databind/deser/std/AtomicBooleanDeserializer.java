package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/AtomicBooleanDeserializer.class */
public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean> {
    private static final long serialVersionUID = 1;

    public AtomicBooleanDeserializer() {
        super((Class<?>) AtomicBoolean.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AtomicBoolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return new AtomicBoolean(true);
        }
        if (t == JsonToken.VALUE_FALSE) {
            return new AtomicBoolean(false);
        }
        Boolean b = _parseBoolean(p, ctxt, AtomicBoolean.class);
        if (b == null) {
            return null;
        }
        return new AtomicBoolean(b.booleanValue());
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Boolean;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return new AtomicBoolean(false);
    }
}
