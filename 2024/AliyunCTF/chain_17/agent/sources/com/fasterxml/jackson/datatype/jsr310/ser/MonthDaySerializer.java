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
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/MonthDaySerializer.class */
public class MonthDaySerializer extends JSR310FormattedSerializerBase<MonthDay> {
    private static final long serialVersionUID = 1;
    public static final MonthDaySerializer INSTANCE = new MonthDaySerializer();

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

    protected MonthDaySerializer() {
        this(null);
    }

    public MonthDaySerializer(DateTimeFormatter formatter) {
        super(MonthDay.class, formatter);
    }

    private MonthDaySerializer(MonthDaySerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
        super(base, useTimestamp, formatter, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public MonthDaySerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new MonthDaySerializer(this, useTimestamp, formatter);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(MonthDay value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (_useTimestampExplicitOnly(provider)) {
            g.writeStartArray();
            _serializeAsArrayContents(value, g, provider);
            g.writeEndArray();
            return;
        }
        g.writeString(this._formatter == null ? value.toString() : value.format(this._formatter));
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase, com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(MonthDay value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, serializationShape(provider)));
        if (typeIdDef.valueShape == JsonToken.START_ARRAY) {
            _serializeAsArrayContents(value, g, provider);
        } else {
            g.writeString(this._formatter == null ? value.toString() : value.format(this._formatter));
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    protected void _serializeAsArrayContents(MonthDay value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeNumber(value.getMonthValue());
        g.writeNumber(value.getDayOfMonth());
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    protected JsonToken serializationShape(SerializerProvider provider) {
        return _useTimestampExplicitOnly(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
    }
}
