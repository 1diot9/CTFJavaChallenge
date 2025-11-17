package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NullifyingDeserializer.class */
public class NullifyingDeserializer extends StdDeserializer<Object> {
    private static final long serialVersionUID = 1;
    public static final NullifyingDeserializer instance = new NullifyingDeserializer();

    public NullifyingDeserializer() {
        super((Class<?>) Object.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.hasToken(JsonToken.FIELD_NAME)) {
            p.skipChildren();
            return null;
        }
        while (true) {
            JsonToken t = p.nextToken();
            if (t != null && t != JsonToken.END_OBJECT) {
                p.skipChildren();
            } else {
                return null;
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        switch (p.currentTokenId()) {
            case 1:
            case 3:
            case 5:
                return typeDeserializer.deserializeTypedFromAny(p, ctxt);
            case 2:
            case 4:
            default:
                return null;
        }
    }
}
