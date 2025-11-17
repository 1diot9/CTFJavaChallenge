package com.fasterxml.jackson.databind.deser;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.impl.ErrorThrowingDeserializer;
import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
import com.fasterxml.jackson.databind.deser.impl.MethodProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import com.fasterxml.jackson.databind.deser.impl.UnsupportedTypeDeserializer;
import com.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.SubTypeValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BeanDeserializerFactory.class */
public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable {
    private static final long serialVersionUID = 1;
    private static final Class<?>[] INIT_CAUSE_PARAMS = {Throwable.class};
    public static final BeanDeserializerFactory instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());

    public BeanDeserializerFactory(DeserializerFactoryConfig config) {
        super(config);
    }

    @Override // com.fasterxml.jackson.databind.deser.BasicDeserializerFactory
    public DeserializerFactory withConfig(DeserializerFactoryConfig config) {
        if (this._factoryConfig == config) {
            return this;
        }
        ClassUtil.verifyMustOverride(BeanDeserializerFactory.class, this, "withConfig");
        return new BeanDeserializerFactory(config);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType concreteType;
        DeserializationConfig config = ctxt.getConfig();
        JsonDeserializer<?> deser = _findCustomBeanDeserializer(type, config, beanDesc);
        if (deser != null) {
            if (this._factoryConfig.hasDeserializerModifiers()) {
                for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                    deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
                }
            }
            return deser;
        }
        if (type.isThrowable()) {
            return buildThrowableDeserializer(ctxt, type, beanDesc);
        }
        if (type.isAbstract() && !type.isPrimitive() && !type.isEnumType() && (concreteType = materializeAbstractType(ctxt, type, beanDesc)) != null) {
            return buildBeanDeserializer(ctxt, concreteType, config.introspect(concreteType));
        }
        JsonDeserializer<?> deser2 = findStdDeserializer(ctxt, type, beanDesc);
        if (deser2 != null) {
            return deser2;
        }
        if (!isPotentialBeanType(type.getRawClass())) {
            return null;
        }
        _validateSubType(ctxt, type, beanDesc);
        JsonDeserializer<?> deser3 = _findUnsupportedTypeDeserializer(ctxt, type, beanDesc);
        if (deser3 != null) {
            return deser3;
        }
        return buildBeanDeserializer(ctxt, type, beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription valueBeanDesc, Class<?> builderClass) throws JsonMappingException {
        JavaType builderType;
        if (ctxt.isEnabled(MapperFeature.INFER_BUILDER_TYPE_BINDINGS)) {
            builderType = ctxt.getTypeFactory().constructParametricType(builderClass, valueType.getBindings());
        } else {
            builderType = ctxt.constructType(builderClass);
        }
        BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType, valueBeanDesc);
        return buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
    }

    protected JsonDeserializer<?> findStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> deser = findDefaultDeserializer(ctxt, type, beanDesc);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
            }
        }
        return deser;
    }

    protected JsonDeserializer<Object> _findUnsupportedTypeDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        String errorMsg = BeanUtil.checkUnsupportedType(type);
        if (errorMsg != null && ctxt.getConfig().findMixInClassFor(type.getRawClass()) == null) {
            return new UnsupportedTypeDeserializer(type, errorMsg);
        }
        return null;
    }

    protected JavaType materializeAbstractType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        for (AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
            JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
            if (concrete != null) {
                return concrete;
            }
        }
        return null;
    }

    public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> deserializer;
        try {
            ValueInstantiator valueInstantiator = findValueInstantiator(ctxt, beanDesc);
            BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
            builder.setValueInstantiator(valueInstantiator);
            addBeanProps(ctxt, beanDesc, builder);
            addObjectIdReader(ctxt, beanDesc, builder);
            addBackReferenceProperties(ctxt, beanDesc, builder);
            addInjectables(ctxt, beanDesc, builder);
            DeserializationConfig config = ctxt.getConfig();
            if (this._factoryConfig.hasDeserializerModifiers()) {
                for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                    builder = mod.updateBuilder(config, beanDesc, builder);
                }
            }
            if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
                deserializer = builder.buildAbstract();
            } else {
                deserializer = builder.build();
            }
            if (this._factoryConfig.hasDeserializerModifiers()) {
                for (BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                    deserializer = mod2.modifyDeserializer(config, beanDesc, deserializer);
                }
            }
            return deserializer;
        } catch (IllegalArgumentException e0) {
            throw InvalidDefinitionException.from(ctxt.getParser(), ClassUtil.exceptionMessage(e0), beanDesc, (BeanPropertyDefinition) null).withCause(e0);
        } catch (NoClassDefFoundError error) {
            return new ErrorThrowingDeserializer(error);
        }
    }

    protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription builderDesc) throws JsonMappingException {
        try {
            ValueInstantiator valueInstantiator = findValueInstantiator(ctxt, builderDesc);
            DeserializationConfig config = ctxt.getConfig();
            BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, builderDesc);
            builder.setValueInstantiator(valueInstantiator);
            addBeanProps(ctxt, builderDesc, builder);
            addObjectIdReader(ctxt, builderDesc, builder);
            addBackReferenceProperties(ctxt, builderDesc, builder);
            addInjectables(ctxt, builderDesc, builder);
            JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
            String buildMethodName = builderConfig == null ? JsonPOJOBuilder.DEFAULT_BUILD_METHOD : builderConfig.buildMethodName;
            AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, null);
            if (buildMethod != null && config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            builder.setPOJOBuilder(buildMethod, builderConfig);
            if (this._factoryConfig.hasDeserializerModifiers()) {
                for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                    builder = mod.updateBuilder(config, builderDesc, builder);
                }
            }
            JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
            if (this._factoryConfig.hasDeserializerModifiers()) {
                for (BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                    deserializer = mod2.modifyDeserializer(config, builderDesc, deserializer);
                }
            }
            return deserializer;
        } catch (IllegalArgumentException e) {
            throw InvalidDefinitionException.from(ctxt.getParser(), ClassUtil.exceptionMessage(e), builderDesc, (BeanPropertyDefinition) null);
        } catch (NoClassDefFoundError error) {
            return new ErrorThrowingDeserializer(error);
        }
    }

    protected void addObjectIdReader(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        JavaType idType;
        SettableBeanProperty idProp;
        ObjectIdGenerator<?> gen;
        ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo == null) {
            return;
        }
        Class<?> implClass = objectIdInfo.getGeneratorType();
        ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
        if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            PropertyName propName = objectIdInfo.getPropertyName();
            idProp = builder.findProperty(propName);
            if (idProp == null) {
                throw new IllegalArgumentException(String.format("Invalid Object Id definition for %s: cannot find property with name %s", ClassUtil.getTypeDescription(beanDesc.getType()), ClassUtil.name(propName)));
            }
            idType = idProp.getType();
            gen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
        } else {
            JavaType type = ctxt.constructType(implClass);
            idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
            idProp = null;
            gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
        }
        JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
        builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), gen, deser, idProp, resolver));
    }

    public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
        builder.setValueInstantiator(findValueInstantiator(ctxt, beanDesc));
        addBeanProps(ctxt, beanDesc, builder);
        Iterator<SettableBeanProperty> it = builder.getProperties();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            if ("setCause".equals(it.next().getMember().getName())) {
                it.remove();
                break;
            }
        }
        AnnotatedMethod am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
        if (am != null) {
            String name = JsonEncoder.CAUSE_ATTR_NAME;
            PropertyNamingStrategy pts = config.getPropertyNamingStrategy();
            if (pts != null) {
                name = pts.nameForSetterMethod(config, am, JsonEncoder.CAUSE_ATTR_NAME);
            }
            SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, new PropertyName(name));
            SettableBeanProperty prop = constructSettableProperty(ctxt, beanDesc, propDef, am.getParameterType(0));
            if (prop != null) {
                builder.addOrReplaceProperty(prop, true);
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer = builder.build();
        if (deserializer instanceof BeanDeserializer) {
            deserializer = ThrowableDeserializer.construct(ctxt, (BeanDeserializer) deserializer);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, beanDesc, deserializer);
            }
        }
        return deserializer;
    }

    protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext ctxt, BeanDescription beanDesc) {
        return new BeanDeserializerBuilder(beanDesc, ctxt);
    }

    protected void addBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        Set<String> ignored;
        boolean isConcrete = !beanDesc.getType().isAbstract();
        SettableBeanProperty[] creatorProps = isConcrete ? builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig()) : null;
        boolean hasCreatorProps = creatorProps != null;
        JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        if (ignorals != null) {
            boolean ignoreAny = ignorals.getIgnoreUnknown();
            builder.setIgnoreUnknownProperties(ignoreAny);
            ignored = ignorals.findIgnoredForDeserialization();
            for (String propName : ignored) {
                builder.addIgnorable(propName);
            }
        } else {
            ignored = Collections.emptySet();
        }
        JsonIncludeProperties.Value inclusions = ctxt.getConfig().getDefaultPropertyInclusions(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        Set<String> included = null;
        if (inclusions != null) {
            included = inclusions.getIncluded();
            if (included != null) {
                for (String propName2 : included) {
                    builder.addIncludable(propName2);
                }
            }
        }
        AnnotatedMember anySetter = beanDesc.findAnySetterAccessor();
        if (anySetter != null) {
            builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetter));
        } else {
            Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
            if (ignored2 != null) {
                for (String propName3 : ignored2) {
                    builder.addIgnorable(propName3);
                }
            }
        }
        boolean useGettersAsSetters = ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS);
        List<BeanPropertyDefinition> propDefs = filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored, included);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
            }
        }
        for (BeanPropertyDefinition propDef : propDefs) {
            SettableBeanProperty prop = null;
            if (propDef.hasSetter()) {
                AnnotatedMethod setter = propDef.getSetter();
                JavaType propertyType = setter.getParameterType(0);
                prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
            } else if (propDef.hasField()) {
                AnnotatedField field = propDef.getField();
                JavaType propertyType2 = field.getType();
                prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType2);
            } else {
                AnnotatedMethod getter = propDef.getGetter();
                if (getter != null) {
                    if (useGettersAsSetters && _isSetterlessType(getter.getRawType())) {
                        if (!builder.hasIgnorable(propDef.getName())) {
                            prop = constructSetterlessProperty(ctxt, beanDesc, propDef);
                        }
                    } else if (!propDef.hasConstructorParameter()) {
                        PropertyMetadata md = propDef.getMetadata();
                        if (md.getMergeInfo() != null) {
                            prop = constructSetterlessProperty(ctxt, beanDesc, propDef);
                        }
                    }
                }
            }
            if (hasCreatorProps && propDef.hasConstructorParameter()) {
                String name = propDef.getName();
                CreatorProperty cprop = null;
                int length = creatorProps.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    SettableBeanProperty cp = creatorProps[i];
                    if (!name.equals(cp.getName()) || !(cp instanceof CreatorProperty)) {
                        i++;
                    } else {
                        cprop = (CreatorProperty) cp;
                        break;
                    }
                }
                if (cprop == null) {
                    List<String> n = new ArrayList<>();
                    for (SettableBeanProperty settableBeanProperty : creatorProps) {
                        n.add(settableBeanProperty.getName());
                    }
                    ctxt.reportBadPropertyDefinition(beanDesc, propDef, "Could not find creator property with name %s (known Creator properties: %s)", ClassUtil.name(name), n);
                } else {
                    if (prop != null) {
                        cprop.setFallbackSetter(prop);
                    }
                    Class<?>[] views = propDef.findViews();
                    if (views == null) {
                        views = beanDesc.findDefaultViews();
                    }
                    cprop.setViews(views);
                    builder.addCreatorProperty(cprop);
                }
            } else if (prop != null) {
                Class<?>[] views2 = propDef.findViews();
                if (views2 == null) {
                    views2 = beanDesc.findDefaultViews();
                }
                prop.setViews(views2);
                builder.addProperty(prop);
            }
        }
    }

    private boolean _isSetterlessType(Class<?> rawType) {
        return Collection.class.isAssignableFrom(rawType) || Map.class.isAssignableFrom(rawType);
    }

    @Deprecated
    protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored) throws JsonMappingException {
        return filterBeanProps(ctxt, beanDesc, builder, propDefsIn, ignored, null);
    }

    protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored, Set<String> included) {
        Class<?> rawPropertyType;
        ArrayList<BeanPropertyDefinition> result = new ArrayList<>(Math.max(4, propDefsIn.size()));
        HashMap<Class<?>, Boolean> ignoredTypes = new HashMap<>();
        for (BeanPropertyDefinition property : propDefsIn) {
            String name = property.getName();
            if (!IgnorePropertiesUtil.shouldIgnore(name, ignored, included)) {
                if (!property.hasConstructorParameter() && (rawPropertyType = property.getRawPrimaryType()) != null && isIgnorableType(ctxt.getConfig(), property, rawPropertyType, ignoredTypes)) {
                    builder.addIgnorable(name);
                } else {
                    result.add(property);
                }
            }
        }
        return result;
    }

    protected void addBackReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        List<BeanPropertyDefinition> refProps = beanDesc.findBackReferences();
        if (refProps != null) {
            for (BeanPropertyDefinition refProp : refProps) {
                String refName = refProp.findReferenceName();
                builder.addBackReferenceProperty(refName, constructSettableProperty(ctxt, beanDesc, refProp, refProp.getPrimaryType()));
            }
        }
    }

    @Deprecated
    protected void addReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        addBackReferenceProperties(ctxt, beanDesc, builder);
    }

    protected void addInjectables(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
        if (raw != null) {
            for (Map.Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
                AnnotatedMember m = entry.getValue();
                builder.addInjectable(PropertyName.construct(m.getName()), m.getType(), beanDesc.getClassAnnotations(), m, entry.getKey());
            }
        }
    }

    protected SettableAnyProperty constructAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedMember mutator) throws JsonMappingException {
        JavaType keyType;
        JavaType valueType;
        BeanProperty prop;
        boolean isField = mutator instanceof AnnotatedField;
        if (mutator instanceof AnnotatedMethod) {
            AnnotatedMethod am = (AnnotatedMethod) mutator;
            keyType = am.getParameterType(0);
            valueType = resolveMemberAndTypeAnnotations(ctxt, mutator, am.getParameterType(1));
            prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), valueType, null, mutator, PropertyMetadata.STD_OPTIONAL);
        } else if (isField) {
            AnnotatedField af = (AnnotatedField) mutator;
            JavaType fieldType = af.getType();
            if (fieldType.isMapLikeType()) {
                JavaType fieldType2 = resolveMemberAndTypeAnnotations(ctxt, mutator, fieldType);
                keyType = fieldType2.getKeyType();
                valueType = fieldType2.getContentType();
                prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), fieldType2, null, mutator, PropertyMetadata.STD_OPTIONAL);
            } else {
                if (fieldType.hasRawClass(JsonNode.class) || fieldType.hasRawClass(ObjectNode.class)) {
                    JavaType fieldType3 = resolveMemberAndTypeAnnotations(ctxt, mutator, fieldType);
                    JavaType valueType2 = ctxt.constructType(JsonNode.class);
                    BeanProperty prop2 = new BeanProperty.Std(PropertyName.construct(mutator.getName()), fieldType3, null, mutator, PropertyMetadata.STD_OPTIONAL);
                    return SettableAnyProperty.constructForJsonNodeField(ctxt, prop2, mutator, valueType2, ctxt.findRootValueDeserializer(valueType2));
                }
                return (SettableAnyProperty) ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unsupported type for any-setter: %s -- only support `Map`s, `JsonNode` and `ObjectNode` ", ClassUtil.getTypeDescription(fieldType)));
            }
        } else {
            return (SettableAnyProperty) ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unrecognized mutator type for any-setter: %s", ClassUtil.nameOf(mutator.getClass())));
        }
        KeyDeserializer keyDeser = findKeyDeserializerFromAnnotation(ctxt, mutator);
        if (keyDeser == null) {
            keyDeser = (KeyDeserializer) keyType.getValueHandler();
        }
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(keyType, prop);
        } else if (keyDeser instanceof ContextualKeyDeserializer) {
            keyDeser = ((ContextualKeyDeserializer) keyDeser).createContextual(ctxt, prop);
        }
        JsonDeserializer<Object> deser = findContentDeserializerFromAnnotation(ctxt, mutator);
        if (deser == null) {
            deser = (JsonDeserializer) valueType.getValueHandler();
        }
        if (deser != null) {
            deser = ctxt.handlePrimaryContextualization(deser, prop, valueType);
        }
        TypeDeserializer typeDeser = (TypeDeserializer) valueType.getTypeHandler();
        if (isField) {
            return SettableAnyProperty.constructForMapField(ctxt, prop, mutator, valueType, keyDeser, deser, typeDeser);
        }
        return SettableAnyProperty.constructForMethod(ctxt, prop, mutator, valueType, keyDeser, deser, typeDeser);
    }

    protected SettableBeanProperty constructSettableProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef, JavaType propType0) throws JsonMappingException {
        SettableBeanProperty prop;
        AnnotatedMember mutator = propDef.getNonConstructorMutator();
        if (mutator == null) {
            ctxt.reportBadPropertyDefinition(beanDesc, propDef, "No non-constructor mutator available", new Object[0]);
        }
        JavaType type = resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
        TypeDeserializer typeDeser = (TypeDeserializer) type.getTypeHandler();
        if (mutator instanceof AnnotatedMethod) {
            prop = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod) mutator);
        } else {
            prop = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField) mutator);
        }
        JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, mutator);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        if (deser != null) {
            prop = prop.withValueDeserializer(ctxt.handlePrimaryContextualization(deser, prop, type));
        }
        AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
        if (ref != null && ref.isManagedReference()) {
            prop.setManagedReferenceName(ref.getName());
        }
        ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
        if (objectIdInfo != null) {
            prop.setObjectIdInfo(objectIdInfo);
        }
        return prop;
    }

    protected SettableBeanProperty constructSetterlessProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef) throws JsonMappingException {
        AnnotatedMethod getter = propDef.getGetter();
        JavaType type = resolveMemberAndTypeAnnotations(ctxt, getter, getter.getType());
        TypeDeserializer typeDeser = (TypeDeserializer) type.getTypeHandler();
        SettableBeanProperty prop = new SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
        JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, getter);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        if (deser != null) {
            prop = prop.withValueDeserializer(ctxt.handlePrimaryContextualization(deser, prop, type));
        }
        return prop;
    }

    protected boolean isPotentialBeanType(Class<?> type) {
        String typeStr = ClassUtil.canBeABeanType(type);
        if (typeStr != null) {
            throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
        if (ClassUtil.isProxyType(type)) {
            throw new IllegalArgumentException("Cannot deserialize Proxy class " + type.getName() + " as a Bean");
        }
        String typeStr2 = ClassUtil.isLocalType(type, true);
        if (typeStr2 != null) {
            throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr2 + ") as a Bean");
        }
        return true;
    }

    protected boolean isIgnorableType(DeserializationConfig config, BeanPropertyDefinition propDef, Class<?> type, Map<Class<?>, Boolean> ignoredTypes) {
        Boolean status;
        Boolean status2 = ignoredTypes.get(type);
        if (status2 != null) {
            return status2.booleanValue();
        }
        if (type == String.class || type.isPrimitive()) {
            status = Boolean.FALSE;
        } else {
            status = config.getConfigOverride(type).getIsIgnoredType();
            if (status == null) {
                BeanDescription desc = config.introspectClassAnnotations(type);
                status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
                if (status == null) {
                    status = Boolean.FALSE;
                }
            }
        }
        ignoredTypes.put(type, status);
        return status.booleanValue();
    }

    protected void _validateSubType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        SubTypeValidator.instance().validateSubType(ctxt, type, beanDesc);
    }
}
