package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.util.DurationUnitConverter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/DurationSerializer.class */
public class DurationSerializer extends JSR310FormattedSerializerBase<Duration> {
    private static final long serialVersionUID = 1;
    public static final DurationSerializer INSTANCE = new DurationSerializer();
    private DurationUnitConverter _durationUnitConverter;

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public /* bridge */ /* synthetic */ void acceptJsonFormatVisitor(JsonFormatVisitorWrapper jsonFormatVisitorWrapper, JavaType javaType) throws JsonMappingException {
        super.acceptJsonFormatVisitor(jsonFormatVisitorWrapper, javaType);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    public /* bridge */ /* synthetic */ JsonNode getSchema(SerializerProvider serializerProvider, Type type) {
        return super.getSchema(serializerProvider, type);
    }

    protected DurationSerializer() {
        super(Duration.class);
    }

    protected DurationSerializer(DurationSerializer base, Boolean useTimestamp, DateTimeFormatter dtf) {
        super(base, useTimestamp, dtf, null);
    }

    protected DurationSerializer(DurationSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf) {
        super(base, useTimestamp, useNanoseconds, dtf, null);
    }

    protected DurationSerializer(DurationSerializer base, DurationUnitConverter converter) {
        super(base, base._useTimestamp, base._useNanoseconds, base._formatter, base._shape);
        this._durationUnitConverter = converter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public DurationSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new DurationSerializer(this, useTimestamp, dtf);
    }

    protected DurationSerializer withConverter(DurationUnitConverter converter) {
        return new DurationSerializer(this, converter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected SerializationFeature getTimestampsFeature() {
        return SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        DurationSerializer ser = (DurationSerializer) super.createContextual(prov, property);
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null && format.hasPattern()) {
            String pattern = format.getPattern();
            DurationUnitConverter p = DurationUnitConverter.from(pattern);
            if (p == null) {
                prov.reportBadDefinition(handledType(), String.format("Bad 'pattern' definition (\"%s\") for `Duration`: expected one of [%s]", pattern, DurationUnitConverter.descForAllowed()));
            }
            ser = ser.withConverter(p);
        }
        return ser;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            if (this._durationUnitConverter != null) {
                generator.writeNumber(this._durationUnitConverter.convert(duration));
                return;
            } else if (useNanoseconds(provider)) {
                generator.writeNumber(_toNanos(duration));
                return;
            } else {
                generator.writeNumber(duration.toMillis());
                return;
            }
        }
        generator.writeString(duration.toString());
    }

    private BigDecimal _toNanos(Duration duration) {
        BigDecimal bd;
        if (duration.isNegative()) {
            Duration duration2 = duration.abs();
            bd = DecimalUtils.toBigDecimal(duration2.getSeconds(), duration2.getNano()).negate();
        } else {
            bd = DecimalUtils.toBigDecimal(duration.getSeconds(), duration.getNano());
        }
        return bd;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (v2 != null) {
            v2.numberType(JsonParser.NumberType.LONG);
            SerializerProvider provider = visitor.getProvider();
            if (provider == null || !useNanoseconds(provider)) {
                v2.format(JsonValueFormat.UTC_MILLISEC);
            }
        }
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    protected JsonToken serializationShape(SerializerProvider provider) {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                return JsonToken.VALUE_NUMBER_FLOAT;
            }
            return JsonToken.VALUE_NUMBER_INT;
        }
        return JsonToken.VALUE_STRING;
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new DurationSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected DateTimeFormatter _useDateTimeFormatter(SerializerProvider prov, JsonFormat.Value format) {
        return null;
    }
}
