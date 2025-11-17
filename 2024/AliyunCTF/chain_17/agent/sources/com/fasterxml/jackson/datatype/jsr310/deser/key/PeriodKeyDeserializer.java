package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Period;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/PeriodKeyDeserializer.class */
public class PeriodKeyDeserializer extends Jsr310KeyDeserializer {
    public static final PeriodKeyDeserializer INSTANCE = new PeriodKeyDeserializer();

    private PeriodKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public Period deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return Period.parse(key);
        } catch (DateTimeException e) {
            return (Period) _handleDateTimeException(ctxt, Period.class, e, key);
        }
    }
}
