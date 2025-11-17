package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators;
import com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers;
import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.EnumNamingStrategyFactory;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BasicDeserializerFactory.class */
public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
    private static final Class<?> CLASS_OBJECT = Object.class;
    private static final Class<?> CLASS_STRING = String.class;
    private static final Class<?> CLASS_CHAR_SEQUENCE = CharSequence.class;
    private static final Class<?> CLASS_ITERABLE = Iterable.class;
    private static final Class<?> CLASS_MAP_ENTRY = Map.Entry.class;
    private static final Class<?> CLASS_SERIALIZABLE = Serializable.class;
    protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
    protected final DeserializerFactoryConfig _factoryConfig;

    protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig deserializerFactoryConfig);

    /* JADX INFO: Access modifiers changed from: protected */
    public BasicDeserializerFactory(DeserializerFactoryConfig config) {
        this._factoryConfig = config;
    }

    public DeserializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
        return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
        return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
        return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
        return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
        return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
        JavaType next;
        while (true) {
            next = _mapAbstractType2(config, type);
            if (next == null) {
                return type;
            }
            Class<?> prevCls = type.getRawClass();
            Class<?> nextCls = next.getRawClass();
            if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
                break;
            }
            type = next;
        }
        throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
    }

    private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
        Class<?> currClass = type.getRawClass();
        if (this._factoryConfig.hasAbstractTypeResolvers()) {
            for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
                JavaType concrete = resolver.findTypeMapping(config, type);
                if (concrete != null && !concrete.hasRawClass(currClass)) {
                    return concrete;
                }
            }
            return null;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        ValueInstantiator instantiator = null;
        AnnotatedClass ac = beanDesc.getClassInfo();
        Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
        if (instDef != null) {
            instantiator = _valueInstantiatorInstance(config, ac, instDef);
        }
        if (instantiator == null) {
            instantiator = JDKValueInstantiators.findStdValueInstantiator(config, beanDesc.getBeanClass());
            if (instantiator == null) {
                instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
            }
        }
        if (this._factoryConfig.hasValueInstantiators()) {
            for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
                instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
                if (instantiator == null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", insts.getClass().getName());
                }
            }
        }
        if (instantiator != null) {
            instantiator = instantiator.createContextual(ctxt, beanDesc);
        }
        return instantiator;
    }

    protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        ConstructorDetector ctorDetector = config.getConstructorDetector();
        CreatorCollector creators = new CreatorCollector(beanDesc, config);
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
        CreatorCollectionState ccState = new CreatorCollectionState(ctxt, beanDesc, vchecker, creators, creatorDefs);
        _addExplicitFactoryCreators(ctxt, ccState, !ctorDetector.requireCtorAnnotation());
        if (beanDesc.getType().isConcrete()) {
            boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
            if (!isNonStaticInnerClass) {
                boolean findImplicit = ctorDetector.shouldIntrospectorImplicitConstructors(beanDesc.getBeanClass());
                _addExplicitConstructorCreators(ctxt, ccState, findImplicit);
                if (ccState.hasImplicitConstructorCandidates() && !ccState.hasExplicitConstructors()) {
                    _addImplicitConstructorCreators(ctxt, ccState, ccState.implicitConstructorCandidates());
                }
            }
        }
        if (ccState.hasImplicitFactoryCandidates() && !ccState.hasExplicitFactories() && !ccState.hasExplicitConstructors()) {
            _addImplicitFactoryCreators(ctxt, ccState, ccState.implicitFactoryCandidates());
        }
        return ccState.creators.constructValueInstantiator(ctxt);
    }

    protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = Collections.emptyMap();
        for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
            Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
            while (it.hasNext()) {
                AnnotatedParameter param = it.next();
                AnnotatedWithParams owner = param.getOwner();
                BeanPropertyDefinition[] defs = result.get(owner);
                int index = param.getIndex();
                if (defs == null) {
                    if (result.isEmpty()) {
                        result = new LinkedHashMap();
                    }
                    defs = new BeanPropertyDefinition[owner.getParameterCount()];
                    result.put(owner, defs);
                } else if (defs[index] != null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Conflict: parameter #%d of %s bound to more than one property; %s vs %s", Integer.valueOf(index), owner, defs[index], propDef);
                }
                defs[index] = propDef;
            }
        }
        return result;
    }

    public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
        ValueInstantiator inst;
        if (instDef == null) {
            return null;
        }
        if (instDef instanceof ValueInstantiator) {
            return (ValueInstantiator) instDef;
        }
        if (!(instDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
        }
        Class<?> instClass = (Class) instDef;
        if (ClassUtil.isBogusClass(instClass)) {
            return null;
        }
        if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
        }
        HandlerInstantiator hi = config.getHandlerInstantiator();
        if (hi != null && (inst = hi.valueInstantiatorInstance(config, annotated, instClass)) != null) {
            return inst;
        }
        return (ValueInstantiator) ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
    }

    @Deprecated
    protected void _addRecordConstructor(DeserializationContext ctxt, CreatorCollectionState ccState, AnnotatedConstructor canonical, List<String> implicitNames) throws JsonMappingException {
        int argCount = canonical.getParameterCount();
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
        for (int i = 0; i < argCount; i++) {
            AnnotatedParameter param = canonical.getParameter(i);
            JacksonInject.Value injectable = intr.findInjectableValue(param);
            PropertyName name = intr.findNameForDeserialization(param);
            if (name == null || name.isEmpty()) {
                name = PropertyName.construct(implicitNames.get(i));
            }
            properties[i] = constructCreatorProperty(ctxt, ccState.beanDesc, name, i, param, injectable);
        }
        ccState.creators.addPropertyCreator(canonical, false, properties);
    }

    protected void _addExplicitConstructorCreators(DeserializationContext ctxt, CreatorCollectionState ccState, boolean findImplicit) throws JsonMappingException {
        BeanDescription beanDesc = ccState.beanDesc;
        CreatorCollector creators = ccState.creators;
        AnnotationIntrospector intr = ccState.annotationIntrospector();
        VisibilityChecker<?> vchecker = ccState.vchecker;
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
        AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
        if (defaultCtor != null && (!creators.hasDefaultCreator() || _hasCreatorAnnotation(ctxt, defaultCtor))) {
            creators.setDefaultCreator(defaultCtor);
        }
        for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
            JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), ctor);
            if (JsonCreator.Mode.DISABLED != creatorMode) {
                if (creatorMode == null) {
                    if (findImplicit && vchecker.isCreatorVisible(ctor)) {
                        ccState.addImplicitConstructorCandidate(CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                    }
                } else {
                    switch (creatorMode) {
                        case DELEGATING:
                            _addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, null));
                            break;
                        case PROPERTIES:
                            _addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                            break;
                        default:
                            _addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)), ctxt.getConfig().getConstructorDetector());
                            break;
                    }
                    ccState.increaseExplicitConstructorCount();
                }
            }
        }
    }

    protected void _addImplicitConstructorCreators(DeserializationContext ctxt, CreatorCollectionState ccState, List<CreatorCandidate> ctorCandidates) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        BeanDescription beanDesc = ccState.beanDesc;
        CreatorCollector creators = ccState.creators;
        AnnotationIntrospector intr = ccState.annotationIntrospector();
        VisibilityChecker<?> vchecker = ccState.vchecker;
        List<AnnotatedWithParams> implicitCtors = null;
        boolean preferPropsBased = config.getConstructorDetector().singleArgCreatorDefaultsToProperties() && !beanDesc.isRecordType();
        for (CreatorCandidate candidate : ctorCandidates) {
            int argCount = candidate.paramCount();
            AnnotatedWithParams ctor = candidate.creator();
            if (argCount == 1) {
                BeanPropertyDefinition propDef = candidate.propertyDef(0);
                boolean useProps = preferPropsBased || _checkIfCreatorPropertyBased(beanDesc, intr, ctor, propDef);
                if (useProps) {
                    SettableBeanProperty[] properties = new SettableBeanProperty[1];
                    JacksonInject.Value injection = candidate.injection(0);
                    PropertyName name = candidate.paramName(0);
                    if (name == null) {
                        name = candidate.findImplicitParamName(0);
                        if (name == null && injection == null) {
                        }
                    }
                    properties[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, candidate.parameter(0), injection);
                    creators.addPropertyCreator(ctor, false, properties);
                } else {
                    _handleSingleArgumentCreator(creators, ctor, false, vchecker.isCreatorVisible(ctor));
                    if (propDef != null) {
                        ((POJOPropertyBuilder) propDef).removeConstructors();
                    }
                }
            } else {
                int nonAnnotatedParamIndex = -1;
                SettableBeanProperty[] properties2 = new SettableBeanProperty[argCount];
                int explicitNameCount = 0;
                int injectCount = 0;
                for (int i = 0; i < argCount; i++) {
                    AnnotatedParameter param = ctor.getParameter(i);
                    BeanPropertyDefinition propDef2 = candidate.propertyDef(i);
                    JacksonInject.Value injectable = intr.findInjectableValue(param);
                    PropertyName name2 = propDef2 == null ? null : propDef2.getFullName();
                    if (propDef2 != null && (propDef2.isExplicitlyNamed() || beanDesc.isRecordType())) {
                        explicitNameCount++;
                        properties2[i] = constructCreatorProperty(ctxt, beanDesc, name2, i, param, injectable);
                    } else if (injectable != null) {
                        injectCount++;
                        properties2[i] = constructCreatorProperty(ctxt, beanDesc, name2, i, param, injectable);
                    } else {
                        NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                        if (unwrapper != null) {
                            _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                        } else if (nonAnnotatedParamIndex < 0) {
                            nonAnnotatedParamIndex = i;
                        }
                    }
                }
                int namedCount = explicitNameCount + 0;
                if (explicitNameCount > 0 || injectCount > 0) {
                    if (namedCount + injectCount == argCount) {
                        creators.addPropertyCreator(ctor, false, properties2);
                    } else if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                        creators.addDelegatingCreator(ctor, false, properties2, 0);
                    } else {
                        PropertyName impl = candidate.findImplicitParamName(nonAnnotatedParamIndex);
                        if (impl == null || impl.isEmpty()) {
                            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", Integer.valueOf(nonAnnotatedParamIndex), ctor);
                        }
                    }
                }
                if (!creators.hasDefaultCreator()) {
                    if (implicitCtors == null) {
                        implicitCtors = new LinkedList<>();
                    }
                    implicitCtors.add(ctor);
                }
            }
        }
        if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
            _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
        }
    }

    protected void _addExplicitFactoryCreators(DeserializationContext ctxt, CreatorCollectionState ccState, boolean findImplicit) throws JsonMappingException {
        BeanDescription beanDesc = ccState.beanDesc;
        CreatorCollector creators = ccState.creators;
        AnnotationIntrospector intr = ccState.annotationIntrospector();
        VisibilityChecker<?> vchecker = ccState.vchecker;
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), factory);
            int argCount = factory.getParameterCount();
            if (creatorMode == null) {
                if (findImplicit && argCount == 1 && vchecker.isCreatorVisible(factory)) {
                    ccState.addImplicitFactoryCandidate(CreatorCandidate.construct(intr, factory, null));
                }
            } else if (creatorMode != JsonCreator.Mode.DISABLED) {
                if (argCount == 0) {
                    creators.setDefaultCreator(factory);
                } else {
                    switch (creatorMode) {
                        case DELEGATING:
                            _addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, null));
                            break;
                        case PROPERTIES:
                            _addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
                            break;
                        case DEFAULT:
                        default:
                            _addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)), ConstructorDetector.DEFAULT);
                            break;
                    }
                    ccState.increaseExplicitFactoryCount();
                }
            }
        }
    }

    protected void _addImplicitFactoryCreators(DeserializationContext ctxt, CreatorCollectionState ccState, List<CreatorCandidate> factoryCandidates) throws JsonMappingException {
        BeanDescription beanDesc = ccState.beanDesc;
        CreatorCollector creators = ccState.creators;
        AnnotationIntrospector intr = ccState.annotationIntrospector();
        VisibilityChecker<?> vchecker = ccState.vchecker;
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams = ccState.creatorParams;
        for (CreatorCandidate candidate : factoryCandidates) {
            int argCount = candidate.paramCount();
            AnnotatedWithParams factory = candidate.creator();
            BeanPropertyDefinition[] propDefs = creatorParams.get(factory);
            if (argCount == 1) {
                BeanPropertyDefinition argDef = candidate.propertyDef(0);
                boolean useProps = _checkIfCreatorPropertyBased(beanDesc, intr, factory, argDef);
                if (!useProps) {
                    _handleSingleArgumentCreator(creators, factory, false, vchecker.isCreatorVisible(factory));
                    if (argDef != null) {
                        ((POJOPropertyBuilder) argDef).removeConstructors();
                    }
                } else {
                    AnnotatedParameter nonAnnotatedParam = null;
                    SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                    int explicitNameCount = 0;
                    int injectCount = 0;
                    for (int i = 0; i < argCount; i++) {
                        AnnotatedParameter param = factory.getParameter(i);
                        BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
                        JacksonInject.Value injectable = intr.findInjectableValue(param);
                        PropertyName name = propDef == null ? null : propDef.getFullName();
                        if (propDef != null && propDef.isExplicitlyNamed()) {
                            explicitNameCount++;
                            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                        } else if (injectable != null) {
                            injectCount++;
                            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                        } else {
                            NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                            if (unwrapper != null) {
                                _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                            } else if (nonAnnotatedParam == null) {
                                nonAnnotatedParam = param;
                            }
                        }
                    }
                    int namedCount = explicitNameCount + 0;
                    if (explicitNameCount > 0 || injectCount > 0) {
                        if (namedCount + injectCount == argCount) {
                            creators.addPropertyCreator(factory, false, properties);
                        } else if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                            creators.addDelegatingCreator(factory, false, properties, 0);
                        } else {
                            Object[] objArr = new Object[2];
                            objArr[0] = Integer.valueOf(nonAnnotatedParam == null ? -1 : nonAnnotatedParam.getIndex());
                            objArr[1] = factory;
                            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of factory method %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", objArr);
                        }
                    }
                }
            }
        }
    }

    protected void _addExplicitDelegatingCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
        int ix = -1;
        int argCount = candidate.paramCount();
        SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
        for (int i = 0; i < argCount; i++) {
            AnnotatedParameter param = candidate.parameter(i);
            JacksonInject.Value injectId = candidate.injection(i);
            if (injectId != null) {
                properties[i] = constructCreatorProperty(ctxt, beanDesc, null, i, param, injectId);
            } else if (ix < 0) {
                ix = i;
            } else {
                ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", Integer.valueOf(ix), Integer.valueOf(i), candidate);
            }
        }
        if (ix < 0) {
            ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", candidate);
        }
        if (argCount == 1) {
            _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
            BeanPropertyDefinition paramDef = candidate.propertyDef(0);
            if (paramDef != null) {
                ((POJOPropertyBuilder) paramDef).removeConstructors();
                return;
            }
            return;
        }
        creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
    }

    protected void _addExplicitPropertyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
        int paramCount = candidate.paramCount();
        SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
        for (int i = 0; i < paramCount; i++) {
            JacksonInject.Value injectId = candidate.injection(i);
            AnnotatedParameter param = candidate.parameter(i);
            PropertyName name = candidate.paramName(i);
            if (name == null) {
                NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(param);
                if (unwrapper != null) {
                    _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                }
                name = candidate.findImplicitParamName(i);
                _validateNamedPropertyParameter(ctxt, beanDesc, candidate, i, name, injectId);
            }
            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
        }
        creators.addPropertyCreator(candidate.creator(), true, properties);
    }

    @Deprecated
    protected void _addExplicitAnyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
        _addExplicitAnyCreator(ctxt, beanDesc, creators, candidate, ctxt.getConfig().getConstructorDetector());
    }

    protected void _addExplicitAnyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate, ConstructorDetector ctorDetector) throws JsonMappingException {
        boolean useProps;
        int oneNotInjected;
        if (1 != candidate.paramCount()) {
            if (!ctorDetector.singleArgCreatorDefaultsToProperties() && (oneNotInjected = candidate.findOnlyParamWithoutInjection()) >= 0 && (ctorDetector.singleArgCreatorDefaultsToDelegating() || candidate.paramName(oneNotInjected) == null)) {
                _addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
                return;
            } else {
                _addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
                return;
            }
        }
        AnnotatedParameter param = candidate.parameter(0);
        JacksonInject.Value injectId = candidate.injection(0);
        PropertyName paramName = null;
        switch (ctorDetector.singleArgMode()) {
            case DELEGATING:
                useProps = false;
                break;
            case PROPERTIES:
                useProps = true;
                paramName = candidate.paramName(0);
                if (paramName == null) {
                    _validateNamedPropertyParameter(ctxt, beanDesc, candidate, 0, paramName, injectId);
                    break;
                }
                break;
            case REQUIRE_MODE:
                ctxt.reportBadTypeDefinition(beanDesc, "Single-argument constructor (%s) is annotated but no 'mode' defined; `CreatorDetector`configured with `SingleArgConstructor.REQUIRE_MODE`", candidate.creator());
                return;
            case HEURISTIC:
            default:
                BeanPropertyDefinition paramDef = candidate.propertyDef(0);
                paramName = candidate.explicitParamName(0);
                useProps = paramName != null;
                if (!useProps && beanDesc.findJsonValueAccessor() == null) {
                    if (injectId != null) {
                        useProps = true;
                        break;
                    } else if (paramDef != null) {
                        paramName = candidate.paramName(0);
                        useProps = paramName != null && paramDef.couldSerialize();
                        break;
                    }
                }
                break;
        }
        if (useProps) {
            SettableBeanProperty[] properties = {constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId)};
            creators.addPropertyCreator(candidate.creator(), true, properties);
            return;
        }
        _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
        BeanPropertyDefinition paramDef2 = candidate.propertyDef(0);
        if (paramDef2 != null) {
            ((POJOPropertyBuilder) paramDef2).removeConstructors();
        }
    }

    private boolean _checkIfCreatorPropertyBased(BeanDescription beanDesc, AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef) {
        if (propDef != null && propDef.isExplicitlyNamed()) {
            return true;
        }
        if (beanDesc.findJsonValueAccessor() != null) {
            return false;
        }
        if (intr.findInjectableValue(creator.getParameter(0)) != null) {
            return true;
        }
        if (propDef != null) {
            String implName = propDef.getName();
            if (implName != null && !implName.isEmpty() && propDef.couldSerialize()) {
                return true;
            }
            if (!propDef.isExplicitlyNamed() && beanDesc.isRecordType()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedWithParams> implicitCtors) throws JsonMappingException {
        AnnotatedWithParams found = null;
        SettableBeanProperty[] foundProps = null;
        Iterator<AnnotatedWithParams> it = implicitCtors.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            AnnotatedWithParams ctor = it.next();
            if (vchecker.isCreatorVisible(ctor)) {
                int argCount = ctor.getParameterCount();
                SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                int i = 0;
                while (true) {
                    if (i < argCount) {
                        AnnotatedParameter param = ctor.getParameter(i);
                        PropertyName name = _findParamName(param, intr);
                        if (name != null && !name.isEmpty()) {
                            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
                            i++;
                        }
                    } else if (found != null) {
                        found = null;
                        break;
                    } else {
                        found = ctor;
                        foundProps = properties;
                    }
                }
            }
        }
        if (found != null) {
            creators.addPropertyCreator(found, false, foundProps);
            BasicBeanDescription bbd = (BasicBeanDescription) beanDesc;
            for (SettableBeanProperty prop : foundProps) {
                PropertyName pn = prop.getFullName();
                if (!bbd.hasProperty(pn)) {
                    BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
                    bbd.addProperty(newDef);
                }
            }
        }
    }

    protected boolean _handleSingleArgumentCreator(CreatorCollector creators, AnnotatedWithParams ctor, boolean isCreator, boolean isVisible) {
        Class<?> type = ctor.getRawParameterType(0);
        if (type == String.class || type == CLASS_CHAR_SEQUENCE) {
            if (isCreator || isVisible) {
                creators.addStringCreator(ctor, isCreator);
                return true;
            }
            return true;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || isVisible) {
                creators.addIntCreator(ctor, isCreator);
                return true;
            }
            return true;
        }
        if (type == Long.TYPE || type == Long.class) {
            if (isCreator || isVisible) {
                creators.addLongCreator(ctor, isCreator);
                return true;
            }
            return true;
        }
        if (type == Double.TYPE || type == Double.class) {
            if (isCreator || isVisible) {
                creators.addDoubleCreator(ctor, isCreator);
                return true;
            }
            return true;
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            if (isCreator || isVisible) {
                creators.addBooleanCreator(ctor, isCreator);
                return true;
            }
            return true;
        }
        if (type == BigInteger.class && (isCreator || isVisible)) {
            creators.addBigIntegerCreator(ctor, isCreator);
        }
        if (type == BigDecimal.class && (isCreator || isVisible)) {
            creators.addBigDecimalCreator(ctor, isCreator);
        }
        if (isCreator) {
            creators.addDelegatingCreator(ctor, isCreator, null, 0);
            return true;
        }
        return false;
    }

    protected void _validateNamedPropertyParameter(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCandidate candidate, int paramIndex, PropertyName name, JacksonInject.Value injectId) throws JsonMappingException {
        if (name == null && injectId == null) {
            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name (and is not Injectable): can not use as property-based Creator", Integer.valueOf(paramIndex), candidate);
        }
    }

    protected void _reportUnwrappedCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedParameter param) throws JsonMappingException {
        ctxt.reportBadTypeDefinition(beanDesc, "Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", Integer.valueOf(param.getIndex()));
    }

    protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, JacksonInject.Value injectable) throws JsonMappingException {
        PropertyMetadata metadata;
        PropertyName wrapperName;
        DeserializationConfig config = ctxt.getConfig();
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
            wrapperName = null;
        } else {
            Boolean b = intr.hasRequiredMarker(param);
            String desc = intr.findPropertyDescription(param);
            Integer idx = intr.findPropertyIndex(param);
            String def = intr.findPropertyDefaultValue(param);
            metadata = PropertyMetadata.construct(b, desc, idx, def);
            wrapperName = intr.findWrapperName(param);
        }
        JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
        BeanProperty.Std property = new BeanProperty.Std(name, type, wrapperName, param, metadata);
        TypeDeserializer typeDeser = (TypeDeserializer) type.getTypeHandler();
        if (typeDeser == null) {
            typeDeser = findTypeDeserializer(config, type);
        }
        SettableBeanProperty prop = CreatorProperty.construct(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectable, _getSetterInfo(ctxt, property, metadata));
        JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        if (deser != null) {
            prop = prop.withValueDeserializer(ctxt.handlePrimaryContextualization(deser, prop, type));
        }
        return prop;
    }

    private PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
        if (intr != null) {
            PropertyName name = intr.findNameForDeserialization(param);
            if (name != null && !name.isEmpty()) {
                return name;
            }
            String str = intr.findImplicitPropertyName(param);
            if (str != null && !str.isEmpty()) {
                return PropertyName.construct(str);
            }
            return null;
        }
        return null;
    }

    protected PropertyMetadata _getSetterInfo(DeserializationContext ctxt, BeanProperty prop, PropertyMetadata metadata) {
        JsonSetter.Value setterInfo;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        DeserializationConfig config = ctxt.getConfig();
        Nulls valueNulls = null;
        Nulls contentNulls = null;
        AnnotatedMember prim = prop.getMember();
        if (prim != null) {
            if (intr != null && (setterInfo = intr.findSetterInfo(prim)) != null) {
                valueNulls = setterInfo.nonDefaultValueNulls();
                contentNulls = setterInfo.nonDefaultContentNulls();
            }
            if (1 != 0 || valueNulls == null || contentNulls == null) {
                ConfigOverride co = config.getConfigOverride(prop.getType().getRawClass());
                JsonSetter.Value setterInfo2 = co.getSetterInfo();
                if (setterInfo2 != null) {
                    if (valueNulls == null) {
                        valueNulls = setterInfo2.nonDefaultValueNulls();
                    }
                    if (contentNulls == null) {
                        contentNulls = setterInfo2.nonDefaultContentNulls();
                    }
                }
            }
        }
        if (1 != 0 || valueNulls == null || contentNulls == null) {
            JsonSetter.Value setterInfo3 = config.getDefaultSetterInfo();
            if (valueNulls == null) {
                valueNulls = setterInfo3.nonDefaultValueNulls();
            }
            if (contentNulls == null) {
                contentNulls = setterInfo3.nonDefaultContentNulls();
            }
        }
        if (valueNulls != null || contentNulls != null) {
            metadata = metadata.withNulls(valueNulls, contentNulls);
        }
        return metadata;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        JavaType elemType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) elemType.getValueHandler();
        TypeDeserializer elemTypeDeser = (TypeDeserializer) elemType.getTypeHandler();
        if (elemTypeDeser == null) {
            elemTypeDeser = findTypeDeserializer(config, elemType);
        }
        JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
        if (deser == null) {
            if (contentDeser == null) {
                Class<?> raw = elemType.getRawClass();
                if (elemType.isPrimitive()) {
                    return PrimitiveArrayDeserializers.forType(raw);
                }
                if (raw == String.class) {
                    return StringArrayDeserializer.instance;
                }
            }
            deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> collectionClass = type.getRawClass();
            if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
                deser = new EnumSetDeserializer(contentType, null);
            }
        }
        if (deser == null) {
            if (type.isInterface() || type.isAbstract()) {
                CollectionType implType = _mapAbstractCollectionType(type, config);
                if (implType == null) {
                    if (type.getTypeHandler() == null) {
                        throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type);
                    }
                    deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                } else {
                    type = implType;
                    beanDesc = config.introspectForCreation(type);
                }
            }
            if (deser == null) {
                ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
                if (!inst.canCreateUsingDefault()) {
                    if (type.hasRawClass(ArrayBlockingQueue.class)) {
                        return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
                    }
                    JsonDeserializer<?> deser2 = JavaUtilCollectionsDeserializers.findForCollection(ctxt, type);
                    if (deser2 != null) {
                        return deser2;
                    }
                }
                if (contentType.hasRawClass(String.class)) {
                    deser = new StringCollectionDeserializer(type, contentDeser, inst);
                } else {
                    deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
        Class<?> collectionClass = ContainerDefaultMappings.findCollectionFallback(type);
        if (collectionClass != null) {
            return (CollectionType) config.getTypeFactory().constructSpecializedType(type, collectionClass, true);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
        ValueInstantiator inst;
        DeserializationConfig config = ctxt.getConfig();
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer) keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> mapClass = type.getRawClass();
            if (EnumMap.class.isAssignableFrom(mapClass)) {
                if (mapClass == EnumMap.class) {
                    inst = null;
                } else {
                    inst = findValueInstantiator(ctxt, beanDesc);
                }
                if (!keyType.isEnumImplType()) {
                    throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
                }
                deser = new EnumMapDeserializer(type, inst, null, contentDeser, contentTypeDeser, null);
            }
            if (deser == null) {
                if (type.isInterface() || type.isAbstract()) {
                    MapType fallback = _mapAbstractMapType(type, config);
                    if (fallback != null) {
                        type = fallback;
                        type.getRawClass();
                        beanDesc = config.introspectForCreation(type);
                    } else {
                        if (type.getTypeHandler() == null) {
                            throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type);
                        }
                        deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                    }
                } else {
                    deser = JavaUtilCollectionsDeserializers.findForMap(ctxt, type);
                    if (deser != null) {
                        return deser;
                    }
                }
                if (deser == null) {
                    ValueInstantiator inst2 = findValueInstantiator(ctxt, beanDesc);
                    MapDeserializer md = new MapDeserializer(type, inst2, keyDes, contentDeser, contentTypeDeser);
                    JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
                    Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForDeserialization();
                    md.setIgnorableProperties(ignored);
                    JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(Map.class, beanDesc.getClassInfo());
                    Set<String> included = inclusions == null ? null : inclusions.getIncluded();
                    md.setIncludableProperties(included);
                    deser = md;
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    protected MapType _mapAbstractMapType(JavaType type, DeserializationConfig config) {
        Class<?> mapClass = ContainerDefaultMappings.findMapFallback(type);
        if (mapClass != null) {
            return (MapType) config.getTypeFactory().constructSpecializedType(type, mapClass, true);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        DeserializationConfig config = ctxt.getConfig();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer) keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        Class<?> enumClass = type.getRawClass();
        JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (deser == null) {
            if (enumClass == Enum.class) {
                return AbstractDeserializer.constructForNonPOJO(beanDesc);
            }
            ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
            SettableBeanProperty[] creatorProps = valueInstantiator == null ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            Iterator<AnnotatedMethod> it = beanDesc.getFactoryMethods().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                AnnotatedMethod factory = it.next();
                if (_hasCreatorAnnotation(ctxt, factory)) {
                    if (factory.getParameterCount() == 0) {
                        deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
                    } else {
                        Class<?> returnType = factory.getRawReturnType();
                        if (!returnType.isAssignableFrom(enumClass)) {
                            ctxt.reportBadDefinition(type, String.format("Invalid `@JsonCreator` annotated Enum factory method [%s]: needs to return compatible type", factory.toString()));
                        }
                        deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
                    }
                }
            }
            if (deser == null) {
                deser = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS), constructEnumNamingStrategyResolver(config, enumClass, beanDesc.getClassInfo()));
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
        Class<?> rawClass = nodeType.getRawClass();
        JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(rawClass, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        return JsonNodeDeserializer.getDeserializer(rawClass);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
        ValueInstantiator inst;
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null && type.isTypeOrSubTypeOf(AtomicReference.class)) {
            Class<?> rawType = type.getRawClass();
            if (rawType == AtomicReference.class) {
                inst = null;
            } else {
                inst = findValueInstantiator(ctxt, beanDesc);
            }
            return new AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
        JavaType defaultType;
        BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
        AnnotatedClass ac = bean.getClassInfo();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        if (b == null) {
            b = config.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
        if (b.getDefaultImpl() == null && baseType.isAbstract() && (defaultType = mapAbstractType(config, baseType)) != null && !defaultType.hasRawClass(baseType.getRawClass())) {
            b = b.withDefaultImpl(defaultType.getRawClass());
        }
        try {
            return b.buildTypeDeserializer(config, baseType, subtypes);
        } catch (IllegalArgumentException | IllegalStateException e0) {
            throw InvalidDefinitionException.from((JsonParser) null, ClassUtil.exceptionMessage(e0), baseType).withCause(e0);
        }
    }

    protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        BeanDescription beanDesc = null;
        KeyDeserializer deser = null;
        if (this._factoryConfig.hasKeyDeserializers()) {
            beanDesc = config.introspectClassAnnotations(type);
            for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
                deser = d.findKeyDeserializer(type, config, beanDesc);
                if (deser != null) {
                    break;
                }
            }
        }
        if (deser == null) {
            if (beanDesc == null) {
                beanDesc = config.introspectClassAnnotations(type.getRawClass());
            }
            deser = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
            if (deser == null) {
                if (type.isEnumType()) {
                    deser = _createEnumKeyDeserializer(ctxt, type);
                } else {
                    deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
                }
            }
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyKeyDeserializer(config, type, deser);
            }
        }
        return deser;
    }

    private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        Class<?> enumClass = type.getRawClass();
        BeanDescription beanDesc = config.introspect(type);
        KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (des != null) {
            return des;
        }
        JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
        }
        JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (valueDesForKey != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
        }
        EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc);
        EnumResolver byEnumNamingResolver = constructEnumNamingStrategyResolver(config, enumClass, beanDesc.getClassInfo());
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            if (_hasCreatorAnnotation(ctxt, factory)) {
                int argCount = factory.getParameterCount();
                if (argCount == 1) {
                    Class<?> returnType = factory.getRawReturnType();
                    if (returnType.isAssignableFrom(enumClass)) {
                        if (factory.getRawParameterType(0) == String.class) {
                            if (config.canOverrideAccessModifiers()) {
                                ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                            }
                            return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory, byEnumNamingResolver);
                        }
                    }
                }
                throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
            }
        }
        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, byEnumNamingResolver);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public boolean hasExplicitDeserializerFor(DeserializationConfig config, Class<?> valueType) {
        while (valueType.isArray()) {
            valueType = valueType.getComponentType();
        }
        if (Enum.class.isAssignableFrom(valueType)) {
            return true;
        }
        String clsName = valueType.getName();
        if (clsName.startsWith("java.")) {
            if (Collection.class.isAssignableFrom(valueType) || Map.class.isAssignableFrom(valueType)) {
                return true;
            }
            if (Number.class.isAssignableFrom(valueType)) {
                return NumberDeserializers.find(valueType, clsName) != null;
            }
            if (JdkDeserializers.hasDeserializerFor(valueType) || valueType == CLASS_STRING || valueType == Boolean.class || valueType == EnumMap.class || valueType == AtomicReference.class || DateDeserializers.hasDeserializerFor(valueType)) {
                return true;
            }
            return false;
        }
        if (clsName.startsWith("com.fasterxml.")) {
            return JsonNode.class.isAssignableFrom(valueType) || valueType == TokenBuffer.class;
        }
        return OptionalHandlerFactory.instance.hasDeserializerFor(valueType);
    }

    public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
        if (b == null) {
            return findTypeDeserializer(config, baseType);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
        try {
            return b.buildTypeDeserializer(config, baseType, subtypes);
        } catch (IllegalArgumentException | IllegalStateException e0) {
            throw InvalidDefinitionException.from((JsonParser) null, ClassUtil.exceptionMessage(e0), baseType).withCause(e0);
        }
    }

    public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
        JavaType contentType = containerType.getContentType();
        if (b == null) {
            return findTypeDeserializer(config, contentType);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
        return b.buildTypeDeserializer(config, contentType, subtypes);
    }

    public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType mt;
        JavaType lt;
        Class<?> rawType = type.getRawClass();
        if (rawType == CLASS_OBJECT || rawType == CLASS_SERIALIZABLE) {
            DeserializationConfig config = ctxt.getConfig();
            if (this._factoryConfig.hasAbstractTypeResolvers()) {
                lt = _findRemappedType(config, List.class);
                mt = _findRemappedType(config, Map.class);
            } else {
                mt = null;
                lt = null;
            }
            return new UntypedObjectDeserializer(lt, mt);
        }
        if (rawType == CLASS_STRING || rawType == CLASS_CHAR_SEQUENCE) {
            return StringDeserializer.instance;
        }
        if (rawType == CLASS_ITERABLE) {
            TypeFactory tf = ctxt.getTypeFactory();
            JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
            JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
            CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
            return createCollectionDeserializer(ctxt, ct, beanDesc);
        }
        if (rawType == CLASS_MAP_ENTRY) {
            JavaType kt = type.containedTypeOrUnknown(0);
            JavaType vt = type.containedTypeOrUnknown(1);
            TypeDeserializer vts = (TypeDeserializer) vt.getTypeHandler();
            if (vts == null) {
                vts = findTypeDeserializer(ctxt.getConfig(), vt);
            }
            JsonDeserializer<Object> valueDeser = (JsonDeserializer) vt.getValueHandler();
            KeyDeserializer keyDes = (KeyDeserializer) kt.getValueHandler();
            return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
        }
        String clsName = rawType.getName();
        if (rawType.isPrimitive() || clsName.startsWith("java.")) {
            JsonDeserializer<?> deser = NumberDeserializers.find(rawType, clsName);
            if (deser == null) {
                deser = DateDeserializers.find(rawType, clsName);
            }
            if (deser != null) {
                return deser;
            }
        }
        if (rawType == TokenBuffer.class) {
            return new TokenBufferDeserializer();
        }
        JsonDeserializer<?> deser2 = findOptionalStdDeserializer(ctxt, type, beanDesc);
        if (deser2 != null) {
            return deser2;
        }
        return JdkDeserializers.find(ctxt, rawType, clsName);
    }

    protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
        JavaType type = mapAbstractType(config, config.constructType(rawType));
        if (type == null || type.hasRawClass(rawType)) {
            return null;
        }
        return type;
    }

    protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && (deserDef = intr.findDeserializer(ann)) != null) {
            return ctxt.deserializerInstance(ann, deserDef);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && (deserDef = intr.findKeyDeserializer(ann)) != null) {
            return ctxt.keyDeserializerInstance(ann, deserDef);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<Object> findContentDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && (deserDef = intr.findContentDeserializer(ann)) != null) {
            return ctxt.deserializerInstance(ann, deserDef);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null) {
                Object kdDef = intr.findKeyDeserializer(member);
                KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
                if (kd != null) {
                    type = ((MapLikeType) type).withKeyValueHandler(kd);
                    type.getKeyType();
                }
            }
        }
        if (type.hasContentType()) {
            Object cdDef = intr.findContentDeserializer(member);
            JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
            if (cd != null) {
                type = type.withContentValueHandler(cd);
            }
            TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
            if (contentTypeDeser != null) {
                type = type.withContentTypeHandler(contentTypeDeser);
            }
        }
        TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
        if (valueTypeDeser != null) {
            type = type.withTypeHandler(valueTypeDeser);
        }
        return intr.refineDeserializationType(ctxt.getConfig(), member, type);
    }

    protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, BeanDescription beanDesc) {
        AnnotatedMember jvAcc = beanDesc.findJsonValueAccessor();
        if (jvAcc != null) {
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(jvAcc.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return EnumResolver.constructUsingMethod(config, enumClass, jvAcc);
        }
        return EnumResolver.constructFor(config, enumClass);
    }

    protected EnumResolver constructEnumNamingStrategyResolver(DeserializationConfig config, Class<?> enumClass, AnnotatedClass annotatedClass) {
        Object namingDef = config.getAnnotationIntrospector().findEnumNamingStrategy(config, annotatedClass);
        EnumNamingStrategy enumNamingStrategy = EnumNamingStrategyFactory.createEnumNamingStrategyInstance(namingDef, config.canOverrideAccessModifiers());
        if (enumNamingStrategy == null) {
            return null;
        }
        return EnumResolver.constructUsingEnumNamingStrategy(config, enumClass, enumNamingStrategy);
    }

    protected boolean _hasCreatorAnnotation(DeserializationContext ctxt, Annotated ann) {
        JsonCreator.Mode mode;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        return (intr == null || (mode = intr.findCreatorAnnotation(ctxt.getConfig(), ann)) == null || mode == JsonCreator.Mode.DISABLED) ? false : true;
    }

    @Deprecated
    protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        return intr.refineDeserializationType(ctxt.getConfig(), a, type);
    }

    @Deprecated
    protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
        return resolveMemberAndTypeAnnotations(ctxt, member, type);
    }

    @Deprecated
    protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
        if (enumType == null) {
            return null;
        }
        BeanDescription beanDesc = config.introspect(enumType);
        return beanDesc.findJsonValueMethod();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BasicDeserializerFactory$ContainerDefaultMappings.class */
    public static class ContainerDefaultMappings {
        static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
        static final HashMap<String, Class<? extends Map>> _mapFallbacks;

        protected ContainerDefaultMappings() {
        }

        static {
            HashMap<String, Class<? extends Collection>> fallbacks = new HashMap<>();
            fallbacks.put(Collection.class.getName(), ArrayList.class);
            fallbacks.put(List.class.getName(), ArrayList.class);
            fallbacks.put(Set.class.getName(), HashSet.class);
            fallbacks.put(SortedSet.class.getName(), TreeSet.class);
            fallbacks.put(Queue.class.getName(), LinkedList.class);
            fallbacks.put(AbstractList.class.getName(), ArrayList.class);
            fallbacks.put(AbstractSet.class.getName(), HashSet.class);
            fallbacks.put(Deque.class.getName(), LinkedList.class);
            fallbacks.put(NavigableSet.class.getName(), TreeSet.class);
            _collectionFallbacks = fallbacks;
            HashMap<String, Class<? extends Map>> fallbacks2 = new HashMap<>();
            fallbacks2.put(Map.class.getName(), LinkedHashMap.class);
            fallbacks2.put(AbstractMap.class.getName(), LinkedHashMap.class);
            fallbacks2.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
            fallbacks2.put(SortedMap.class.getName(), TreeMap.class);
            fallbacks2.put(NavigableMap.class.getName(), TreeMap.class);
            fallbacks2.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
            _mapFallbacks = fallbacks2;
        }

        public static Class<?> findCollectionFallback(JavaType type) {
            return _collectionFallbacks.get(type.getRawClass().getName());
        }

        public static Class<?> findMapFallback(JavaType type) {
            return _mapFallbacks.get(type.getRawClass().getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BasicDeserializerFactory$CreatorCollectionState.class */
    public static class CreatorCollectionState {
        public final DeserializationContext context;
        public final BeanDescription beanDesc;
        public final VisibilityChecker<?> vchecker;
        public final CreatorCollector creators;
        public final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams;
        private List<CreatorCandidate> _implicitFactoryCandidates;
        private int _explicitFactoryCount;
        private List<CreatorCandidate> _implicitConstructorCandidates;
        private int _explicitConstructorCount;

        public CreatorCollectionState(DeserializationContext ctxt, BeanDescription bd, VisibilityChecker<?> vc, CreatorCollector cc, Map<AnnotatedWithParams, BeanPropertyDefinition[]> cp) {
            this.context = ctxt;
            this.beanDesc = bd;
            this.vchecker = vc;
            this.creators = cc;
            this.creatorParams = cp;
        }

        public AnnotationIntrospector annotationIntrospector() {
            return this.context.getAnnotationIntrospector();
        }

        public void addImplicitFactoryCandidate(CreatorCandidate cc) {
            if (this._implicitFactoryCandidates == null) {
                this._implicitFactoryCandidates = new LinkedList();
            }
            this._implicitFactoryCandidates.add(cc);
        }

        public void increaseExplicitFactoryCount() {
            this._explicitFactoryCount++;
        }

        public boolean hasExplicitFactories() {
            return this._explicitFactoryCount > 0;
        }

        public boolean hasImplicitFactoryCandidates() {
            return this._implicitFactoryCandidates != null;
        }

        public List<CreatorCandidate> implicitFactoryCandidates() {
            return this._implicitFactoryCandidates;
        }

        public void addImplicitConstructorCandidate(CreatorCandidate cc) {
            if (this._implicitConstructorCandidates == null) {
                this._implicitConstructorCandidates = new LinkedList();
            }
            this._implicitConstructorCandidates.add(cc);
        }

        public void increaseExplicitConstructorCount() {
            this._explicitConstructorCount++;
        }

        public boolean hasExplicitConstructors() {
            return this._explicitConstructorCount > 0;
        }

        public boolean hasImplicitConstructorCandidates() {
            return this._implicitConstructorCandidates != null;
        }

        public List<CreatorCandidate> implicitConstructorCandidates() {
            return this._implicitConstructorCandidates;
        }
    }
}
