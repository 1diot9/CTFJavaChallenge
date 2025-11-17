package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/key/YearMonthKeyDeserializer.class */
public class YearMonthKeyDeserializer extends Jsr310KeyDeserializer {
    public static final YearMonthKeyDeserializer INSTANCE = new YearMonthKeyDeserializer();
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).toFormatter();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.key.Jsr310KeyDeserializer
    public YearMonth deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return YearMonth.parse(key, FORMATTER);
        } catch (DateTimeException e) {
            return (YearMonth) _handleDateTimeException(ctxt, YearMonth.class, e, key);
        }
    }
}
