package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/cfg/MapperConfig.class */
public abstract class MapperConfig<T extends MapperConfig<T>> implements ClassIntrospector.MixInResolver, Serializable {
    private static final long serialVersionUID = 2;
    protected static final JsonInclude.Value EMPTY_INCLUDE = JsonInclude.Value.empty();
    protected static final JsonFormat.Value EMPTY_FORMAT = JsonFormat.Value.empty();
    protected final long _mapperFeatures;
    protected final BaseSettings _base;

    public abstract T with(MapperFeature... mapperFeatureArr);

    public abstract T without(MapperFeature... mapperFeatureArr);

    public abstract T with(MapperFeature mapperFeature, boolean z);

    public abstract boolean isEnabled(DatatypeFeature datatypeFeature);

    public abstract DatatypeFeatures getDatatypeFeatures();

    public abstract boolean useRootWrapping();

    public abstract SubtypeResolver getSubtypeResolver();

    public abstract ConfigOverride findConfigOverride(Class<?> cls);

    public abstract ConfigOverride getConfigOverride(Class<?> cls);

    public abstract JsonInclude.Value getDefaultPropertyInclusion();

    public abstract JsonInclude.Value getDefaultPropertyInclusion(Class<?> cls);

    public abstract JsonInclude.Value getDefaultInclusion(Class<?> cls, Class<?> cls2);

    public abstract JsonFormat.Value getDefaultPropertyFormat(Class<?> cls);

