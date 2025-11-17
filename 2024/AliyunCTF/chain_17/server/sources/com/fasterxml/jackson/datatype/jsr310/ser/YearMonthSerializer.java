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
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/YearMonthSerializer.class */
public class YearMonthSerializer extends JSR310FormattedSerializerBase<YearMonth> {
    private static final long serialVersionUID = 1;
    public static final YearMonthSerializer INSTANCE = new YearMonthSerializer();

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

    protected YearMonthSerializer() {
        this(null);
    }

    public YearMonthSerializer(DateTimeFormatter formatter) {
        super(YearMonth.class, formatter);
    }

    private YearMonthSerializer(YearMonthSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
        super(base, useTimestamp, formatter, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public YearMonthSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new YearMonthSerializer(this, useTimestamp, formatter);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(YearMonth value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            g.writeStartArray();
            _serializeAsArrayContents(value, g, provider);
            g.writeEndArray();
            return;
        }
        g.writeString(this._formatter == null ? value.toString() : value.format(this._formatter));
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase, com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(YearMonth value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, serializationShape(provider)));
        if (typeIdDef.valueShape == JsonToken.START_ARRAY) {
            _serializeAsArrayContents(value, g, provider);
        } else {
            g.writeString(this._formatter == null ? value.toString() : value.format(this._formatter));
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    protected void _serializeAsArrayContents(YearMonth value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeNumber(value.getYear());
        g.writeNumber(value.getMonthValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        SerializerProvider provider = visitor.getProvider();
        boolean useTimestamp = provider != null && useTimestamp(provider);
        if (useTimestamp) {
            super._acceptTimestampVisitor(visitor, typeHint);
            return;
        }
        JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
        if (v2 != null) {
            v2.format(JsonValueFormat.DATE_TIME);
        }
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    protected JsonToken serializationShape(SerializerProvider provider) {
        return useTimestamp(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
    }
}
