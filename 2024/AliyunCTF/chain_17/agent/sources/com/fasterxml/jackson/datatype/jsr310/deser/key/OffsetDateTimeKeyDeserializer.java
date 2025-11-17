package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/OffsetDateTimeKeyDeserializer.class */
public class OffsetDateTimeKeyDeserializer extends Jsr310KeyDeserializer {
    public static final OffsetDateTimeKeyDeserializer INSTANCE = new OffsetDateTimeKeyDeserializer();

    private OffsetDateTimeKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public OffsetDateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return OffsetDateTime.parse(key, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeException e) {
            return (OffsetDateTime) _handleDateTimeException(ctxt, OffsetDateTime.class, e, key);
        }
    }
}
