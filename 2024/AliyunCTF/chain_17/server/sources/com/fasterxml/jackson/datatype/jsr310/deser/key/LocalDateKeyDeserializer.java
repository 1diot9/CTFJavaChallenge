package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/LocalDateKeyDeserializer.class */
public class LocalDateKeyDeserializer extends Jsr310KeyDeserializer {
    public static final LocalDateKeyDeserializer INSTANCE = new LocalDateKeyDeserializer();

    private LocalDateKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public LocalDate deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return LocalDate.parse(key, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeException e) {
            return (LocalDate) _handleDateTimeException(ctxt, LocalDate.class, e, key);
        }
    }
}
