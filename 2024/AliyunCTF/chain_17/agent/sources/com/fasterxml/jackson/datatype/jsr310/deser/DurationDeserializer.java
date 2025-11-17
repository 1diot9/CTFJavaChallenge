package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.util.DurationUnitConverter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Duration;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/deser/DurationDeserializer.class */
public class DurationDeserializer extends JSR310DeserializerBase<Duration> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    public static final DurationDeserializer INSTANCE = new DurationDeserializer();
    protected final DurationUnitConverter _durationUnitConverter;

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException {
        return super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public /* bridge */ /* synthetic */ LogicalType logicalType() {
        return super.logicalType();
    }

    public DurationDeserializer() {
        super(Duration.class);
        this._durationUnitConverter = null;
    }

    protected DurationDeserializer(DurationDeserializer base, Boolean leniency) {
        super(base, leniency);
        this._durationUnitConverter = base._durationUnitConverter;
    }

    protected DurationDeserializer(DurationDeserializer base, DurationUnitConverter converter) {
        super(base, Boolean.valueOf(base._isLenient));
        this._durationUnitConverter = converter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DeserializerBase
    /* renamed from: withLeniency */
    public JSR310DeserializerBase<Duration> withLeniency2(Boolean leniency) {
        return new DurationDeserializer(this, leniency);
    }

    protected DurationDeserializer withConverter(DurationUnitConverter converter) {
        return new DurationDeserializer(this, converter);
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Boolean leniency;
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        DurationDeserializer deser = this;
        if (format != null) {
            if (format.hasLenient() && (leniency = format.getLenient()) != null) {
                deser = deser.withLeniency2(leniency);
            }
            if (format.hasPattern()) {
                String pattern = format.getPattern();
                DurationUnitConverter p = DurationUnitConverter.from(pattern);
                if (p == null) {
                    ctxt.reportBadDefinition(getValueType(ctxt), String.format("Bad 'pattern' definition (\"%s\") for `Duration`: expected one of [%s]", pattern, DurationUnitConverter.descForAllowed()));
                }
                deser = deser.withConverter(p);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        switch (parser.currentTokenId()) {
            case 1:
                return _fromString(parser, context, context.extractScalarFromObject(parser, this, handledType()));
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            case 11:
            default:
                return (Duration) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT);
            case 3:
                return _deserializeFromArray(parser, context);
            case 6:
                return _fromString(parser, context, parser.getText());
            case 7:
                return _fromTimestamp(context, parser.getLongValue());
            case 8:
                BigDecimal value = parser.getDecimalValue();
                return (Duration) DecimalUtils.extractSecondsAndNanos(value, (v0, v1) -> {
                    return Duration.ofSeconds(v0, v1);
                });
            case 12:
                return (Duration) parser.getEmbeddedObject();
        }
    }

    protected Duration _fromString(JsonParser parser, DeserializationContext ctxt, String value0) throws IOException {
        String value = value0.trim();
        if (value.length() == 0) {
            return _fromEmptyString(parser, ctxt, value);
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS) && _isValidTimestampString(value)) {
            return _fromTimestamp(ctxt, NumberInput.parseLong(value));
        }
        try {
            return Duration.parse(value);
        } catch (DateTimeException e) {
            return (Duration) _handleDateTimeException(ctxt, e, value);
        }
    }

    protected Duration _fromTimestamp(DeserializationContext ctxt, long ts) {
        if (this._durationUnitConverter != null) {
            return this._durationUnitConverter.convert(ts);
        }
        if (ctxt.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            return Duration.ofSeconds(ts);
        }
        return Duration.ofMillis(ts);
    }
}
