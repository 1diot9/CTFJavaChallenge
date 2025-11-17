package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/LocalTimeDeserializer.class */
public class LocalTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalTime> {
    private static final long serialVersionUID = 1;
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
    public static final LocalTimeDeserializer INSTANCE = new LocalTimeDeserializer();

    protected LocalTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalTime.class, formatter);
    }

    protected LocalTimeDeserializer(LocalTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withDateFormat */
    public JSR310DateTimeDeserializerBase<LocalTime> withDateFormat2(DateTimeFormatter formatter) {
        return new LocalTimeDeserializer(formatter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase, com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    public LocalTimeDeserializer withLeniency(Boolean leniency) {
        return new LocalTimeDeserializer(this, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withShape */
    public JSR310DateTimeDeserializerBase<LocalTime> withShape2(JsonFormat.Shape shape) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        LocalTime result;
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(parser, context, parser.getText());
        }
        if (parser.isExpectedStartObjectToken()) {
            return _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if (context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS) && (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)) {
                LocalTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                int hour = parser.getIntValue();
                parser.nextToken();
                int minute = parser.getIntValue();
                if (parser.nextToken() == JsonToken.END_ARRAY) {
                    result = LocalTime.of(hour, minute);
                } else {
                    int second = parser.getIntValue();
                    if (parser.nextToken() == JsonToken.END_ARRAY) {
                        result = LocalTime.of(hour, minute, second);
                    } else {
                        int partialSecond = parser.getIntValue();
                        if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                            partialSecond *= 1000000;
                        }
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY, "Expected array to end");
                        }
                        result = LocalTime.of(hour, minute, second, partialSecond);
                    }
                }
                return result;
            }
            context.reportInputMismatch(handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalTime) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(parser, context);
        }
        return (LocalTime) _handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
    }

    protected LocalTime _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
        String string = string0.trim();
        if (string.length() == 0) {
            return _fromEmptyString(p, ctxt, string);
        }
        DateTimeFormatter format = this._formatter;
        try {
            if (format == DEFAULT_FORMATTER && string.contains("T")) {
                return LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            return LocalTime.parse(string, format);
        } catch (DateTimeException e) {
            return (LocalTime) _handleDateTimeException(ctxt, e, string);
        }
    }
}
