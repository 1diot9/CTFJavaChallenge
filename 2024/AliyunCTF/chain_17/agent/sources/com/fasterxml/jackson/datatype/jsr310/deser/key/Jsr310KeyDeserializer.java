package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import java.io.IOException;
import java.time.DateTimeException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/Jsr310KeyDeserializer.class */
abstract class Jsr310KeyDeserializer extends KeyDeserializer {
    protected abstract Object deserialize(String str, DeserializationContext deserializationContext) throws IOException;

    @Override // com.fasterxml.jackson.databind.KeyDeserializer
    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if ("".equals(key)) {
            return null;
        }
        return deserialize(key, ctxt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T _handleDateTimeException(DeserializationContext deserializationContext, Class<?> cls, DateTimeException dateTimeException, String str) throws IOException {
        try {
            return (T) deserializationContext.handleWeirdKey(cls, str, "Failed to deserialize %s: (%s) %s", cls.getName(), dateTimeException.getClass().getName(), dateTimeException.getMessage());
        } catch (JsonMappingException e) {
            e.initCause(dateTimeException);
            throw e;
        } catch (IOException e2) {
            if (null == e2.getCause()) {
                e2.initCause(dateTimeException);
            }
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }
}
