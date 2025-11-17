package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/JsonValueSerializer.class */
public class JsonValueSerializer extends StdSerializer<Object> implements ContextualSerializer, JsonFormatVisitable {
    protected final AnnotatedMember _accessor;
    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final BeanProperty _property;
    protected final JavaType _valueType;
    protected final boolean _forceTypeInformation;
    protected transient PropertySerializerMap _dynamicSerializers;

    public JsonValueSerializer(AnnotatedMember accessor, TypeSerializer vts, JsonSerializer<?> ser) {
        super(accessor.getType());
        this._accessor = accessor;
        this._valueType = accessor.getType();
        this._valueTypeSerializer = vts;
        this._valueSerializer = ser;
        this._property = null;
        this._forceTypeInformation = true;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }

    @Deprecated
    public JsonValueSerializer(AnnotatedMember accessor, JsonSerializer<?> ser) {
        this(accessor, null, ser);
    }

    public JsonValueSerializer(JsonValueSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
        super(_notNullClass(src.handledType()));
        this._accessor = src._accessor;
        this._valueType = src._valueType;
        this._valueTypeSerializer = vts;
        this._valueSerializer = ser;
        this._property = property;
        this._forceTypeInformation = forceTypeInfo;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }

    private static final Class<Object> _notNullClass(Class<?> cls) {
        return cls == null ? Object.class : cls;
    }

