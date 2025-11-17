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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/LocalDateSerializer.class */
public class LocalDateSerializer extends JSR310FormattedSerializerBase<LocalDate> {
    private static final long serialVersionUID = 1;
    public static final LocalDateSerializer INSTANCE = new LocalDateSerializer();

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    public /* bridge */ /* synthetic */ JsonNode getSchema(SerializerProvider serializerProvider, Type type) {
        return super.getSchema(serializerProvider, type);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.ContextualSerializer
    public /* bridge */ /* synthetic */ JsonSerializer createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return super.createContextual(serializerProvider, beanProperty);
    }

    protected LocalDateSerializer() {
        super(LocalDate.class);
    }

    protected LocalDateSerializer(LocalDateSerializer base, Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        super(base, useTimestamp, dtf, shape);
    }

    public LocalDateSerializer(DateTimeFormatter formatter) {
        super(LocalDate.class, formatter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    public LocalDateSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new LocalDateSerializer(this, useTimestamp, dtf, shape);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(LocalDate date, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            if (this._shape == JsonFormat.Shape.NUMBER_INT) {
                g.writeNumber(date.toEpochDay());
                return;
            }
            g.writeStartArray();
            _serializeAsArrayContents(date, g, provider);
            g.writeEndArray();
            return;
        }
        g.writeString(this._formatter == null ? date.toString() : date.format(this._formatter));
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase, com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(LocalDate value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, serializationShape(provider)));
        switch (typeIdDef.valueShape) {
            case START_ARRAY:
                _serializeAsArrayContents(value, g, provider);
                break;
            case VALUE_NUMBER_INT:
                g.writeNumber(value.toEpochDay());
                break;
            default:
                g.writeString(this._formatter == null ? value.toString() : value.format(this._formatter));
                break;
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    protected void _serializeAsArrayContents(LocalDate value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeNumber(value.getYear());
        g.writeNumber(value.getMonthValue());
        g.writeNumber(value.getDayOfMonth());
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        SerializerProvider provider = visitor.getProvider();
        boolean useTimestamp = provider != null && useTimestamp(provider);
        if (useTimestamp) {
            _acceptTimestampVisitor(visitor, typeHint);
            return;
        }
        JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
        if (v2 != null) {
            v2.format(JsonValueFormat.DATE);
        }
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    protected JsonToken serializationShape(SerializerProvider provider) {
        if (useTimestamp(provider)) {
            if (this._shape == JsonFormat.Shape.NUMBER_INT) {
                return JsonToken.VALUE_NUMBER_INT;
            }
            return JsonToken.START_ARRAY;
        }
        return JsonToken.VALUE_STRING;
    }
}
