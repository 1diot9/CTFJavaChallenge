package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/LocalTimeKeyDeserializer.class */
public class LocalTimeKeyDeserializer extends Jsr310KeyDeserializer {
    public static final LocalTimeKeyDeserializer INSTANCE = new LocalTimeKeyDeserializer();

    private LocalTimeKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public LocalTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return LocalTime.parse(key, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeException e) {
            return (LocalTime) _handleDateTimeException(ctxt, LocalTime.class, e, key);
        }
    }
}
