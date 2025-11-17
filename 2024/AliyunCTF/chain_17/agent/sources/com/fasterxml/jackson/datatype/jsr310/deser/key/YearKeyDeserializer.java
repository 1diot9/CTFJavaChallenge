package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/YearKeyDeserializer.class */
public class YearKeyDeserializer extends Jsr310KeyDeserializer {
    public static final YearKeyDeserializer INSTANCE = new YearKeyDeserializer();

    protected YearKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public Year deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return Year.of(Integer.parseInt(key));
        } catch (NumberFormatException nfe) {
            return (Year) _handleDateTimeException(ctxt, Year.class, new DateTimeException("Number format exception", nfe), key);
        } catch (DateTimeException dte) {
            return (Year) _handleDateTimeException(ctxt, Year.class, dte, key);
        }
    }
}
