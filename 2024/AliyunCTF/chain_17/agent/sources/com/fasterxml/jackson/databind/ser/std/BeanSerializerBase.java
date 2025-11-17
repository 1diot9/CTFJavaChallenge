package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.AnyGetterWriter;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/BeanSerializerBase.class */
public abstract class BeanSerializerBase extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable {
    protected static final PropertyName NAME_FOR_OBJECT_REF = new PropertyName("#object-ref");
    protected static final BeanPropertyWriter[] NO_PROPS = new BeanPropertyWriter[0];
    protected final JavaType _beanType;
    protected final BeanPropertyWriter[] _props;
    protected final BeanPropertyWriter[] _filteredProps;
    protected final AnyGetterWriter _anyGetterWriter;
    protected final Object _propertyFilterId;
    protected final AnnotatedMember _typeId;
    protected final ObjectIdWriter _objectIdWriter;
    protected final JsonFormat.Shape _serializationShape;

    public abstract BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter);

    protected abstract BeanSerializerBase withByNameInclusion(Set<String> set, Set<String> set2);

    protected abstract BeanSerializerBase asArraySerializer();

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public abstract BeanSerializerBase withFilterId(Object obj);

    protected abstract BeanSerializerBase withProperties(BeanPropertyWriter[] beanPropertyWriterArr, BeanPropertyWriter[] beanPropertyWriterArr2);

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public abstract void serialize(Object obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanSerializerBase(JavaType type, BeanSerializerBuilder builder, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
        super(type);
        this._beanType = type;
        this._props = properties;
        this._filteredProps = filteredProperties;
        if (builder == null) {
            this._typeId = null;
            this._anyGetterWriter = null;
            this._propertyFilterId = null;
            this._objectIdWriter = null;
            this._serializationShape = null;
            return;
        }
        this._typeId = builder.getTypeId();
        this._anyGetterWriter = builder.getAnyGetter();
        this._propertyFilterId = builder.getFilterId();
        this._objectIdWriter = builder.getObjectIdWriter();
        JsonFormat.Value format = builder.getBeanDescription().findExpectedFormat(null);
        this._serializationShape = format.getShape();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanSerializerBase(BeanSerializerBase src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
        super(src._handledType);
        this._beanType = src._beanType;
        this._props = properties;
        this._filteredProps = filteredProperties;
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = src._objectIdWriter;
        this._propertyFilterId = src._propertyFilterId;
        this._serializationShape = src._serializationShape;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter) {
        this(src, objectIdWriter, src._propertyFilterId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId) {
        super(src._handledType);
        this._beanType = src._beanType;
        this._props = src._props;
        this._filteredProps = src._filteredProps;
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = objectIdWriter;
        this._propertyFilterId = filterId;
        this._serializationShape = src._serializationShape;
    }

    @Deprecated
    protected BeanSerializerBase(BeanSerializerBase src, String[] toIgnore) {
        this(src, ArrayBuilders.arrayToSet(toIgnore), (Set<String>) null);
    }

    @Deprecated
    protected BeanSerializerBase(BeanSerializerBase src, Set<String> toIgnore) {
        this(src, toIgnore, (Set<String>) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanSerializerBase(BeanSerializerBase src, Set<String> toIgnore, Set<String> toInclude) {
        super(src._handledType);
        this._beanType = src._beanType;
        BeanPropertyWriter[] propsIn = src._props;
        BeanPropertyWriter[] fpropsIn = src._filteredProps;
        int len = propsIn.length;
        ArrayList<BeanPropertyWriter> propsOut = new ArrayList<>(len);
        ArrayList<BeanPropertyWriter> fpropsOut = fpropsIn == null ? null : new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            BeanPropertyWriter bpw = propsIn[i];
            if (!IgnorePropertiesUtil.shouldIgnore(bpw.getName(), toIgnore, toInclude)) {
                propsOut.add(bpw);
                if (fpropsIn != null) {
                    fpropsOut.add(fpropsIn[i]);
                }
            }
        }
        this._props = (BeanPropertyWriter[]) propsOut.toArray(new BeanPropertyWriter[propsOut.size()]);
        this._filteredProps = fpropsOut == null ? null : (BeanPropertyWriter[]) fpropsOut.toArray(new BeanPropertyWriter[fpropsOut.size()]);
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = src._objectIdWriter;
        this._propertyFilterId = src._propertyFilterId;
        this._serializationShape = src._serializationShape;
    }

    @Deprecated
    protected BeanSerializerBase withIgnorals(Set<String> toIgnore) {
        return withByNameInclusion(toIgnore, null);
    }

    @Deprecated
    protected BeanSerializerBase withIgnorals(String[] toIgnore) {
        return withIgnorals(ArrayBuilders.arrayToSet(toIgnore));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanSerializerBase(BeanSerializerBase src) {
        this(src, src._props, src._filteredProps);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanSerializerBase(BeanSerializerBase src, NameTransformer unwrapper) {
        this(src, rename(src._props, unwrapper), rename(src._filteredProps, unwrapper));
    }

    private static final BeanPropertyWriter[] rename(BeanPropertyWriter[] props, NameTransformer transformer) {
        if (props == null || props.length == 0 || transformer == null || transformer == NameTransformer.NOP) {
            return props;
        }
        int len = props.length;
        BeanPropertyWriter[] result = new BeanPropertyWriter[len];
        for (int i = 0; i < len; i++) {
            BeanPropertyWriter bpw = props[i];
            if (bpw != null) {
                result[i] = bpw.rename(transformer);
            }
        }
        return result;
    }

    @Override // com.fasterxml.jackson.databind.ser.ResolvableSerializer
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        BeanPropertyWriter w2;
        TypeSerializer typeSer;
        JsonSerializer<Object> nullSer;
        BeanPropertyWriter w22;
        int filteredCount = this._filteredProps == null ? 0 : this._filteredProps.length;
        int len = this._props.length;
        for (int i = 0; i < len; i++) {
            BeanPropertyWriter prop = this._props[i];
            if (!prop.willSuppressNulls() && !prop.hasNullSerializer() && (nullSer = provider.findNullValueSerializer(prop)) != null) {
                prop.assignNullSerializer(nullSer);
                if (i < filteredCount && (w22 = this._filteredProps[i]) != null) {
                    w22.assignNullSerializer(nullSer);
                }
            }
            if (!prop.hasSerializer()) {
                JsonSerializer<Object> ser = findConvertingSerializer(provider, prop);
                if (ser == null) {
                    JavaType type = prop.getSerializationType();
                    if (type == null) {
                        type = prop.getType();
                        if (!type.isFinal()) {
                            if (type.isContainerType() || type.containedTypeCount() > 0) {
                                prop.setNonTrivialBaseType(type);
                            }
                        }
                    }
                    ser = provider.findValueSerializer(type, prop);
                    if (type.isContainerType() && (typeSer = (TypeSerializer) type.getContentType().getTypeHandler()) != null && (ser instanceof ContainerSerializer)) {
                        JsonSerializer<Object> ser2 = ((ContainerSerializer) ser).withValueTypeSerializer(typeSer);
                        ser = ser2;
                    }
                }
                if (i < filteredCount && (w2 = this._filteredProps[i]) != null) {
                    w2.assignSerializer(ser);
                } else {
                    prop.assignSerializer(ser);
                }
            }
        }
        if (this._anyGetterWriter != null) {
            this._anyGetterWriter.resolve(provider);
        }
    }

    protected JsonSerializer<Object> findConvertingSerializer(SerializerProvider provider, BeanPropertyWriter prop) throws JsonMappingException {
        AnnotatedMember m;
        Object convDef;
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (intr != null && (m = prop.getMember()) != null && (convDef = intr.findSerializationConverter(m)) != null) {
            Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
            JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
            JsonSerializer<?> ser = delegateType.isJavaLangObject() ? null : provider.findValueSerializer(delegateType, prop);
            return new StdDelegatingSerializer(conv, delegateType, ser);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        BeanPropertyWriter[] newFiltered;
        ObjectIdInfo objectIdInfo;
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        AnnotatedMember accessor = (property == null || intr == null) ? null : property.getMember();
        SerializationConfig config = provider.getConfig();
        JsonFormat.Value format = findFormatOverrides(provider, property, this._handledType);
        JsonFormat.Shape shape = null;
        if (format != null && format.hasShape()) {
            shape = format.getShape();
            if (shape != JsonFormat.Shape.ANY && shape != this._serializationShape) {
                if (this._beanType.isEnumType()) {
                    switch (shape) {
                        case STRING:
                        case NUMBER:
                        case NUMBER_INT:
                            BeanDescription desc = config.introspectClassAnnotations(this._beanType);
                            JsonSerializer<?> ser = EnumSerializer.construct(this._beanType.getRawClass(), provider.getConfig(), desc, format);
                            return provider.handlePrimaryContextualization(ser, property);
                    }
                }
                if (shape == JsonFormat.Shape.NATURAL && ((!this._beanType.isMapLikeType() || !Map.class.isAssignableFrom(this._handledType)) && Map.Entry.class.isAssignableFrom(this._handledType))) {
                    JavaType mapEntryType = this._beanType.findSuperType(Map.Entry.class);
                    JavaType kt = mapEntryType.containedTypeOrUnknown(0);
                    JavaType vt = mapEntryType.containedTypeOrUnknown(1);
                    JsonSerializer<?> ser2 = new MapEntrySerializer(this._beanType, kt, vt, false, null, property);
                    return provider.handlePrimaryContextualization(ser2, property);
                }
            }
        }
        ObjectIdWriter oiw = this._objectIdWriter;
        int idPropOrigIndex = 0;
        Set<String> ignoredProps = null;
        Set<String> includedProps = null;
        Object newFilterId = null;
        if (accessor != null) {
            ignoredProps = intr.findPropertyIgnoralByName(config, accessor).findIgnoredForSerialization();
            includedProps = intr.findPropertyInclusionByName(config, accessor).getIncluded();
            ObjectIdInfo objectIdInfo2 = intr.findObjectIdInfo(accessor);
            if (objectIdInfo2 == null) {
                if (oiw != null && (objectIdInfo = intr.findObjectReferenceInfo(accessor, null)) != null) {
                    oiw = this._objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
                }
            } else {
                ObjectIdInfo objectIdInfo3 = intr.findObjectReferenceInfo(accessor, objectIdInfo2);
                Class<?> implClass = objectIdInfo3.getGeneratorType();
                JavaType type = provider.constructType(implClass);
                JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                    String propName = objectIdInfo3.getPropertyName().getSimpleName();
                    int i = 0;
                    int len = this._props.length;
                    while (true) {
                        if (i == len) {
                            provider.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name %s", ClassUtil.nameOf((Class<?>) handledType()), ClassUtil.name(propName)));
                        }
                        BeanPropertyWriter prop = this._props[i];
                        if (!propName.equals(prop.getName())) {
                            i++;
                        } else {
                            idPropOrigIndex = i;
                            JavaType idType2 = prop.getType();
                            ObjectIdGenerator<?> gen = new PropertyBasedObjectIdGenerator(objectIdInfo3, prop);
                            oiw = ObjectIdWriter.construct(idType2, (PropertyName) null, gen, objectIdInfo3.getAlwaysAsId());
                        }
                    }
                } else {
                    ObjectIdGenerator<?> gen2 = provider.objectIdGeneratorInstance(accessor, objectIdInfo3);
                    oiw = ObjectIdWriter.construct(idType, objectIdInfo3.getPropertyName(), gen2, objectIdInfo3.getAlwaysAsId());
                }
            }
            Object filterId = intr.findFilterId(accessor);
            if (filterId != null && (this._propertyFilterId == null || !filterId.equals(this._propertyFilterId))) {
                newFilterId = filterId;
            }
        }
        BeanSerializerBase contextual = this;
        if (idPropOrigIndex > 0) {
            BeanPropertyWriter[] newProps = (BeanPropertyWriter[]) Arrays.copyOf(this._props, this._props.length);
            BeanPropertyWriter bpw = newProps[idPropOrigIndex];
            System.arraycopy(newProps, 0, newProps, 1, idPropOrigIndex);
            newProps[0] = bpw;
            if (this._filteredProps == null) {
                newFiltered = null;
            } else {
                newFiltered = (BeanPropertyWriter[]) Arrays.copyOf(this._filteredProps, this._filteredProps.length);
                BeanPropertyWriter bpw2 = newFiltered[idPropOrigIndex];
                System.arraycopy(newFiltered, 0, newFiltered, 1, idPropOrigIndex);
                newFiltered[0] = bpw2;
            }
            contextual = contextual.withProperties(newProps, newFiltered);
        }
        if (oiw != null) {
            JsonSerializer<?> ser3 = provider.findValueSerializer(oiw.idType, property);
            ObjectIdWriter oiw2 = oiw.withSerializer(ser3);
            if (oiw2 != this._objectIdWriter) {
                contextual = contextual.withObjectIdWriter(oiw2);
            }
        }
        if ((ignoredProps != null && !ignoredProps.isEmpty()) || includedProps != null) {
            contextual = contextual.withByNameInclusion(ignoredProps, includedProps);
        }
        if (newFilterId != null) {
            contextual = contextual.withFilterId(newFilterId);
        }
        if (shape == null) {
            shape = this._serializationShape;
        }
        if (shape == JsonFormat.Shape.ARRAY) {
            return contextual.asArraySerializer();
        }
        return contextual;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public Iterator<PropertyWriter> properties() {
        return Arrays.asList(this._props).iterator();
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean usesObjectId() {
        return this._objectIdWriter != null;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        if (this._objectIdWriter != null) {
            _serializeWithObjectId(bean, gen, provider, typeSer);
            return;
        }
        WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
        typeSer.writeTypePrefix(gen, typeIdDef);
        gen.setCurrentValue(bean);
        if (this._propertyFilterId != null) {
            serializeFieldsFiltered(bean, gen, provider);
        } else {
            serializeFields(bean, gen, provider);
        }
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, boolean startEndObject) throws IOException {
        ObjectIdWriter w = this._objectIdWriter;
        WritableObjectId objectId = provider.findObjectId(bean, w.generator);
        if (objectId.writeAsId(gen, provider, w)) {
            return;
        }
        Object id = objectId.generateId(bean);
        if (w.alwaysAsId) {
            w.serializer.serialize(id, gen, provider);
            return;
        }
        if (startEndObject) {
            gen.writeStartObject(bean);
        }
        objectId.writeAsField(gen, provider, w);
        if (this._propertyFilterId != null) {
            serializeFieldsFiltered(bean, gen, provider);
        } else {
            serializeFields(bean, gen, provider);
        }
        if (startEndObject) {
            gen.writeEndObject();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        ObjectIdWriter w = this._objectIdWriter;
        WritableObjectId objectId = provider.findObjectId(bean, w.generator);
        if (objectId.writeAsId(gen, provider, w)) {
            return;
        }
        Object id = objectId.generateId(bean);
        if (w.alwaysAsId) {
            w.serializer.serialize(id, gen, provider);
        } else {
            _serializeObjectId(bean, gen, provider, typeSer, objectId);
        }
    }

    protected void _serializeObjectId(Object bean, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer, WritableObjectId objectId) throws IOException {
        ObjectIdWriter w = this._objectIdWriter;
        WritableTypeId typeIdDef = _typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
        typeSer.writeTypePrefix(g, typeIdDef);
        g.setCurrentValue(bean);
        objectId.writeAsField(g, provider, w);
        if (this._propertyFilterId != null) {
            serializeFieldsFiltered(bean, g, provider);
        } else {
            serializeFields(bean, g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final WritableTypeId _typeIdDef(TypeSerializer typeSer, Object bean, JsonToken valueShape) {
        if (this._typeId == null) {
            return typeSer.typeId(bean, valueShape);
        }
        Object typeId = this._typeId.getValue(bean);
        if (typeId == null) {
            typeId = "";
        }
        return typeSer.typeId(bean, valueShape, typeId);
    }

    @Deprecated
    protected final String _customTypeId(Object bean) {
        Object typeId = this._typeId.getValue(bean);
        if (typeId == null) {
            return "";
        }
        return typeId instanceof String ? (String) typeId : typeId.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        } else {
            props = this._props;
        }
        int i = 0;
        try {
            int len = props.length;
            while (i < len) {
                BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    prop.serializeAsField(bean, gen, provider);
                }
                i++;
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndSerialize(bean, gen, provider);
            }
        } catch (Exception e) {
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            wrapAndThrow(provider, e, bean, name);
        } catch (StackOverflowError e2) {
            DatabindException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e2);
            String name2 = i == props.length ? "[anySetter]" : props[i].getName();
            mapE.prependPath(bean, name2);
            throw mapE;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void serializeFieldsFiltered(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        } else {
            props = this._props;
        }
        PropertyFilter filter = findPropertyFilter(provider, this._propertyFilterId, bean);
        if (filter == null) {
            serializeFields(bean, gen, provider);
            return;
        }
        int i = 0;
        try {
            int len = props.length;
            while (i < len) {
                BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    filter.serializeAsField(bean, gen, provider, prop);
                }
                i++;
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndFilter(bean, gen, provider, filter);
            }
        } catch (Exception e) {
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            wrapAndThrow(provider, e, bean, name);
        } catch (StackOverflowError e2) {
            DatabindException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e2);
            String name2 = i == props.length ? "[anySetter]" : props[i].getName();
            mapE.prependPath(bean, name2);
            throw mapE;
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        PropertyFilter filter;
        String id;
        ObjectNode o = createSchemaNode("object", true);
        JsonSerializableSchema ann = (JsonSerializableSchema) this._handledType.getAnnotation(JsonSerializableSchema.class);
        if (ann != null && (id = ann.id()) != null && !id.isEmpty()) {
            o.put("id", id);
        }
        ObjectNode propertiesNode = o.objectNode();
        if (this._propertyFilterId != null) {
            filter = findPropertyFilter(provider, this._propertyFilterId, null);
        } else {
            filter = null;
        }
        for (int i = 0; i < this._props.length; i++) {
            BeanPropertyWriter prop = this._props[i];
            if (filter == null) {
                prop.depositSchemaProperty(propertiesNode, provider);
            } else {
                filter.depositSchemaProperty(prop, propertiesNode, provider);
            }
        }
        o.set("properties", propertiesNode);
        return o;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonObjectFormatVisitor objectVisitor;
        BeanPropertyWriter[] props;
        if (visitor == null || (objectVisitor = visitor.expectObjectFormat(typeHint)) == null) {
            return;
        }
        SerializerProvider provider = visitor.getProvider();
        if (this._propertyFilterId != null) {
            PropertyFilter filter = findPropertyFilter(visitor.getProvider(), this._propertyFilterId, null);
            int end = this._props.length;
            for (int i = 0; i < end; i++) {
                filter.depositSchemaProperty(this._props[i], objectVisitor, provider);
            }
            return;
        }
        Class<?> view = (this._filteredProps == null || provider == null) ? null : provider.getActiveView();
        if (view != null) {
            props = this._filteredProps;
        } else {
            props = this._props;
        }
        for (BeanPropertyWriter prop : props) {
            if (prop != null) {
                prop.depositSchemaProperty(objectVisitor, provider);
            }
        }
    }
}
