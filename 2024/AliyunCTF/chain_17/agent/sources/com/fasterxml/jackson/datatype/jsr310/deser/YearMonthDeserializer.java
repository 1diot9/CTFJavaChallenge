package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/YearMonthDeserializer.class */
public class YearMonthDeserializer extends JSR310DateTimeDeserializerBase<YearMonth> {
    private static final long serialVersionUID = 1;
    public static final YearMonthDeserializer INSTANCE = new YearMonthDeserializer();

    public YearMonthDeserializer() {
        this(DateTimeFormatter.ofPattern("u-MM"));
    }

    public YearMonthDeserializer(DateTimeFormatter formatter) {
        super(YearMonth.class, formatter);
    }

    protected YearMonthDeserializer(YearMonthDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withDateFormat */
    public JSR310DateTimeDeserializerBase<YearMonth> withDateFormat2(DateTimeFormatter dtf) {
        return new YearMonthDeserializer(dtf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase, com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    public YearMonthDeserializer withLeniency(Boolean leniency) {
        return new YearMonthDeserializer(this, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withShape */
    public JSR310DateTimeDeserializerBase<YearMonth> withShape2(JsonFormat.Shape shape) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public YearMonth deserialize(JsonParser parser, DeserializationContext context) throws IOException {
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
                YearMonth parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t != JsonToken.VALUE_NUMBER_INT) {
                _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "years");
            }
            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            if (month == -1) {
                if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "months");
                }
                month = parser.getIntValue();
            }
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY, "Expected array to end");
            }
            return YearMonth.of(year, month);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (YearMonth) parser.getEmbeddedObject();
        }
        return (YearMonth) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.START_ARRAY);
    }

    protected YearMonth _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
        String string = string0.trim();
        if (string.length() == 0) {
            return _fromEmptyString(p, ctxt, string);
        }
        try {
            return YearMonth.parse(string, this._formatter);
        } catch (DateTimeException e) {
            return (YearMonth) _handleDateTimeException(ctxt, e, string);
        }
    }
}
