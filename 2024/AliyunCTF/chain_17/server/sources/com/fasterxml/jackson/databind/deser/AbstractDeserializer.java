package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/AbstractDeserializer.class */
public class AbstractDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer, Serializable {
    private static final long serialVersionUID = 1;
    protected final JavaType _baseType;
    protected final ObjectIdReader _objectIdReader;
    protected final Map<String, SettableBeanProperty> _backRefProperties;
    protected transient Map<String, SettableBeanProperty> _properties;
    protected final boolean _acceptString;
    protected final boolean _acceptBoolean;
    protected final boolean _acceptInt;
    protected final boolean _acceptDouble;

    public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps, Map<String, SettableBeanProperty> props) {
        this._baseType = beanDesc.getType();
        this._objectIdReader = builder.getObjectIdReader();
        this._backRefProperties = backRefProps;
        this._properties = props;
        Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        this._acceptBoolean = cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class);
        this._acceptInt = cls == Integer.TYPE || cls.isAssignableFrom(Integer.class);
        this._acceptDouble = cls == Double.TYPE || cls.isAssignableFrom(Double.class);
    }

    @Deprecated
    public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps) {
        this(builder, beanDesc, backRefProps, null);
    }

    protected AbstractDeserializer(BeanDescription beanDesc) {
        this._baseType = beanDesc.getType();
        this._objectIdReader = null;
        this._backRefProperties = null;
        Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        this._acceptBoolean = cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class);
        this._acceptInt = cls == Integer.TYPE || cls.isAssignableFrom(Integer.class);
        this._acceptDouble = cls == Double.TYPE || cls.isAssignableFrom(Double.class);
    }

    protected AbstractDeserializer(AbstractDeserializer base, ObjectIdReader objectIdReader, Map<String, SettableBeanProperty> props) {
        this._baseType = base._baseType;
        this._backRefProperties = base._backRefProperties;
        this._acceptString = base._acceptString;
        this._acceptBoolean = base._acceptBoolean;
        this._acceptInt = base._acceptInt;
        this._acceptDouble = base._acceptDouble;
        this._objectIdReader = objectIdReader;
        this._properties = props;
    }

    public static AbstractDeserializer constructForNonPOJO(BeanDescription beanDesc) {
        return new AbstractDeserializer(beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        AnnotatedMember accessor;
        ObjectIdInfo objectIdInfo;
        JavaType idType;
        ObjectIdGenerator<?> idGen;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (property != null && intr != null && (accessor = property.getMember()) != null && (objectIdInfo = intr.findObjectIdInfo(accessor)) != null) {
            SettableBeanProperty idProp = null;
            ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
            ObjectIdInfo objectIdInfo2 = intr.findObjectReferenceInfo(accessor, objectIdInfo);
            Class<?> implClass = objectIdInfo2.getGeneratorType();
            if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                PropertyName propName = objectIdInfo2.getPropertyName();
                idProp = this._properties == null ? null : this._properties.get(propName.getSimpleName());
                if (idProp == null) {
                    ctxt.reportBadDefinition(this._baseType, String.format("Invalid Object Id definition for %s: cannot find property with name %s", ClassUtil.nameOf(handledType()), ClassUtil.name(propName)));
                }
                idType = idProp.getType();
                idGen = new PropertyBasedObjectIdGenerator(objectIdInfo2.getScope());
            } else {
                resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo2);
                JavaType type = ctxt.constructType(implClass);
                idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo2);
            }
            JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
            ObjectIdReader oir = ObjectIdReader.construct(idType, objectIdInfo2.getPropertyName(), idGen, deser, idProp, resolver);
            return new AbstractDeserializer(this, oir, (Map<String, SettableBeanProperty>) null);
        }
        if (this._properties == null) {
            return this;
        }
        return new AbstractDeserializer(this, this._objectIdReader, (Map<String, SettableBeanProperty>) null);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Class<?> handledType() {
        return this._baseType.getRawClass();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.POJO;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public SettableBeanProperty findBackReference(String logicalName) {
        if (this._backRefProperties == null) {
            return null;
        }
        return this._backRefProperties.get(logicalName);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        if (this._objectIdReader != null) {
            JsonToken t = p.currentToken();
            if (t != null) {
                if (t.isScalarValue()) {
                    return _deserializeFromObjectId(p, ctxt);
                }
                if (t == JsonToken.START_OBJECT) {
                    t = p.nextToken();
                }
                if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.currentName(), p)) {
                    return _deserializeFromObjectId(p, ctxt);
                }
            }
        }
        Object result = _deserializeIfNatural(p, ctxt);
        if (result != null) {
            return result;
        }
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ValueInstantiator bogus = new ValueInstantiator.Base(this._baseType);
        return ctxt.handleMissingInstantiator(this._baseType.getRawClass(), bogus, p, "abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information", new Object[0]);
    }

    protected Object _deserializeIfNatural(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 6:
                if (this._acceptString) {
                    return p.getText();
                }
                return null;
            case 7:
                if (this._acceptInt) {
                    return Integer.valueOf(p.getIntValue());
                }
                return null;
            case 8:
                if (this._acceptDouble) {
                    return Double.valueOf(p.getDoubleValue());
                }
                return null;
            case 9:
                if (this._acceptBoolean) {
                    return Boolean.TRUE;
                }
                return null;
            case 10:
                if (this._acceptBoolean) {
                    return Boolean.FALSE;
                }
                return null;
            default:
                return null;
        }
    }

    protected Object _deserializeFromObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object id = this._objectIdReader.readObjectReference(p, ctxt);
        ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        Object pojo = roid.resolve();
        if (pojo == null) {
            throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] -- unresolved forward-reference?", p.getCurrentLocation(), roid);
        }
        return pojo;
    }
}