    public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> cls);

    public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> cls, AnnotatedClass annotatedClass);

    public abstract JsonIncludeProperties.Value getDefaultPropertyInclusions(Class<?> cls, AnnotatedClass annotatedClass);

    public abstract VisibilityChecker<?> getDefaultVisibilityChecker();

    public abstract VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> cls, AnnotatedClass annotatedClass);

    public abstract JsonSetter.Value getDefaultSetterInfo();

    public abstract Boolean getDefaultMergeable();

    public abstract Boolean getDefaultMergeable(Class<?> cls);

    public abstract Class<?> getActiveView();

    public abstract ContextAttributes getAttributes();

    public abstract PropertyName findRootName(JavaType javaType);

    public abstract PropertyName findRootName(Class<?> cls);

    /* JADX INFO: Access modifiers changed from: protected */
    public MapperConfig(BaseSettings base, long mapperFeatures) {
        this._base = base;
        this._mapperFeatures = mapperFeatures;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MapperConfig(MapperConfig<T> src, long mapperFeatures) {
        this._base = src._base;
        this._mapperFeatures = mapperFeatures;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MapperConfig(MapperConfig<T> src, BaseSettings base) {
        this._base = base;
        this._mapperFeatures = src._mapperFeatures;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MapperConfig(MapperConfig<T> src) {
        this._base = src._base;
        this._mapperFeatures = src._mapperFeatures;
    }

    public static <F extends Enum<F> & ConfigFeature> int collectFeatureDefaults(Class<F> cls) {
        int i = 0;
        for (Object obj : (Enum[]) cls.getEnumConstants()) {
            if (((ConfigFeature) obj).enabledByDefault()) {
                i |= ((ConfigFeature) obj).getMask();
            }
        }
        return i;
    }

    public final boolean isEnabled(MapperFeature f) {
        return f.enabledIn(this._mapperFeatures);
    }

    @Deprecated
    public final boolean hasMapperFeatures(int featureMask) {
        return (this._mapperFeatures & ((long) featureMask)) == ((long) featureMask);
    }

    public final boolean isAnnotationProcessingEnabled() {
        return isEnabled(MapperFeature.USE_ANNOTATIONS);
    }

    public final boolean canOverrideAccessModifiers() {
        return isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
    }

    public final boolean shouldSortPropertiesAlphabetically() {
        return isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    public SerializableString compileString(String src) {
        return new SerializedString(src);
    }

    public ClassIntrospector getClassIntrospector() {
        return this._base.getClassIntrospector();
    }

    public AnnotationIntrospector getAnnotationIntrospector() {
        if (isEnabled(MapperFeature.USE_ANNOTATIONS)) {
            return this._base.getAnnotationIntrospector();
        }
        return NopAnnotationIntrospector.instance;
    }

    public final PropertyNamingStrategy getPropertyNamingStrategy() {
        return this._base.getPropertyNamingStrategy();
    }

    public final AccessorNamingStrategy.Provider getAccessorNaming() {
        return this._base.getAccessorNaming();
    }

    public final HandlerInstantiator getHandlerInstantiator() {
        return this._base.getHandlerInstantiator();
    }

    public final TypeResolverBuilder<?> getDefaultTyper(JavaType baseType) {
        return this._base.getTypeResolverBuilder();
    }

    public PolymorphicTypeValidator getPolymorphicTypeValidator() {
        PolymorphicTypeValidator ptv = this._base.getPolymorphicTypeValidator();
        if (ptv == LaissezFaireSubTypeValidator.instance && isEnabled(MapperFeature.BLOCK_UNSAFE_POLYMORPHIC_BASE_TYPES)) {
            ptv = new DefaultBaseTypeLimitingValidator();
        }
        return ptv;
    }

    public final TypeFactory getTypeFactory() {
        return this._base.getTypeFactory();
    }

    public final JavaType constructType(Class<?> cls) {
        return getTypeFactory().constructType(cls);
    }

    public final JavaType constructType(TypeReference<?> valueTypeRef) {
        return getTypeFactory().constructType(valueTypeRef.getType());
    }

    public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
        return getTypeFactory().constructSpecializedType(baseType, subclass, true);
    }

    public BeanDescription introspectClassAnnotations(Class<?> cls) {
        return introspectClassAnnotations(constructType(cls));
    }

    public BeanDescription introspectClassAnnotations(JavaType type) {
        return getClassIntrospector().forClassAnnotations(this, type, this);
    }

    public BeanDescription introspectDirectClassAnnotations(Class<?> cls) {
        return introspectDirectClassAnnotations(constructType(cls));
    }

    public final BeanDescription introspectDirectClassAnnotations(JavaType type) {
        return getClassIntrospector().forDirectClassAnnotations(this, type, this);
    }

    public JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType, JsonInclude.Value defaultIncl) {
        JsonInclude.Value v = getConfigOverride(baseType).getInclude();
        if (v != null) {
            return v;
        }
        return defaultIncl;
    }

    public JsonInclude.Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType, JsonInclude.Value defaultIncl) {
        JsonInclude.Value baseOverride = getConfigOverride(baseType).getInclude();
        JsonInclude.Value propOverride = getConfigOverride(propertyType).getIncludeAsProperty();
        JsonInclude.Value result = JsonInclude.Value.mergeAll(defaultIncl, baseOverride, propOverride);
        return result;
    }

    public final DateFormat getDateFormat() {
        return this._base.getDateFormat();
    }

    public final Locale getLocale() {
        return this._base.getLocale();
    }

    public final TimeZone getTimeZone() {
        return this._base.getTimeZone();
    }

    public boolean hasExplicitTimeZone() {
        return this._base.hasExplicitTimeZone();
    }

    public Base64Variant getBase64Variant() {
        return this._base.getBase64Variant();
    }

    public TypeResolverBuilder<?> typeResolverBuilderInstance(Annotated annotated, Class<? extends TypeResolverBuilder<?>> builderClass) {
        TypeResolverBuilder<?> builder;
        HandlerInstantiator hi = getHandlerInstantiator();
        if (hi != null && (builder = hi.typeResolverBuilderInstance(this, annotated, builderClass)) != null) {
            return builder;
        }
        return (TypeResolverBuilder) ClassUtil.createInstance(builderClass, canOverrideAccessModifiers());
    }

    public TypeIdResolver typeIdResolverInstance(Annotated annotated, Class<? extends TypeIdResolver> resolverClass) {
        TypeIdResolver builder;
        HandlerInstantiator hi = getHandlerInstantiator();
        if (hi != null && (builder = hi.typeIdResolverInstance(this, annotated, resolverClass)) != null) {
            return builder;
        }
        return (TypeIdResolver) ClassUtil.createInstance(resolverClass, canOverrideAccessModifiers());
    }
}
