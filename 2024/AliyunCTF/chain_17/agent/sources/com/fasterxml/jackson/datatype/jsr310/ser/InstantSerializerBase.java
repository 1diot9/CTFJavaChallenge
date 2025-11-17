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
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/InstantSerializerBase.class */
public abstract class InstantSerializerBase<T extends Temporal> extends JSR310FormattedSerializerBase<T> {
    private final DateTimeFormatter defaultFormat;
    private final ToLongFunction<T> getEpochMillis;
    private final ToLongFunction<T> getEpochSeconds;
    private final ToIntFunction<T> getNanoseconds;

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean bool, DateTimeFormatter dateTimeFormatter, JsonFormat.Shape shape);

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public /* bridge */ /* synthetic */ void acceptJsonFormatVisitor(JsonFormatVisitorWrapper jsonFormatVisitorWrapper, JavaType javaType) throws JsonMappingException {
        super.acceptJsonFormatVisitor(jsonFormatVisitorWrapper, javaType);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    public /* bridge */ /* synthetic */ JsonNode getSchema(SerializerProvider serializerProvider, Type type) {
        return super.getSchema(serializerProvider, type);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.ContextualSerializer
    public /* bridge */ /* synthetic */ JsonSerializer createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return super.createContextual(serializerProvider, beanProperty);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InstantSerializerBase(Class<T> supportedType, ToLongFunction<T> getEpochMillis, ToLongFunction<T> getEpochSeconds, ToIntFunction<T> getNanoseconds, DateTimeFormatter formatter) {
        super(supportedType, null);
        this.defaultFormat = formatter;
        this.getEpochMillis = getEpochMillis;
        this.getEpochSeconds = getEpochSeconds;
        this.getNanoseconds = getNanoseconds;
    }

    protected InstantSerializerBase(InstantSerializerBase<T> base, Boolean useTimestamp, DateTimeFormatter dtf) {
        this(base, useTimestamp, base._useNanoseconds, dtf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InstantSerializerBase(InstantSerializerBase<T> base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf) {
        this(base, useTimestamp, useNanoseconds, dtf, base._shape);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InstantSerializerBase(InstantSerializerBase<T> base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        super(base, useTimestamp, useNanoseconds, dtf, shape);
        this.defaultFormat = base.defaultFormat;
        this.getEpochMillis = base.getEpochMillis;
        this.getEpochSeconds = base.getEpochSeconds;
        this.getNanoseconds = base.getNanoseconds;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(this.getEpochSeconds.applyAsLong(value), this.getNanoseconds.applyAsInt(value)));
                return;
            } else {
                generator.writeNumber(this.getEpochMillis.applyAsLong(value));
                return;
            }
        }
        generator.writeString(formatValue(value, provider));
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        if (useNanoseconds(visitor.getProvider())) {
            JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
            if (v2 != null) {
                v2.numberType(JsonParser.NumberType.BIG_DECIMAL);
                return;
            }
            return;
        }
        JsonIntegerFormatVisitor v22 = visitor.expectIntegerFormat(typeHint);
        if (v22 != null) {
            v22.numberType(JsonParser.NumberType.LONG);
            v22.format(JsonValueFormat.UTC_MILLISEC);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    public JsonToken serializationShape(SerializerProvider provider) {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                return JsonToken.VALUE_NUMBER_FLOAT;
            }
            return JsonToken.VALUE_NUMBER_INT;
        }
        return JsonToken.VALUE_STRING;
    }

    protected String formatValue(T value, SerializerProvider provider) {
        DateTimeFormatter formatter = this._formatter != null ? this._formatter : this.defaultFormat;
        if (formatter != null) {
            if (formatter.getZone() == null && provider.getConfig().hasExplicitTimeZone() && provider.isEnabled(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)) {
                formatter = formatter.withZone(provider.getTimeZone().toZoneId());
            }
            return formatter.format(value);
        }
        return value.toString();
    }
}
