package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/InstantKeyDeserializer.class */
public class InstantKeyDeserializer extends Jsr310KeyDeserializer {
    public static final InstantKeyDeserializer INSTANCE = new InstantKeyDeserializer();

    private InstantKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public Instant deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return (Instant) DateTimeFormatter.ISO_INSTANT.parse(key, Instant::from);
        } catch (DateTimeException e) {
            return (Instant) _handleDateTimeException(ctxt, Instant.class, e, key);
        }
    }
}
