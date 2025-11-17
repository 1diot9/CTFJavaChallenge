package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneOffset;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/ZoneOffsetKeyDeserializer.class */
public class ZoneOffsetKeyDeserializer extends Jsr310KeyDeserializer {
    public static final ZoneOffsetKeyDeserializer INSTANCE = new ZoneOffsetKeyDeserializer();

    private ZoneOffsetKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public ZoneOffset deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return ZoneOffset.of(key);
        } catch (DateTimeException e) {
            return (ZoneOffset) _handleDateTimeException(ctxt, ZoneOffset.class, e, key);
        }
    }
}
