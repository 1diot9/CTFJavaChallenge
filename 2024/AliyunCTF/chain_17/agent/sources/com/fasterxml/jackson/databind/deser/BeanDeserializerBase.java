package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import com.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty;
import com.fasterxml.jackson.databind.deser.impl.MergingSettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReferenceProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ClassKey;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.NativeImageUtil;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BeanDeserializerBase.class */
public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, ValueInstantiator.Gettable, Serializable {
    private static final long serialVersionUID = 1;
    protected static final PropertyName TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
    protected final JavaType _beanType;
    protected final JsonFormat.Shape _serializationShape;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected JsonDeserializer<Object> _arrayDelegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;
    protected boolean _nonStandardCreation;
    protected boolean _vanillaProcessing;
    protected final BeanPropertyMap _beanProperties;
    protected final ValueInjector[] _injectables;
    protected SettableAnyProperty _anySetter;
    protected final Set<String> _ignorableProps;
    protected final Set<String> _includableProps;
    protected final boolean _ignoreAllUnknown;
    protected final boolean _needViewProcesing;
    protected final Map<String, SettableBeanProperty> _backRefs;
    protected transient HashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
    protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
    protected ExternalTypeHandler _externalTypeIdHandler;
    protected final ObjectIdReader _objectIdReader;

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public abstract JsonDeserializer<Object> unwrappingDeserializer(NameTransformer nameTransformer);

    public abstract BeanDeserializerBase withObjectIdReader(ObjectIdReader objectIdReader);

    public abstract BeanDeserializerBase withByNameInclusion(Set<String> set, Set<String> set2);

    public abstract BeanDeserializerBase withIgnoreAllUnknown(boolean z);

    protected abstract BeanDeserializerBase asArrayDeserializer();

    public abstract Object deserializeFromObject(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    protected abstract Object _deserializeUsingPropertyBased(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanDeserializerBase(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, Set<String> includableProps, boolean hasViews) {
        super(beanDesc.getType());
        this._beanType = beanDesc.getType();
        this._valueInstantiator = builder.getValueInstantiator();
        this._delegateDeserializer = null;
        this._arrayDelegateDeserializer = null;
        this._propertyBasedCreator = null;
        this._beanProperties = properties;
        this._backRefs = backRefs;
        this._ignorableProps = ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._includableProps = includableProps;
        this._anySetter = builder.getAnySetter();
        List<ValueInjector> injectables = builder.getInjectables();
        this._injectables = (injectables == null || injectables.isEmpty()) ? null : (ValueInjector[]) injectables.toArray(new ValueInjector[injectables.size()]);
        this._objectIdReader = builder.getObjectIdReader();
        this._nonStandardCreation = this._unwrappedPropertyHandler != null || this._valueInstantiator.canCreateUsingDelegate() || this._valueInstantiator.canCreateFromObjectWith() || !this._valueInstantiator.canCreateUsingDefault();
        JsonFormat.Value format = beanDesc.findExpectedFormat(null);
        this._serializationShape = format.getShape();
        this._needViewProcesing = hasViews;
        this._vanillaProcessing = !this._nonStandardCreation && this._injectables == null && !this._needViewProcesing && this._objectIdReader == null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanDeserializerBase(BeanDeserializerBase src) {
        this(src, src._ignoreAllUnknown);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanDeserializerBase(BeanDeserializerBase src, boolean ignoreAllUnknown) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._beanProperties = src._beanProperties;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._includableProps = src._includableProps;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanDeserializerBase(BeanDeserializerBase src, NameTransformer unwrapper) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = unwrapper != null || src._ignoreAllUnknown;
        this._includableProps = src._includableProps;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
        if (unwrapper != null) {
            uph = uph != null ? uph.renameAll(unwrapper) : uph;
            this._beanProperties = src._beanProperties.renameAll(unwrapper);
        } else {
            this._beanProperties = src._beanProperties;
        }
        this._unwrappedPropertyHandler = uph;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = false;
    }

    public BeanDeserializerBase(BeanDeserializerBase src, ObjectIdReader oir) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._includableProps = src._includableProps;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._objectIdReader = oir;
        if (oir == null) {
            this._beanProperties = src._beanProperties;
            this._vanillaProcessing = src._vanillaProcessing;
        } else {
            ObjectIdValueProperty idProp = new ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
            this._beanProperties = src._beanProperties.withProperty(idProp);
            this._vanillaProcessing = false;
        }
    }

    public BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps, Set<String> includableProps) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._includableProps = includableProps;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
        this._objectIdReader = src._objectIdReader;
        this._beanProperties = src._beanProperties.withoutProperties(ignorableProps, includableProps);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanDeserializerBase(BeanDeserializerBase src, BeanPropertyMap beanProps) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._beanProperties = beanProps;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._includableProps = src._includableProps;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps) {
        this(src, ignorableProps, src._includableProps);
    }

    public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
        throw new UnsupportedOperationException("Class " + getClass().getName() + " does not override `withBeanProperties()`, needs to");
    }

    @Deprecated
    public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
        return withByNameInclusion(ignorableProps, this._includableProps);
    }

    @Override // com.fasterxml.jackson.databind.deser.ResolvableDeserializer
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        SettableBeanProperty[] creatorProps;
        JsonDeserializer<Object> orig;
        JsonDeserializer<Object> unwrapping;
        ExternalTypeHandler.Builder extTypes = null;
        if (this._valueInstantiator.canCreateFromObjectWith()) {
            creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            if (this._ignorableProps != null || this._includableProps != null) {
                int end = creatorProps.length;
                for (int i = 0; i < end; i++) {
                    if (IgnorePropertiesUtil.shouldIgnore(creatorProps[i].getName(), this._ignorableProps, this._includableProps)) {
                        creatorProps[i].markAsIgnorable();
                    }
                }
            }
        } else {
            creatorProps = null;
        }
        UnwrappedPropertyHandler unwrapped = null;
        Iterator<SettableBeanProperty> it = this._beanProperties.iterator();
        while (it.hasNext()) {
            SettableBeanProperty prop = it.next();
            if (!prop.hasValueDeserializer()) {
                JsonDeserializer<?> deser = findConvertingDeserializer(ctxt, prop);
                if (deser == null) {
                    deser = ctxt.findNonContextualValueDeserializer(prop.getType());
                }
                SettableBeanProperty newProp = prop.withValueDeserializer(deser);
                _replaceProperty(this._beanProperties, creatorProps, prop, newProp);
            }
        }
        Iterator<SettableBeanProperty> it2 = this._beanProperties.iterator();
        while (it2.hasNext()) {
            SettableBeanProperty origProp = it2.next();
            SettableBeanProperty prop2 = _resolveManagedReferenceProperty(ctxt, origProp.withValueDeserializer(ctxt.handlePrimaryContextualization(origProp.getValueDeserializer(), origProp, origProp.getType())));
            if (!(prop2 instanceof ManagedReferenceProperty)) {
                prop2 = _resolvedObjectIdProperty(ctxt, prop2);
            }
            NameTransformer xform = _findPropertyUnwrapper(ctxt, prop2);
            if (xform != null && (unwrapping = (orig = prop2.getValueDeserializer()).unwrappingDeserializer(xform)) != orig && unwrapping != null) {
                SettableBeanProperty prop3 = prop2.withValueDeserializer(unwrapping);
                if (unwrapped == null) {
                    unwrapped = new UnwrappedPropertyHandler();
                }
                unwrapped.addProperty(prop3);
                this._beanProperties.remove(prop3);
            } else {
                PropertyMetadata md = prop2.getMetadata();
                SettableBeanProperty prop4 = _resolveInnerClassValuedProperty(ctxt, _resolveMergeAndNullSettings(ctxt, prop2, md));
                if (prop4 != origProp) {
                    _replaceProperty(this._beanProperties, creatorProps, origProp, prop4);
                }
                if (prop4.hasValueTypeDeserializer()) {
                    TypeDeserializer typeDeser = prop4.getValueTypeDeserializer();
                    if (typeDeser.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
                        if (extTypes == null) {
                            extTypes = ExternalTypeHandler.builder(this._beanType);
                        }
                        extTypes.addExternal(prop4, typeDeser);
                        this._beanProperties.remove(prop4);
                    }
                }
            }
        }
        if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
            this._anySetter = this._anySetter.withValueDeserializer(findDeserializer(ctxt, this._anySetter.getType(), this._anySetter.getProperty()));
        }
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", ClassUtil.getTypeDescription(this._beanType), ClassUtil.classNameOf(this._valueInstantiator)));
            }
            this._delegateDeserializer = _findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getDelegateCreator());
        }
        if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
            JavaType delegateType2 = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            if (delegateType2 == null) {
                ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", ClassUtil.getTypeDescription(this._beanType), ClassUtil.classNameOf(this._valueInstantiator)));
            }
            this._arrayDelegateDeserializer = _findDelegateDeserializer(ctxt, delegateType2, this._valueInstantiator.getArrayDelegateCreator());
        }
        if (creatorProps != null) {
            this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, this._beanProperties);
        }
        if (extTypes != null) {
            this._externalTypeIdHandler = extTypes.build(this._beanProperties);
            this._nonStandardCreation = true;
        }
        this._unwrappedPropertyHandler = unwrapped;
        if (unwrapped != null) {
            this._nonStandardCreation = true;
        }
        this._vanillaProcessing = this._vanillaProcessing && !this._nonStandardCreation;
    }

    protected void _replaceProperty(BeanPropertyMap props, SettableBeanProperty[] creatorProps, SettableBeanProperty origProp, SettableBeanProperty newProp) {
        props.replace(origProp, newProp);
        if (creatorProps != null) {
            int len = creatorProps.length;
            for (int i = 0; i < len; i++) {
                if (creatorProps[i] == origProp) {
                    creatorProps[i] = newProp;
                    return;
                }
            }
        }
    }

    private JsonDeserializer<Object> _findDelegateDeserializer(DeserializationContext ctxt, JavaType delegateType, AnnotatedWithParams delegateCreator) throws JsonMappingException {
        JsonDeserializer<Object> dd;
        BeanProperty.Std property = new BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, null, delegateCreator, PropertyMetadata.STD_OPTIONAL);
        TypeDeserializer td = (TypeDeserializer) delegateType.getTypeHandler();
        if (td == null) {
            td = ctxt.getConfig().findTypeDeserializer(delegateType);
        }
        JsonDeserializer<Object> dd2 = (JsonDeserializer) delegateType.getValueHandler();
        if (dd2 == null) {
            dd = findDeserializer(ctxt, delegateType, property);
        } else {
            dd = ctxt.handleSecondaryContextualization(dd2, property, delegateType);
        }
        if (td != null) {
            return new TypeWrappedDeserializer(td.forProperty(property), dd);
        }
        return dd;
    }

    protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
        Object convDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && (convDef = intr.findDeserializationConverter(prop.getMember())) != null) {
            Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
            JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
            JsonDeserializer<?> deser = ctxt.findNonContextualValueDeserializer(delegateType);
            return new StdDelegatingDeserializer(conv, delegateType, deser);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        BeanPropertyMap propsOrig;
        BeanPropertyMap props;
        ObjectIdInfo objectIdInfo;
        JavaType idType;
        SettableBeanProperty idProp;
        ObjectIdGenerator<?> idGen;
        ObjectIdReader oir = this._objectIdReader;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        AnnotatedMember accessor = _neitherNull(property, intr) ? property.getMember() : null;
        if (accessor != null && (objectIdInfo = intr.findObjectIdInfo(accessor)) != null) {
            ObjectIdInfo objectIdInfo2 = intr.findObjectReferenceInfo(accessor, objectIdInfo);
            Class<?> implClass = objectIdInfo2.getGeneratorType();
            ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo2);
            if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                PropertyName propName = objectIdInfo2.getPropertyName();
                idProp = findProperty(propName);
                if (idProp == null) {
                    return (JsonDeserializer) ctxt.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name %s", ClassUtil.nameOf(handledType()), ClassUtil.name(propName)));
                }
                idType = idProp.getType();
                idGen = new PropertyBasedObjectIdGenerator(objectIdInfo2.getScope());
            } else {
                JavaType type = ctxt.constructType(implClass);
                idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                idProp = null;
                idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo2);
            }
            JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
            oir = ObjectIdReader.construct(idType, objectIdInfo2.getPropertyName(), idGen, deser, idProp, resolver);
        }
        BeanDeserializerBase contextual = this;
        if (oir != null && oir != this._objectIdReader) {
            contextual = contextual.withObjectIdReader(oir);
        }
        if (accessor != null) {
            contextual = _handleByNameInclusion(ctxt, intr, contextual, accessor);
        }
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        JsonFormat.Shape shape = null;
        if (format != null) {
            if (format.hasShape()) {
                shape = format.getShape();
            }
            Boolean B = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            if (B != null && (props = (propsOrig = this._beanProperties).withCaseInsensitivity(B.booleanValue())) != propsOrig) {
                contextual = contextual.withBeanProperties(props);
            }
        }
        if (shape == null) {
            shape = this._serializationShape;
        }
        if (shape == JsonFormat.Shape.ARRAY) {
            contextual = contextual.asArrayDeserializer();
        }
        return contextual;
    }

    protected BeanDeserializerBase _handleByNameInclusion(DeserializationContext ctxt, AnnotationIntrospector intr, BeanDeserializerBase contextual, AnnotatedMember accessor) throws JsonMappingException {
        Set<String> newNamesToIgnore;
        DeserializationConfig config = ctxt.getConfig();
        JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName(config, accessor);
        if (ignorals.getIgnoreUnknown() && !this._ignoreAllUnknown) {
            contextual = contextual.withIgnoreAllUnknown(true);
        }
        Set<String> namesToIgnore = ignorals.findIgnoredForDeserialization();
        Set<String> prevNamesToIgnore = contextual._ignorableProps;
        if (namesToIgnore.isEmpty()) {
            newNamesToIgnore = prevNamesToIgnore;
        } else if (prevNamesToIgnore == null || prevNamesToIgnore.isEmpty()) {
            newNamesToIgnore = namesToIgnore;
        } else {
            newNamesToIgnore = new HashSet(prevNamesToIgnore);
            newNamesToIgnore.addAll(namesToIgnore);
        }
        Set<String> prevNamesToInclude = contextual._includableProps;
        Set<String> newNamesToInclude = IgnorePropertiesUtil.combineNamesToInclude(prevNamesToInclude, intr.findPropertyInclusionByName(config, accessor).getIncluded());
        if (newNamesToIgnore != prevNamesToIgnore || newNamesToInclude != prevNamesToInclude) {
            contextual = contextual.withByNameInclusion(newNamesToIgnore, newNamesToInclude);
        }
        return contextual;
    }

    protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
        String refName = prop.getManagedReferenceName();
        if (refName == null) {
            return prop;
        }
        JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
        SettableBeanProperty backProp = valueDeser.findBackReference(refName);
        if (backProp == null) {
            return (SettableBeanProperty) ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference %s: no back reference property found from type %s", ClassUtil.name(refName), ClassUtil.getTypeDescription(prop.getType())));
        }
        JavaType referredType = this._beanType;
        JavaType backRefType = backProp.getType();
        boolean isContainer = prop.getType().isContainerType();
        if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
            ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference %s: back reference type (%s) not compatible with managed type (%s)", ClassUtil.name(refName), ClassUtil.getTypeDescription(backRefType), referredType.getRawClass().getName()));
        }
        return new ManagedReferenceProperty(prop, refName, backProp, isContainer);
    }

    protected SettableBeanProperty _resolvedObjectIdProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
        ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
        JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
        ObjectIdReader objectIdReader = valueDeser == null ? null : valueDeser.getObjectIdReader();
        if (objectIdInfo == null && objectIdReader == null) {
            return prop;
        }
        return new ObjectIdReferenceProperty(prop, objectIdInfo);
    }

    protected NameTransformer _findPropertyUnwrapper(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
        NameTransformer unwrapper;
        AnnotatedMember am = prop.getMember();
        if (am != null && (unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am)) != null) {
            if (prop instanceof CreatorProperty) {
                ctxt.reportBadDefinition(getValueType(), String.format("Cannot define Creator property \"%s\" as `@JsonUnwrapped`: combination not yet supported", prop.getName()));
            }
            return unwrapper;
        }
        return null;
    }

    protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
        Class<?> valueClass;
        Class<?> enclosing;
        JsonDeserializer<Object> deser = prop.getValueDeserializer();
        if (deser instanceof BeanDeserializerBase) {
            BeanDeserializerBase bd = (BeanDeserializerBase) deser;
            ValueInstantiator vi = bd.getValueInstantiator();
            if (!vi.canCreateUsingDefault() && (enclosing = ClassUtil.getOuterClass((valueClass = prop.getType().getRawClass()))) != null && enclosing == this._beanType.getRawClass()) {
                for (Constructor<?> ctor : valueClass.getConstructors()) {
                    if (ctor.getParameterCount() == 1) {
                        Class<?>[] paramTypes = ctor.getParameterTypes();
                        if (enclosing.equals(paramTypes[0])) {
                            if (ctxt.canOverrideAccessModifiers()) {
                                ClassUtil.checkAndFixAccess(ctor, ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                            }
                            return new InnerClassProperty(prop, ctor);
                        }
                    }
                }
            }
        }
        return prop;
    }

    protected SettableBeanProperty _resolveMergeAndNullSettings(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
        PropertyMetadata.MergeInfo merge = propMetadata.getMergeInfo();
        if (merge != null) {
            JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
            Boolean mayMerge = valueDeser.supportsUpdate(ctxt.getConfig());
            if (mayMerge == null) {
                if (merge.fromDefaults) {
                    return prop;
                }
            } else if (!mayMerge.booleanValue()) {
                if (!merge.fromDefaults) {
                    ctxt.handleBadMerge(valueDeser);
                }
                return prop;
            }
            AnnotatedMember accessor = merge.getter;
            accessor.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            if (!(prop instanceof SetterlessProperty)) {
                prop = MergingSettableBeanProperty.construct(prop, accessor);
            }
        }
        NullValueProvider nuller = findValueNullProvider(ctxt, prop, propMetadata);
        if (nuller != null) {
            prop = prop.withNullProvider(nuller);
        }
        return prop;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.ALWAYS_NULL;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        try {
            return this._valueInstantiator.createUsingDefaultOrWithoutArguments(ctxt);
        } catch (IOException e) {
            return ClassUtil.throwAsMappingException(ctxt, e);
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return true;
    }

    public boolean isCaseInsensitive() {
        return this._beanProperties.isCaseInsensitive();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.TRUE;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Class<?> handledType() {
        return this._beanType.getRawClass();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }

    public boolean hasProperty(String propertyName) {
        return this._beanProperties.find(propertyName) != null;
    }

    public boolean hasViews() {
        return this._needViewProcesing;
    }

    public int getPropertyCount() {
        return this._beanProperties.size();
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Collection<Object> getKnownPropertyNames() {
        ArrayList<Object> names = new ArrayList<>();
        Iterator<SettableBeanProperty> it = this._beanProperties.iterator();
        while (it.hasNext()) {
            SettableBeanProperty prop = it.next();
            names.add(prop.getName());
        }
        return names;
    }

    @Deprecated
    public final Class<?> getBeanClass() {
        return this._beanType.getRawClass();
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer
    public JavaType getValueType() {
        return this._beanType;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.POJO;
    }

    public Iterator<SettableBeanProperty> properties() {
        if (this._beanProperties == null) {
            throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
        }
        return this._beanProperties.iterator();
    }

    public Iterator<SettableBeanProperty> creatorProperties() {
        if (this._propertyBasedCreator == null) {
            return Collections.emptyList().iterator();
        }
        return this._propertyBasedCreator.properties().iterator();
    }

    public SettableBeanProperty findProperty(PropertyName propertyName) {
        return findProperty(propertyName.getSimpleName());
    }

    public SettableBeanProperty findProperty(String propertyName) {
        SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyName);
        if (prop == null && this._propertyBasedCreator != null) {
            prop = this._propertyBasedCreator.findCreatorProperty(propertyName);
        }
        return prop;
    }

    public SettableBeanProperty findProperty(int propertyIndex) {
        SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyIndex);
        if (prop == null && this._propertyBasedCreator != null) {
            prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex);
        }
        return prop;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public SettableBeanProperty findBackReference(String logicalName) {
        if (this._backRefs == null) {
            return null;
        }
        return this._backRefs.get(logicalName);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    public void replaceProperty(SettableBeanProperty original, SettableBeanProperty replacement) {
        this._beanProperties.replace(original, replacement);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        Object id;
        if (this._objectIdReader != null) {
            if (p.canReadObjectId() && (id = p.getObjectId()) != null) {
                Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
                return _handleTypedObjectId(p, ctxt, ob, id);
            }
            JsonToken t = p.currentToken();
            if (t != null) {
                if (t.isScalarValue()) {
                    return deserializeFromObjectId(p, ctxt);
                }
                if (t == JsonToken.START_OBJECT) {
                    t = p.nextToken();
                }
                if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.currentName(), p)) {
                    return deserializeFromObjectId(p, ctxt);
                }
            }
        }
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object _handleTypedObjectId(JsonParser p, DeserializationContext ctxt, Object pojo, Object rawId) throws IOException {
        Object id;
        JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
        if (idDeser.handledType() == rawId.getClass()) {
            id = rawId;
        } else {
            id = _convertObjectId(p, ctxt, rawId, idDeser);
        }
        ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        roid.bindItem(pojo);
        SettableBeanProperty idProp = this._objectIdReader.idProperty;
        if (idProp != null) {
            return idProp.setAndReturn(pojo, id);
        }
        return pojo;
    }

    protected Object _convertObjectId(JsonParser p, DeserializationContext ctxt, Object rawId, JsonDeserializer<Object> idDeser) throws IOException {
        TokenBuffer buf = ctxt.bufferForInputBuffering(p);
        if (rawId instanceof String) {
            buf.writeString((String) rawId);
        } else if (rawId instanceof Long) {
            buf.writeNumber(((Long) rawId).longValue());
        } else if (rawId instanceof Integer) {
            buf.writeNumber(((Integer) rawId).intValue());
        } else {
            buf.writeObject(rawId);
        }
        JsonParser bufParser = buf.asParser(p.streamReadConstraints());
        bufParser.nextToken();
        return idDeser.deserialize(bufParser, ctxt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object deserializeWithObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
        return deserializeFromObject(p, ctxt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object deserializeFromObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object id = this._objectIdReader.readObjectReference(p, ctxt);
        ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        Object pojo = roid.resolve();
        if (pojo == null) {
            throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", p.getCurrentLocation(), roid);
        }
        return pojo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object deserializeFromObjectUsingNonDefault(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
        if (delegateDeser != null) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                injectValues(ctxt, bean);
            }
            return bean;
        }
        if (this._propertyBasedCreator != null) {
            return _deserializeUsingPropertyBased(p, ctxt);
        }
        Class<?> raw = this._beanType.getRawClass();
        if (ClassUtil.isNonStaticInnerClass(raw)) {
            return ctxt.handleMissingInstantiator(raw, null, p, "non-static inner classes like this can only by instantiated using default, no-argument constructor", new Object[0]);
        }
        if (NativeImageUtil.needsReflectionConfiguration(raw)) {
            return ctxt.handleMissingInstantiator(raw, null, p, "cannot deserialize from Object value (no delegate- or property-based Creator): this appears to be a native image, in which case you may need to configure reflection for the class that is to be deserialized", new Object[0]);
        }
        return ctxt.handleMissingInstantiator(raw, getValueInstantiator(), p, "cannot deserialize from Object value (no delegate- or property-based Creator)", new Object[0]);
    }

    public Object deserializeFromNumber(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null) {
            return deserializeFromObjectId(p, ctxt);
        }
        JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
        JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.INT) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
                Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    injectValues(ctxt, bean);
                }
                return bean;
            }
            return this._valueInstantiator.createFromInt(ctxt, p.getIntValue());
        }
        if (nt == JsonParser.NumberType.LONG) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
                Object bean2 = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    injectValues(ctxt, bean2);
                }
                return bean2;
            }
            return this._valueInstantiator.createFromLong(ctxt, p.getLongValue());
        }
        if (nt == JsonParser.NumberType.BIG_INTEGER) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromBigInteger()) {
                Object bean3 = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    injectValues(ctxt, bean3);
                }
                return bean3;
            }
            return this._valueInstantiator.createFromBigInteger(ctxt, p.getBigIntegerValue());
        }
        return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
    }

    public Object deserializeFromString(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null) {
            return deserializeFromObjectId(p, ctxt);
        }
        JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
        if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                injectValues(ctxt, bean);
            }
            return bean;
        }
        return _deserializeFromString(p, ctxt);
    }

    public Object deserializeFromDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonParser.NumberType t = p.getNumberType();
        if (t == JsonParser.NumberType.DOUBLE || t == JsonParser.NumberType.FLOAT) {
            JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromDouble()) {
                Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    injectValues(ctxt, bean);
                }
                return bean;
            }
            return this._valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
        }
        if (t == JsonParser.NumberType.BIG_DECIMAL) {
            JsonDeserializer<Object> delegateDeser2 = _delegateDeserializer();
            if (delegateDeser2 != null && !this._valueInstantiator.canCreateFromBigDecimal()) {
                Object bean2 = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser2.deserialize(p, ctxt));
                if (this._injectables != null) {
                    injectValues(ctxt, bean2);
                }
                return bean2;
            }
            return this._valueInstantiator.createFromBigDecimal(ctxt, p.getDecimalValue());
        }
        return ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
    }

    public Object deserializeFromBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
        if (delegateDeser != null && !this._valueInstantiator.canCreateFromBoolean()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                injectValues(ctxt, bean);
            }
            return bean;
        }
        boolean value = p.currentToken() == JsonToken.VALUE_TRUE;
        return this._valueInstantiator.createFromBoolean(ctxt, value);
    }

    @Deprecated
    public Object deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        return _deserializeFromArray(p, ctxt);
    }

    public Object deserializeFromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null) {
            return deserializeFromObjectId(p, ctxt);
        }
        JsonDeserializer<Object> delegateDeser = _delegateDeserializer();
        if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                injectValues(ctxt, bean);
            }
            return bean;
        }
        Object value = p.getEmbeddedObject();
        if (value != null && !this._beanType.isTypeOrSuperTypeOf(value.getClass())) {
            value = ctxt.handleWeirdNativeValue(this._beanType, value, p);
        }
        return value;
    }

    protected final JsonDeserializer<Object> _delegateDeserializer() {
        JsonDeserializer<Object> deser = this._delegateDeserializer;
        if (deser == null) {
            deser = this._arrayDelegateDeserializer;
        }
        return deser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void injectValues(DeserializationContext ctxt, Object bean) throws IOException {
        for (ValueInjector injector : this._injectables) {
            injector.inject(ctxt, bean);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
        unknownTokens.writeEndObject();
        JsonParser bufferParser = unknownTokens.asParser();
        while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
            String propName = bufferParser.currentName();
            bufferParser.nextToken();
            handleUnknownProperty(bufferParser, ctxt, bean, propName);
        }
        return bean;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleUnknownVanilla(JsonParser p, DeserializationContext ctxt, Object beanOrBuilder, String propName) throws IOException {
        if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
            handleIgnoredProperty(p, ctxt, beanOrBuilder, propName);
            return;
        }
        if (this._anySetter != null) {
            try {
                this._anySetter.deserializeAndSet(p, ctxt, beanOrBuilder, propName);
                return;
            } catch (Exception e) {
                wrapAndThrow(e, beanOrBuilder, propName, ctxt);
                return;
            }
        }
        handleUnknownProperty(p, ctxt, beanOrBuilder, propName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer
    public void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
        if (this._ignoreAllUnknown) {
            p.skipChildren();
            return;
        }
        if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
            handleIgnoredProperty(p, ctxt, beanOrClass, propName);
        }
        super.handleUnknownProperty(p, ctxt, beanOrClass, propName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleIgnoredProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
            throw IgnoredPropertyException.from(p, beanOrClass, propName, getKnownPropertyNames());
        }
        p.skipChildren();
    }

    @Deprecated
    protected Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
        StreamReadConstraints streamReadConstraints = p == null ? StreamReadConstraints.defaults() : p.streamReadConstraints();
        return handlePolymorphic(p, ctxt, streamReadConstraints, bean, unknownTokens);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, StreamReadConstraints streamReadConstraints, Object bean, TokenBuffer unknownTokens) throws IOException {
        JsonDeserializer<Object> subDeser = _findSubclassDeserializer(ctxt, bean, unknownTokens);
        if (subDeser != null) {
            if (unknownTokens != null) {
                unknownTokens.writeEndObject();
                JsonParser p2 = unknownTokens.asParser(streamReadConstraints);
                p2.nextToken();
                bean = subDeser.deserialize(p2, ctxt, bean);
            }
            if (p != null) {
                bean = subDeser.deserialize(p, ctxt, bean);
            }
            return bean;
        }
        if (unknownTokens != null) {
            bean = handleUnknownProperties(ctxt, bean, unknownTokens);
        }
        if (p != null) {
            bean = deserialize(p, ctxt, bean);
        }
        return bean;
    }

    protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
        JsonDeserializer<Object> subDeser;
        synchronized (this) {
            subDeser = this._subDeserializers == null ? null : this._subDeserializers.get(new ClassKey(bean.getClass()));
        }
        if (subDeser != null) {
            return subDeser;
        }
        JavaType type = ctxt.constructType(bean.getClass());
        JsonDeserializer<Object> subDeser2 = ctxt.findRootValueDeserializer(type);
        if (subDeser2 != null) {
            synchronized (this) {
                if (this._subDeserializers == null) {
                    this._subDeserializers = new HashMap<>();
                }
                this._subDeserializers.put(new ClassKey(bean.getClass()), subDeser2);
            }
        }
        return subDeser2;
    }

    public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
        throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
    }

    private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
        while ((t instanceof InvocationTargetException) && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JacksonException)) {
                throw ((IOException) t);
            }
        } else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object wrapInstantiationProblem(Throwable t, DeserializationContext ctxt) throws IOException {
        while ((t instanceof InvocationTargetException) && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        if (t instanceof IOException) {
            throw ((IOException) t);
        }
        if (ctxt == null) {
            throw new IllegalArgumentException(t.getMessage(), t);
        }
        if (!ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS)) {
            ClassUtil.throwIfRTE(t);
        }
        return ctxt.handleInstantiationProblem(this._beanType.getRawClass(), null, t);
    }
}
