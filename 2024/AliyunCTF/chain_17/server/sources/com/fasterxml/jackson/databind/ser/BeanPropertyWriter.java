package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/BeanPropertyWriter.class */
public class BeanPropertyWriter extends PropertyWriter implements Serializable {
    private static final long serialVersionUID = 1;
    public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
    protected final SerializedString _name;
    protected final PropertyName _wrapperName;
    protected final JavaType _declaredType;
    protected final JavaType _cfgSerializationType;
    protected JavaType _nonTrivialBaseType;
    protected final transient Annotations _contextAnnotations;
    protected final AnnotatedMember _member;
    protected transient Method _accessorMethod;
    protected transient Field _field;
    protected JsonSerializer<Object> _serializer;
    protected JsonSerializer<Object> _nullSerializer;
    protected TypeSerializer _typeSerializer;
    protected transient PropertySerializerMap _dynamicSerializers;
    protected final boolean _suppressNulls;
    protected final Object _suppressableValue;
    protected final Class<?>[] _includeInViews;
    protected transient HashMap<Object, Object> _internalSettings;

    public BeanPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue, Class<?>[] includeInViews) {
        super(propDef);
        this._member = member;
        this._contextAnnotations = contextAnnotations;
        this._name = new SerializedString(propDef.getName());
        this._wrapperName = propDef.getWrapperName();
        this._declaredType = declaredType;
        this._serializer = ser;
        this._dynamicSerializers = ser == null ? PropertySerializerMap.emptyForProperties() : null;
        this._typeSerializer = typeSer;
        this._cfgSerializationType = serType;
        if (member instanceof AnnotatedField) {
            this._accessorMethod = null;
            this._field = (Field) member.getMember();
        } else if (member instanceof AnnotatedMethod) {
            this._accessorMethod = (Method) member.getMember();
            this._field = null;
        } else {
            this._accessorMethod = null;
            this._field = null;
        }
        this._suppressNulls = suppressNulls;
        this._suppressableValue = suppressableValue;
        this._nullSerializer = null;
        this._includeInViews = includeInViews;
    }

    @Deprecated
    public BeanPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue) {
        this(propDef, member, contextAnnotations, declaredType, ser, typeSer, serType, suppressNulls, suppressableValue, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanPropertyWriter() {
        super(PropertyMetadata.STD_REQUIRED_OR_OPTIONAL);
        this._member = null;
        this._contextAnnotations = null;
        this._name = null;
        this._wrapperName = null;
        this._includeInViews = null;
        this._declaredType = null;
        this._serializer = null;
        this._dynamicSerializers = null;
        this._typeSerializer = null;
        this._cfgSerializationType = null;
        this._accessorMethod = null;
        this._field = null;
        this._suppressNulls = false;
        this._suppressableValue = null;
        this._nullSerializer = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanPropertyWriter(BeanPropertyWriter base) {
        this(base, base._name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanPropertyWriter(BeanPropertyWriter base, PropertyName name) {
        super(base);
        this._name = new SerializedString(name.getSimpleName());
        this._wrapperName = base._wrapperName;
        this._contextAnnotations = base._contextAnnotations;
        this._declaredType = base._declaredType;
        this._member = base._member;
        this._accessorMethod = base._accessorMethod;
        this._field = base._field;
        this._serializer = base._serializer;
        this._nullSerializer = base._nullSerializer;
        if (base._internalSettings != null) {
            this._internalSettings = new HashMap<>(base._internalSettings);
        }
        this._cfgSerializationType = base._cfgSerializationType;
        this._dynamicSerializers = base._dynamicSerializers;
        this._suppressNulls = base._suppressNulls;
        this._suppressableValue = base._suppressableValue;
        this._includeInViews = base._includeInViews;
        this._typeSerializer = base._typeSerializer;
        this._nonTrivialBaseType = base._nonTrivialBaseType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanPropertyWriter(BeanPropertyWriter base, SerializedString name) {
        super(base);
        this._name = name;
        this._wrapperName = base._wrapperName;
        this._member = base._member;
        this._contextAnnotations = base._contextAnnotations;
        this._declaredType = base._declaredType;
        this._accessorMethod = base._accessorMethod;
        this._field = base._field;
        this._serializer = base._serializer;
        this._nullSerializer = base._nullSerializer;
        if (base._internalSettings != null) {
            this._internalSettings = new HashMap<>(base._internalSettings);
        }
        this._cfgSerializationType = base._cfgSerializationType;
        this._dynamicSerializers = base._dynamicSerializers;
        this._suppressNulls = base._suppressNulls;
        this._suppressableValue = base._suppressableValue;
        this._includeInViews = base._includeInViews;
        this._typeSerializer = base._typeSerializer;
        this._nonTrivialBaseType = base._nonTrivialBaseType;
    }

    public BeanPropertyWriter rename(NameTransformer transformer) {
        String newName = transformer.transform(this._name.getValue());
        if (newName.equals(this._name.toString())) {
            return this;
        }
        return _new(PropertyName.construct(newName));
    }

    protected BeanPropertyWriter _new(PropertyName newName) {
        return new BeanPropertyWriter(this, newName);
    }

    public void assignTypeSerializer(TypeSerializer typeSer) {
        this._typeSerializer = typeSer;
    }

    public void assignSerializer(JsonSerializer<Object> ser) {
        if (this._serializer != null && this._serializer != ser) {
            throw new IllegalStateException(String.format("Cannot override _serializer: had a %s, trying to set to %s", ClassUtil.classNameOf(this._serializer), ClassUtil.classNameOf(ser)));
        }
        this._serializer = ser;
    }

    public void assignNullSerializer(JsonSerializer<Object> nullSer) {
        if (this._nullSerializer != null && this._nullSerializer != nullSer) {
            throw new IllegalStateException(String.format("Cannot override _nullSerializer: had a %s, trying to set to %s", ClassUtil.classNameOf(this._nullSerializer), ClassUtil.classNameOf(nullSer)));
        }
        this._nullSerializer = nullSer;
    }

    public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper) {
        return new UnwrappingBeanPropertyWriter(this, unwrapper);
    }

    public void setNonTrivialBaseType(JavaType t) {
        this._nonTrivialBaseType = t;
    }

    public void fixAccess(SerializationConfig config) {
        this._member.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }

    Object readResolve() {
        if (this._member instanceof AnnotatedField) {
            this._accessorMethod = null;
            this._field = (Field) this._member.getMember();
        } else if (this._member instanceof AnnotatedMethod) {
            this._accessorMethod = (Method) this._member.getMember();
            this._field = null;
        }
        if (this._serializer == null) {
            this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty, com.fasterxml.jackson.databind.util.Named
    public String getName() {
        return this._name.getValue();
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty
    public PropertyName getFullName() {
        return new PropertyName(this._name.getValue());
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public JavaType getType() {
        return this._declaredType;
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public PropertyName getWrapperName() {
        return this._wrapperName;
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty
    public <A extends Annotation> A getAnnotation(Class<A> cls) {
        if (this._member == null) {
            return null;
        }
        return (A) this._member.getAnnotation(cls);
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty
    public <A extends Annotation> A getContextAnnotation(Class<A> cls) {
        if (this._contextAnnotations == null) {
            return null;
        }
        return (A) this._contextAnnotations.get(cls);
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public AnnotatedMember getMember() {
        return this._member;
    }

    protected void _depositSchemaProperty(ObjectNode propertiesNode, JsonNode schemaNode) {
        propertiesNode.set(getName(), schemaNode);
    }

    public Object getInternalSetting(Object key) {
        if (this._internalSettings == null) {
            return null;
        }
        return this._internalSettings.get(key);
    }

    public Object setInternalSetting(Object key, Object value) {
        if (this._internalSettings == null) {
            this._internalSettings = new HashMap<>();
        }
        return this._internalSettings.put(key, value);
    }

    public Object removeInternalSetting(Object key) {
        Object removed = null;
        if (this._internalSettings != null) {
            removed = this._internalSettings.remove(key);
            if (this._internalSettings.size() == 0) {
                this._internalSettings = null;
            }
        }
        return removed;
    }

    public SerializableString getSerializedName() {
        return this._name;
    }

    public boolean hasSerializer() {
        return this._serializer != null;
    }

    public boolean hasNullSerializer() {
        return this._nullSerializer != null;
    }

    public TypeSerializer getTypeSerializer() {
        return this._typeSerializer;
    }

    public boolean isUnwrapping() {
        return false;
    }

    public boolean willSuppressNulls() {
        return this._suppressNulls;
    }

    public boolean wouldConflictWithName(PropertyName name) {
        if (this._wrapperName != null) {
            return this._wrapperName.equals(name);
        }
        return name.hasSimpleName(this._name.getValue()) && !name.hasNamespace();
    }

    public JsonSerializer<Object> getSerializer() {
        return this._serializer;
    }

    public JavaType getSerializationType() {
        return this._cfgSerializationType;
    }

    @Deprecated
    public Class<?> getRawSerializationType() {
        if (this._cfgSerializationType == null) {
            return null;
        }
        return this._cfgSerializationType.getRawClass();
    }

    @Deprecated
    public Class<?> getPropertyType() {
        if (this._accessorMethod != null) {
            return this._accessorMethod.getReturnType();
        }
        if (this._field != null) {
            return this._field.getType();
        }
        return null;
    }

    @Deprecated
    public Type getGenericPropertyType() {
        if (this._accessorMethod != null) {
            return this._accessorMethod.getGenericReturnType();
        }
        if (this._field != null) {
            return this._field.getGenericType();
        }
        return null;
    }

    public Class<?>[] getViews() {
        return this._includeInViews;
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        Object value = this._accessorMethod == null ? this._field.get(bean) : this._accessorMethod.invoke(bean, (Object[]) null);
        if (value == null) {
            if ((this._suppressableValue == null || !prov.includeFilterSuppressNulls(this._suppressableValue)) && this._nullSerializer != null) {
                gen.writeFieldName(this._name);
                this._nullSerializer.serialize(null, gen, prov);
                return;
            }
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap m = this._dynamicSerializers;
            ser = m.serializerFor(cls);
            if (ser == null) {
                ser = _findAndAddDynamic(m, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    return;
                }
            } else if (this._suppressableValue.equals(value)) {
                return;
            }
        }
        if (value == bean && _handleSelfReference(bean, gen, prov, ser)) {
            return;
        }
        gen.writeFieldName(this._name);
        if (this._typeSerializer == null) {
            ser.serialize(value, gen, prov);
        } else {
            ser.serializeWithType(value, gen, prov, this._typeSerializer);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter
    public void serializeAsOmittedField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        if (!gen.canOmitFields()) {
            gen.writeOmittedField(this._name.getValue());
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter
    public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        Object value = this._accessorMethod == null ? this._field.get(bean) : this._accessorMethod.invoke(bean, (Object[]) null);
        if (value == null) {
            if (this._nullSerializer != null) {
                this._nullSerializer.serialize(null, gen, prov);
                return;
            } else {
                gen.writeNull();
                return;
            }
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = _findAndAddDynamic(map, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    serializeAsPlaceholder(bean, gen, prov);
                    return;
                }
            } else if (this._suppressableValue.equals(value)) {
                serializeAsPlaceholder(bean, gen, prov);
                return;
            }
        }
        if (value == bean && _handleSelfReference(bean, gen, prov, ser)) {
            return;
        }
        if (this._typeSerializer == null) {
            ser.serialize(value, gen, prov);
        } else {
            ser.serializeWithType(value, gen, prov, this._typeSerializer);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter
    public void serializeAsPlaceholder(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        if (this._nullSerializer != null) {
            this._nullSerializer.serialize(null, gen, prov);
        } else {
            gen.writeNull();
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter, com.fasterxml.jackson.databind.BeanProperty
    public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
        if (v != null) {
            if (isRequired()) {
                v.property(this);
            } else {
                v.optionalProperty(this);
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.PropertyWriter
    @Deprecated
    public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
        JsonNode schemaNode;
        JavaType propType = getSerializationType();
        Type hint = propType == null ? getType() : propType.getRawClass();
        JsonSerializer<Object> ser = getSerializer();
        if (ser == null) {
            ser = provider.findValueSerializer(getType(), this);
        }
        boolean isOptional = !isRequired();
        if (ser instanceof SchemaAware) {
            schemaNode = ((SchemaAware) ser).getSchema(provider, hint, isOptional);
        } else {
            schemaNode = JsonSchema.getDefaultSchemaNode();
        }
        _depositSchemaProperty(propertiesNode, schemaNode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result;
        if (this._nonTrivialBaseType != null) {
            JavaType t = provider.constructSpecializedType(this._nonTrivialBaseType, type);
            result = map.findAndAddPrimarySerializer(t, provider, this);
        } else {
            result = map.findAndAddPrimarySerializer(type, provider, this);
        }
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }

    public final Object get(Object bean) throws Exception {
        return this._accessorMethod == null ? this._field.get(bean) : this._accessorMethod.invoke(bean, (Object[]) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean _handleSelfReference(Object bean, JsonGenerator gen, SerializerProvider prov, JsonSerializer<?> ser) throws IOException {
        if (!ser.usesObjectId()) {
            if (prov.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES)) {
                if (ser instanceof BeanSerializerBase) {
                    prov.reportBadDefinition(getType(), "Direct self-reference leading to cycle");
                    return false;
                }
                return false;
            }
            if (prov.isEnabled(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)) {
                if (this._nullSerializer != null) {
                    if (!gen.getOutputContext().inArray()) {
                        gen.writeFieldName(this._name);
                    }
                    this._nullSerializer.serialize(null, gen, prov);
                    return true;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(40);
        sb.append("property '").append(getName()).append("' (");
        if (this._accessorMethod != null) {
            sb.append("via method ").append(this._accessorMethod.getDeclaringClass().getName()).append("#").append(this._accessorMethod.getName());
        } else if (this._field != null) {
            sb.append("field \"").append(this._field.getDeclaringClass().getName()).append("#").append(this._field.getName());
        } else {
            sb.append("virtual");
        }
        if (this._serializer == null) {
            sb.append(", no static serializer");
        } else {
            sb.append(", static serializer of type " + this._serializer.getClass().getName());
        }
        sb.append(')');
        return sb.toString();
    }
}
