package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/OffsetTimeSerializer.class */
public class OffsetTimeSerializer extends JSR310FormattedSerializerBase<OffsetTime> {
    private static final long serialVersionUID = 1;
    public static final OffsetTimeSerializer INSTANCE = new OffsetTimeSerializer();

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

    protected OffsetTimeSerializer() {
        super(OffsetTime.class);
    }

    protected OffsetTimeSerializer(OffsetTimeSerializer base, Boolean useTimestamp, DateTimeFormatter dtf) {
        this(base, useTimestamp, null, dtf);
    }

    protected OffsetTimeSerializer(OffsetTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf) {
        super(base, useTimestamp, useNanoseconds, dtf, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public OffsetTimeSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new OffsetTimeSerializer(this, useTimestamp, dtf);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(OffsetTime time, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            g.writeStartArray();
            _serializeAsArrayContents(time, g, provider);
            g.writeEndArray();
        } else {
            String str = this._formatter == null ? time.toString() : time.format(this._formatter);
            g.writeString(str);
        }
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase, com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(OffsetTime value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, serializationShape(provider)));
        if (typeIdDef.valueShape == JsonToken.START_ARRAY) {
            _serializeAsArrayContents(value, g, provider);
        } else {
            String str = this._formatter == null ? value.toString() : value.format(this._formatter);
            g.writeString(str);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    private final void _serializeAsArrayContents(OffsetTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeNumber(value.getHour());
        g.writeNumber(value.getMinute());
        int secs = value.getSecond();
        int nanos = value.getNano();
        if (secs > 0 || nanos > 0) {
            g.writeNumber(secs);
            if (nanos > 0) {
                if (useNanoseconds(provider)) {
                    g.writeNumber(nanos);
                } else {
                    g.writeNumber(value.get(ChronoField.MILLI_OF_SECOND));
                }
            }
        }
        g.writeString(value.getOffset().toString());
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    protected JsonToken serializationShape(SerializerProvider provider) {
        return useTimestamp(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new OffsetTimeSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter);
    }
}
