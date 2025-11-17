package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/ZoneIdKeyDeserializer.class */
public class ZoneIdKeyDeserializer extends Jsr310KeyDeserializer {
    public static final ZoneIdKeyDeserializer INSTANCE = new ZoneIdKeyDeserializer();

    private ZoneIdKeyDeserializer() {
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    protected Object deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return ZoneId.of(key);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, ZoneId.class, e, key);
        }
    }
}
