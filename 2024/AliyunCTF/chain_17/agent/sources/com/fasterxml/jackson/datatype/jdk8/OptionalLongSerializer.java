package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import java.util.OptionalLong;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/OptionalLongSerializer.class */
final class OptionalLongSerializer extends StdScalarSerializer<OptionalLong> {
    private static final long serialVersionUID = 1;
    static final OptionalLongSerializer INSTANCE = new OptionalLongSerializer();

    public OptionalLongSerializer() {
        super(OptionalLong.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider provider, OptionalLong value) {
        return value == null || !value.isPresent();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdScalarSerializer, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (v2 != null) {
            v2.numberType(JsonParser.NumberType.LONG);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(OptionalLong value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (value.isPresent()) {
            jgen.writeNumber(value.getAsLong());
        } else {
            jgen.writeNull();
        }
    }
}
