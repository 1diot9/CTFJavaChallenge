package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.EnumNamingStrategyFactory;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.EnumValues;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/EnumSerializer.class */
public class EnumSerializer extends StdScalarSerializer<Enum<?>> implements ContextualSerializer {
    private static final long serialVersionUID = 1;
    protected final EnumValues _values;
    protected final Boolean _serializeAsIndex;
    protected final EnumValues _valuesByEnumNaming;

    public EnumSerializer(EnumValues v, Boolean serializeAsIndex) {
        super(v.getEnumClass(), false);
        this._values = v;
        this._serializeAsIndex = serializeAsIndex;
        this._valuesByEnumNaming = null;
    }

    public EnumSerializer(EnumValues v, Boolean serializeAsIndex, EnumValues valuesByEnumNaming) {
        super(v.getEnumClass(), false);
        this._values = v;
        this._serializeAsIndex = serializeAsIndex;
        this._valuesByEnumNaming = valuesByEnumNaming;
    }

    public static EnumSerializer construct(Class<?> enumClass, SerializationConfig config, BeanDescription beanDesc, JsonFormat.Value format) {
        EnumValues v = EnumValues.constructFromName(config, enumClass);
        EnumValues valuesByEnumNaming = constructEnumNamingStrategyValues(config, enumClass, beanDesc.getClassInfo());
        Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true, null);
        return new EnumSerializer(v, serializeAsIndex, valuesByEnumNaming);
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
        if (format != null) {
            Class<?> type = handledType();
            Boolean serializeAsIndex = _isShapeWrittenUsingIndex(type, format, false, this._serializeAsIndex);
            if (!Objects.equals(serializeAsIndex, this._serializeAsIndex)) {
                return new EnumSerializer(this._values, serializeAsIndex);
            }
        }
        return this;
    }

    public EnumValues getEnumValues() {
        return this._values;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public final void serialize(Enum<?> en, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (this._valuesByEnumNaming != null) {
            gen.writeString(this._valuesByEnumNaming.serializedValueFor(en));
            return;
        }
        if (_serializeAsIndex(serializers)) {
            gen.writeNumber(en.ordinal());
        } else if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
            gen.writeString(en.toString());
        } else {
            gen.writeString(this._values.serializedValueFor(en));
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdScalarSerializer, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        if (_serializeAsIndex(provider)) {
            return createSchemaNode("integer", true);
        }
        ObjectNode objectNode = createSchemaNode("string", true);
        if (typeHint != null) {
            JavaType type = provider.constructType(typeHint);
            if (type.isEnumType()) {
                ArrayNode enumNode = objectNode.putArray("enum");
                for (SerializableString value : this._values.values()) {
                    enumNode.add(value.getValue());
                }
            }
        }
        return objectNode;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdScalarSerializer, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        SerializerProvider serializers = visitor.getProvider();
        if (_serializeAsIndex(serializers)) {
            visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
            return;
        }
        JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
        if (stringVisitor != null) {
            Set<String> enums = new LinkedHashSet<>();
            if (serializers != null && serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
                for (Enum<?> e : this._values.enums()) {
                    enums.add(e.toString());
                }
            } else {
                for (SerializableString value : this._values.values()) {
                    enums.add(value.getValue());
                }
            }
            stringVisitor.enumTypes(enums);
        }
    }

    protected final boolean _serializeAsIndex(SerializerProvider serializers) {
        if (this._serializeAsIndex != null) {
            return this._serializeAsIndex.booleanValue();
        }
        return serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    }

    protected static Boolean _isShapeWrittenUsingIndex(Class<?> enumClass, JsonFormat.Value format, boolean fromClass, Boolean defaultValue) {
        JsonFormat.Shape shape = format == null ? null : format.getShape();
        if (shape == null) {
            return defaultValue;
        }
        if (shape == JsonFormat.Shape.ANY || shape == JsonFormat.Shape.SCALAR) {
            return defaultValue;
        }
        if (shape == JsonFormat.Shape.STRING || shape == JsonFormat.Shape.NATURAL) {
            return Boolean.FALSE;
        }
        if (shape.isNumeric() || shape == JsonFormat.Shape.ARRAY) {
            return Boolean.TRUE;
        }
        Object[] objArr = new Object[3];
        objArr[0] = shape;
        objArr[1] = enumClass.getName();
        objArr[2] = fromClass ? "class" : BeanDefinitionParserDelegate.PROPERTY_ELEMENT;
        throw new IllegalArgumentException(String.format("Unsupported serialization shape (%s) for Enum %s, not supported as %s annotation", objArr));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static EnumValues constructEnumNamingStrategyValues(SerializationConfig config, Class<Enum<?>> enumClass, AnnotatedClass annotatedClass) {
        Object namingDef = config.getAnnotationIntrospector().findEnumNamingStrategy(config, annotatedClass);
        EnumNamingStrategy enumNamingStrategy = EnumNamingStrategyFactory.createEnumNamingStrategyInstance(namingDef, config.canOverrideAccessModifiers());
        if (enumNamingStrategy == null) {
            return null;
        }
        return EnumValues.constructUsingEnumNamingStrategy(config, enumClass, enumNamingStrategy);
    }
}
