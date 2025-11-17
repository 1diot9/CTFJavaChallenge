package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/NumberSerializer.class */
public class NumberSerializer extends StdScalarSerializer<Number> implements ContextualSerializer {
    public static final NumberSerializer instance = new NumberSerializer(Number.class);
    protected static final int MAX_BIG_DECIMAL_SCALE = 9999;
    protected final boolean _isInt;

    public NumberSerializer(Class<? extends Number> rawType) {
        super(rawType, false);
        this._isInt = rawType == BigInteger.class;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null) {
            switch (format.getShape()) {
                case STRING:
                    if (handledType() == BigDecimal.class) {
                        return bigDecimalAsStringSerializer();
                    }
                    return ToStringSerializer.instance;
            }
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (value instanceof BigDecimal) {
            g.writeNumber((BigDecimal) value);
            return;
        }
        if (value instanceof BigInteger) {
            g.writeNumber((BigInteger) value);
            return;
        }
        if (value instanceof Long) {
            g.writeNumber(value.longValue());
            return;
        }
        if (value instanceof Double) {
            g.writeNumber(value.doubleValue());
            return;
        }
        if (value instanceof Float) {
            g.writeNumber(value.floatValue());
        } else if ((value instanceof Integer) || (value instanceof Byte) || (value instanceof Short)) {
            g.writeNumber(value.intValue());
        } else {
            g.writeNumber(value.toString());
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdScalarSerializer, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode(this._isInt ? "integer" : "number", true);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdScalarSerializer, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        if (this._isInt) {
            visitIntFormat(visitor, typeHint, JsonParser.NumberType.BIG_INTEGER);
        } else if (handledType() == BigDecimal.class) {
            visitFloatFormat(visitor, typeHint, JsonParser.NumberType.BIG_DECIMAL);
        } else {
            visitor.expectNumberFormat(typeHint);
        }
    }

    public static JsonSerializer<?> bigDecimalAsStringSerializer() {
        return BigDecimalAsStringSerializer.BD_INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/NumberSerializer$BigDecimalAsStringSerializer.class */
    public static final class BigDecimalAsStringSerializer extends ToStringSerializerBase {
        static final BigDecimalAsStringSerializer BD_INSTANCE = new BigDecimalAsStringSerializer();

        public BigDecimalAsStringSerializer() {
            super(BigDecimal.class);
        }

        @Override // com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase, com.fasterxml.jackson.databind.JsonSerializer
        public boolean isEmpty(SerializerProvider prov, Object value) {
            return false;
        }

        @Override // com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            String text;
            if (gen.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)) {
                BigDecimal bd = (BigDecimal) value;
                if (!_verifyBigDecimalRange(gen, bd)) {
                    String errorMsg = String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", Integer.valueOf(bd.scale()), Integer.valueOf(NumberSerializer.MAX_BIG_DECIMAL_SCALE), Integer.valueOf(NumberSerializer.MAX_BIG_DECIMAL_SCALE));
                    provider.reportMappingProblem(errorMsg, new Object[0]);
                }
                text = bd.toPlainString();
            } else {
                text = value.toString();
            }
            gen.writeString(text);
        }

        @Override // com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase
        public String valueToString(Object value) {
            throw new IllegalStateException();
        }

        protected boolean _verifyBigDecimalRange(JsonGenerator gen, BigDecimal value) throws IOException {
            int scale = value.scale();
            return scale >= -9999 && scale <= NumberSerializer.MAX_BIG_DECIMAL_SCALE;
        }
    }
}
