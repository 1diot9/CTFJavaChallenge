package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/LocalDateDeserializer.class */
public class LocalDateDeserializer extends JSR310DateTimeDeserializerBase<LocalDate> {
    private static final long serialVersionUID = 1;
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final LocalDateDeserializer INSTANCE = new LocalDateDeserializer();

    protected LocalDateDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalDateDeserializer(DateTimeFormatter dtf) {
        super(LocalDate.class, dtf);
    }

    public LocalDateDeserializer(LocalDateDeserializer base, DateTimeFormatter dtf) {
        super(base, dtf);
    }

    protected LocalDateDeserializer(LocalDateDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    protected LocalDateDeserializer(LocalDateDeserializer base, JsonFormat.Shape shape) {
        super(base, shape);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withDateFormat */
    public JSR310DateTimeDeserializerBase<LocalDate> withDateFormat2(DateTimeFormatter dtf) {
        return new LocalDateDeserializer(this, dtf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase, com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    public LocalDateDeserializer withLeniency(Boolean leniency) {
        return new LocalDateDeserializer(this, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withShape */
    public JSR310DateTimeDeserializerBase<LocalDate> withShape2(JsonFormat.Shape shape) {
        return new LocalDateDeserializer(this, shape);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
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
                LocalDate parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                int year = parser.getIntValue();
                int month = parser.nextIntValue(-1);
                int day = parser.nextIntValue(-1);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY, "Expected array to end");
                }
                return LocalDate.of(year, month, day);
            }
            context.reportInputMismatch(handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDate) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            CoercionAction act = context.findCoercionAction(logicalType(), this._valueClass, CoercionInputShape.Integer);
            _checkCoercionFail(context, act, handledType(), Long.valueOf(parser.getLongValue()), "Integer value (" + parser.getLongValue() + ")");
            if (this._shape == JsonFormat.Shape.NUMBER_INT || isLenient()) {
                return LocalDate.ofEpochDay(parser.getLongValue());
            }
            return _failForNotLenient(parser, context, JsonToken.VALUE_STRING);
        }
        return (LocalDate) _handleUnexpectedToken(context, parser, "Expected array or string.", new Object[0]);
    }

    protected LocalDate _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
        String string = string0.trim();
        if (string.length() == 0) {
            return _fromEmptyString(p, ctxt, string);
        }
        try {
            DateTimeFormatter format = this._formatter;
            if (format == DEFAULT_FORMATTER && string.length() > 10 && string.charAt(10) == 'T') {
                if (isLenient()) {
                    if (string.endsWith("Z")) {
                        return LocalDate.parse(string.substring(0, string.length() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                JavaType t = getValueType(ctxt);
                return (LocalDate) ctxt.handleWeirdStringValue(t.getRawClass(), string, "Should not contain time component when 'strict' mode set for property or type (enable 'lenient' handling to allow)", new Object[0]);
            }
            return LocalDate.parse(string, format);
        } catch (DateTimeException e) {
            return (LocalDate) _handleDateTimeException(ctxt, e, string);
        }
    }
}