    protected JsonValueSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
        if (this._property == property && this._valueTypeSerializer == vts && this._valueSerializer == ser && forceTypeInfo == this._forceTypeInformation) {
            return this;
        }
        return new JsonValueSerializer(this, property, vts, ser, forceTypeInfo);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider ctxt, Object bean) {
        Object referenced = this._accessor.getValue(bean);
        if (referenced == null) {
            return true;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            try {
                ser = _findDynamicSerializer(ctxt, referenced.getClass());
            } catch (JsonMappingException e) {
                throw new RuntimeJsonMappingException(e);
            }
        }
        return ser.isEmpty(ctxt, referenced);
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider ctxt, BeanProperty property) throws JsonMappingException {
        TypeSerializer typeSer = this._valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }
        JsonSerializer<?> ser = this._valueSerializer;
        if (ser == null) {
            if (ctxt.isEnabled(MapperFeature.USE_STATIC_TYPING) || this._valueType.isFinal()) {
                JsonSerializer<?> ser2 = ctxt.findPrimaryPropertySerializer(this._valueType, property);
                boolean forceTypeInformation = isNaturalTypeWithStdHandling(this._valueType.getRawClass(), ser2);
                return withResolved(property, typeSer, ser2, forceTypeInformation);
            }
            if (property != this._property) {
                return withResolved(property, typeSer, ser, this._forceTypeInformation);
            }
            return this;
        }
        return withResolved(property, typeSer, ctxt.handlePrimaryContextualization(ser, property), this._forceTypeInformation);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Object bean, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
        Object value;
        try {
            value = this._accessor.getValue(bean);
        } catch (Exception e) {
            value = null;
            wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
        }
        if (value == null) {
            ctxt.defaultSerializeNull(gen);
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = _findDynamicSerializer(ctxt, value.getClass());
        }
        if (this._valueTypeSerializer != null) {
            ser.serializeWithType(value, gen, ctxt, this._valueTypeSerializer);
        } else {
            ser.serialize(value, gen, ctxt);
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer0) throws IOException {
        Object value;
        try {
            value = this._accessor.getValue(bean);
        } catch (Exception e) {
            value = null;
            wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
        }
        if (value == null) {
            ctxt.defaultSerializeNull(gen);
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = _findDynamicSerializer(ctxt, value.getClass());
        } else if (this._forceTypeInformation) {
            WritableTypeId typeIdDef = typeSer0.writeTypePrefix(gen, typeSer0.typeId(bean, JsonToken.VALUE_STRING));
            ser.serialize(value, gen, ctxt);
            typeSer0.writeTypeSuffix(gen, typeIdDef);
            return;
        }
        TypeSerializerRerouter rr = new TypeSerializerRerouter(typeSer0, bean);
        ser.serializeWithType(value, gen, ctxt, rr);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    @Deprecated
    public JsonNode getSchema(SerializerProvider ctxt, Type typeHint) throws JsonMappingException {
        if (this._valueSerializer instanceof SchemaAware) {
            return ((SchemaAware) this._valueSerializer).getSchema(ctxt, null);
        }
        return JsonSchema.getDefaultSchemaNode();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        Class<?> declaring = this._accessor.getDeclaringClass();
        if (declaring != null && ClassUtil.isEnumType(declaring) && _acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring)) {
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = visitor.getProvider().findTypedValueSerializer(this._valueType, false, this._property);
            if (ser == null) {
                visitor.expectAnyFormat(typeHint);
                return;
            }
        }
        ser.acceptJsonFormatVisitor(visitor, this._valueType);
    }

    protected boolean _acceptJsonFormatVisitorForEnum(JsonFormatVisitorWrapper visitor, JavaType typeHint, Class<?> enumType) throws JsonMappingException {
        Throwable t;
        JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
        if (stringVisitor != null) {
            Set<String> enums = new LinkedHashSet<>();
            for (Object en : enumType.getEnumConstants()) {
                try {
                    enums.add(String.valueOf(this._accessor.getValue(en)));
                } catch (Exception e) {
                    Throwable th = e;
                    while (true) {
                        t = th;
                        if (!(t instanceof InvocationTargetException) || t.getCause() == null) {
                            break;
                        }
                        th = t.getCause();
                    }
                    ClassUtil.throwIfError(t);
                    throw JsonMappingException.wrapWithPath(t, en, this._accessor.getName() + "()");
                }
            }
            stringVisitor.enumTypes(enums);
            return true;
        }
        return true;
    }

    protected boolean isNaturalTypeWithStdHandling(Class<?> rawType, JsonSerializer<?> ser) {
        if (rawType.isPrimitive()) {
            if (rawType != Integer.TYPE && rawType != Boolean.TYPE && rawType != Double.TYPE) {
                return false;
            }
        } else if (rawType != String.class && rawType != Integer.class && rawType != Boolean.class && rawType != Double.class) {
            return false;
        }
        return isDefaultSerializer(ser);
    }

    protected JsonSerializer<Object> _findDynamicSerializer(SerializerProvider ctxt, Class<?> valueClass) throws JsonMappingException {
        JsonSerializer<Object> serializer = this._dynamicSerializers.serializerFor(valueClass);
        if (serializer == null) {
            if (this._valueType.hasGenericTypes()) {
                JavaType fullType = ctxt.constructSpecializedType(this._valueType, valueClass);
                serializer = ctxt.findPrimaryPropertySerializer(fullType, this._property);
                PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(fullType, serializer);
                this._dynamicSerializers = result.map;
            } else {
                serializer = ctxt.findPrimaryPropertySerializer(valueClass, this._property);
                PropertySerializerMap.SerializerAndMapResult result2 = this._dynamicSerializers.addSerializer(valueClass, serializer);
                this._dynamicSerializers = result2.map;
            }
        }
        return serializer;
    }

    public String toString() {
        return "(@JsonValue serializer for method " + this._accessor.getDeclaringClass() + "#" + this._accessor.getName() + ")";
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/JsonValueSerializer$TypeSerializerRerouter.class */
    static class TypeSerializerRerouter extends TypeSerializer {
        protected final TypeSerializer _typeSerializer;
        protected final Object _forObject;

        public TypeSerializerRerouter(TypeSerializer ts, Object ob) {
            this._typeSerializer = ts;
            this._forObject = ob;
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        public TypeSerializer forProperty(BeanProperty prop) {
            throw new UnsupportedOperationException();
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        public JsonTypeInfo.As getTypeInclusion() {
            return this._typeSerializer.getTypeInclusion();
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        public String getPropertyName() {
            return this._typeSerializer.getPropertyName();
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        public TypeIdResolver getTypeIdResolver() {
            return this._typeSerializer.getTypeIdResolver();
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId typeId) throws IOException {
            typeId.forValue = this._forObject;
            return this._typeSerializer.writeTypePrefix(g, typeId);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId typeId) throws IOException {
            return this._typeSerializer.writeTypeSuffix(g, typeId);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypePrefixForScalar(Object value, JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypePrefixForObject(Object value, JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForObject(this._forObject, gen);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypePrefixForArray(Object value, JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypePrefixForArray(this._forObject, gen);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypeSuffixForScalar(Object value, JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForScalar(this._forObject, gen);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypeSuffixForObject(Object value, JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForObject(this._forObject, gen);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypeSuffixForArray(Object value, JsonGenerator gen) throws IOException {
            this._typeSerializer.writeTypeSuffixForArray(this._forObject, gen);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypePrefixForScalar(Object value, JsonGenerator gen, Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen, type);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypePrefixForObject(Object value, JsonGenerator gen, Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForObject(this._forObject, gen, type);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeTypePrefixForArray(Object value, JsonGenerator gen, Class<?> type) throws IOException {
            this._typeSerializer.writeTypePrefixForArray(this._forObject, gen, type);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeCustomTypePrefixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForScalar(this._forObject, gen, typeId);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeCustomTypePrefixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForObject(this._forObject, gen, typeId);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeCustomTypePrefixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
            this._typeSerializer.writeCustomTypePrefixForArray(this._forObject, gen, typeId);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForScalar(this._forObject, gen, typeId);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeCustomTypeSuffixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForObject(this._forObject, gen, typeId);
        }

        @Override // com.fasterxml.jackson.databind.jsontype.TypeSerializer
        @Deprecated
        public void writeCustomTypeSuffixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
            this._typeSerializer.writeCustomTypeSuffixForArray(this._forObject, gen, typeId);
        }
    }
}
