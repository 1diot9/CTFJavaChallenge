package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/MonthDayDeserializer.class */
public class MonthDayDeserializer extends JSR310DateTimeDeserializerBase<MonthDay> {
    private static final long serialVersionUID = 1;
    public static final MonthDayDeserializer INSTANCE = new MonthDayDeserializer();

    public MonthDayDeserializer() {
        this(null);
    }

    public MonthDayDeserializer(DateTimeFormatter formatter) {
        super(MonthDay.class, formatter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withDateFormat, reason: merged with bridge method [inline-methods] */
    public JSR310DateTimeDeserializerBase<MonthDay> withDateFormat2(DateTimeFormatter dtf) {
        return new MonthDayDeserializer(dtf);
    }

    protected MonthDayDeserializer(MonthDayDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase, com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    public MonthDayDeserializer withLeniency(Boolean leniency) {
        return new MonthDayDeserializer(this, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withShape, reason: merged with bridge method [inline-methods] */
    public JSR310DateTimeDeserializerBase<MonthDay> withShape2(JsonFormat.Shape shape) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public MonthDay deserialize(JsonParser parser, DeserializationContext context) throws IOException {
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
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                MonthDay parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t != JsonToken.VALUE_NUMBER_INT) {
                _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "month");
            }
            int month = parser.getIntValue();
            int day = parser.nextIntValue(-1);
            if (day == -1) {
                if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "day");
                }
                day = parser.getIntValue();
            }
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY, "Expected array to end");
            }
            return MonthDay.of(month, day);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (MonthDay) parser.getEmbeddedObject();
        }
        return (MonthDay) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.START_ARRAY);
    }

    protected MonthDay _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
        String string = string0.trim();
        if (string.length() == 0) {
            return _fromEmptyString(p, ctxt, string);
        }
        try {
            if (this._formatter == null) {
                return MonthDay.parse(string);
            }
            return MonthDay.parse(string, this._formatter);
        } catch (DateTimeException e) {
            return (MonthDay) _handleDateTimeException(ctxt, e, string);
        }
    }
}
