package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;
import java.time.format.DateTimeFormatter;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/YearDeserializer.class */
public class YearDeserializer extends JSR310DateTimeDeserializerBase<Year> {
    private static final long serialVersionUID = 1;
    public static final YearDeserializer INSTANCE = new YearDeserializer();

    public YearDeserializer() {
        this(null);
    }

    public YearDeserializer(DateTimeFormatter formatter) {
        super(Year.class, formatter);
    }

    protected YearDeserializer(YearDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withDateFormat */
    public JSR310DateTimeDeserializerBase<Year> withDateFormat2(DateTimeFormatter dtf) {
        return new YearDeserializer(dtf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase, com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    public YearDeserializer withLeniency(Boolean leniency) {
        return new YearDeserializer(this, leniency);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    /* renamed from: withShape */
    public JSR310DateTimeDeserializerBase<Year> withShape2(JsonFormat.Shape shape) {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Year deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken t = parser.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            return _fromString(parser, context, parser.getText());
        }
        if (t == JsonToken.START_OBJECT) {
            return _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return _fromNumber(context, parser.getIntValue());
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return (Year) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.START_ARRAY)) {
            return _deserializeFromArray(parser, context);
        }
        return (Year) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT);
    }

    protected Year _fromString(JsonParser p, DeserializationContext ctxt, String string0) throws IOException {
        String string = string0.trim();
        if (string.length() == 0) {
            return _fromEmptyString(p, ctxt, string);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && _isValidTimestampString(string)) {
            return _fromNumber(ctxt, NumberInput.parseInt(string));
        }
        try {
            if (this._formatter == null) {
                return Year.parse(string);
            }
            return Year.parse(string, this._formatter);
        } catch (DateTimeException e) {
            return (Year) _handleDateTimeException(ctxt, e, string);
        }
    }

    protected Year _fromNumber(DeserializationContext ctxt, int value) {
        return Year.of(value);
    }
}
