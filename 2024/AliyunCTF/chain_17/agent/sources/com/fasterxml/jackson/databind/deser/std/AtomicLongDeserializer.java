package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/AtomicLongDeserializer.class */
public class AtomicLongDeserializer extends StdScalarDeserializer<AtomicLong> {
    private static final long serialVersionUID = 1;

    public AtomicLongDeserializer() {
        super((Class<?>) AtomicLong.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AtomicLong deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.isExpectedNumberIntToken()) {
            return new AtomicLong(p.getLongValue());
        }
        Long L = _parseLong(p, ctxt, AtomicLong.class);
        if (L == null) {
            return null;
        }
        return new AtomicLong(L.intValue());
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Integer;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return new AtomicLong();
    }
}
