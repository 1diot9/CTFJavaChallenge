package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/ZonedDateTimeKeyDeserializer.class */
public class ZonedDateTimeKeyDeserializer extends Jsr310KeyDeserializer {
    public static final ZonedDateTimeKeyDeserializer INSTANCE = new ZonedDateTimeKeyDeserializer();

    private ZonedDateTimeKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public ZonedDateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return ZonedDateTime.parse(key, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeException e) {
            return (ZonedDateTime) _handleDateTimeException(ctxt, ZonedDateTime.class, e, key);
        }
    }
}
