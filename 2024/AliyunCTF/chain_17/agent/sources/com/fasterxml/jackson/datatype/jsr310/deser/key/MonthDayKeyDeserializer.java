package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/MonthDayKeyDeserializer.class */
public class MonthDayKeyDeserializer extends Jsr310KeyDeserializer {
    public static final MonthDayKeyDeserializer INSTANCE = new MonthDayKeyDeserializer();
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder().appendLiteral("--").appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter();

    private MonthDayKeyDeserializer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public MonthDay deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return MonthDay.parse(key, PARSER);
        } catch (DateTimeException e) {
            return (MonthDay) _handleDateTimeException(ctxt, MonthDay.class, e, key);
        }
    }
}
