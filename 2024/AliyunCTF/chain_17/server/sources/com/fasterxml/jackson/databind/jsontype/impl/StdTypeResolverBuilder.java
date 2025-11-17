package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collection;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/StdTypeResolverBuilder.class */
public class StdTypeResolverBuilder implements TypeResolverBuilder<StdTypeResolverBuilder> {
    protected JsonTypeInfo.Id _idType;
    protected JsonTypeInfo.As _includeAs;
    protected String _typeProperty;
    protected boolean _typeIdVisible;
    protected Class<?> _defaultImpl;
    protected TypeIdResolver _customIdResolver;

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public /* bridge */ /* synthetic */ StdTypeResolverBuilder withDefaultImpl(Class cls) {
        return withDefaultImpl((Class<?>) cls);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public /* bridge */ /* synthetic */ StdTypeResolverBuilder defaultImpl(Class cls) {
        return defaultImpl((Class<?>) cls);
    }

    public StdTypeResolverBuilder() {
        this._typeIdVisible = false;
    }

    protected StdTypeResolverBuilder(JsonTypeInfo.Id idType, JsonTypeInfo.As idAs, String propName) {
        this._typeIdVisible = false;
        this._idType = idType;
        this._includeAs = idAs;
        this._typeProperty = propName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StdTypeResolverBuilder(StdTypeResolverBuilder base, Class<?> defaultImpl) {
        this._typeIdVisible = false;
        this._idType = base._idType;
        this._includeAs = base._includeAs;
        this._typeProperty = base._typeProperty;
        this._typeIdVisible = base._typeIdVisible;
        this._customIdResolver = base._customIdResolver;
        this._defaultImpl = defaultImpl;
    }

    public static StdTypeResolverBuilder noTypeInfoBuilder() {
        return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, (TypeIdResolver) null);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes) {
        if (idType == null) {
            throw new IllegalArgumentException("idType cannot be null");
        }
        this._idType = idType;
        this._customIdResolver = idRes;
        this._typeProperty = idType.getDefaultPropertyName();
        return this;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        if (baseType.isPrimitive() && !allowPrimitiveTypes(config, baseType)) {
            return null;
        }
        if (this._idType == JsonTypeInfo.Id.DEDUCTION) {
            return AsDeductionTypeSerializer.instance();
        }
        TypeIdResolver idRes = idResolver(config, baseType, subTypeValidator(config), subtypes, true, false);
        switch (this._includeAs) {
            case WRAPPER_ARRAY:
                return new AsArrayTypeSerializer(idRes, null);
            case PROPERTY:
                return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
            case WRAPPER_OBJECT:
                return new AsWrapperTypeSerializer(idRes, null);
            case EXTERNAL_PROPERTY:
                return new AsExternalTypeSerializer(idRes, null, this._typeProperty);
            case EXISTING_PROPERTY:
                return new AsExistingPropertyTypeSerializer(idRes, null, this._typeProperty);
            default:
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
        }
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        if (baseType.isPrimitive() && !allowPrimitiveTypes(config, baseType)) {
            return null;
        }
        PolymorphicTypeValidator subTypeValidator = verifyBaseTypeValidity(config, baseType);
        TypeIdResolver idRes = idResolver(config, baseType, subTypeValidator, subtypes, false, true);
        JavaType defaultImpl = defineDefaultImpl(config, baseType);
        if (this._idType == JsonTypeInfo.Id.DEDUCTION) {
            return new AsDeductionTypeDeserializer(baseType, idRes, defaultImpl, config, subtypes);
        }
        switch (this._includeAs) {
            case WRAPPER_ARRAY:
                return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            case PROPERTY:
            case EXISTING_PROPERTY:
                return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl, this._includeAs, _strictTypeIdHandling(config, baseType));
            case WRAPPER_OBJECT:
                return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            case EXTERNAL_PROPERTY:
                return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
            default:
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
        }
    }

    protected JavaType defineDefaultImpl(DeserializationConfig config, JavaType baseType) {
        if (this._defaultImpl != null) {
            if (this._defaultImpl == Void.class || this._defaultImpl == NoClass.class) {
                return config.getTypeFactory().constructType(this._defaultImpl);
            }
            if (baseType.hasRawClass(this._defaultImpl)) {
                return baseType;
            }
            if (baseType.isTypeOrSuperTypeOf(this._defaultImpl)) {
                return config.getTypeFactory().constructSpecializedType(baseType, this._defaultImpl);
            }
            if (baseType.hasRawClass(this._defaultImpl)) {
                return baseType;
            }
        }
        if (config.isEnabled(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL) && !baseType.isAbstract()) {
            return baseType;
        }
        return null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs) {
        if (includeAs == null) {
            throw new IllegalArgumentException("includeAs cannot be null");
        }
        this._includeAs = includeAs;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
        if (typeIdPropName == null || typeIdPropName.isEmpty()) {
            typeIdPropName = this._idType.getDefaultPropertyName();
        }
        this._typeProperty = typeIdPropName;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl) {
        this._defaultImpl = defaultImpl;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder typeIdVisibility(boolean isVisible) {
        this._typeIdVisible = isVisible;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public StdTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
        if (this._defaultImpl == defaultImpl) {
            return this;
        }
        ClassUtil.verifyMustOverride(StdTypeResolverBuilder.class, this, "withDefaultImpl");
        return new StdTypeResolverBuilder(this, defaultImpl);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
    public Class<?> getDefaultImpl() {
        return this._defaultImpl;
    }

    public String getTypeProperty() {
        return this._typeProperty;
    }

    public boolean isTypeIdVisible() {
        return this._typeIdVisible;
    }

    protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator subtypeValidator, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
        if (this._customIdResolver != null) {
            return this._customIdResolver;
        }
        if (this._idType == null) {
            throw new IllegalStateException("Cannot build, 'init()' not yet called");
        }
        switch (this._idType) {
            case DEDUCTION:
            case CLASS:
                return ClassNameIdResolver.construct(baseType, config, subtypeValidator);
            case MINIMAL_CLASS:
                return MinimalClassNameIdResolver.construct(baseType, config, subtypeValidator);
            case NAME:
                return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
            case NONE:
                return null;
            case CUSTOM:
            default:
                throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
        }
    }

    public PolymorphicTypeValidator subTypeValidator(MapperConfig<?> config) {
        return config.getPolymorphicTypeValidator();
    }

    protected PolymorphicTypeValidator verifyBaseTypeValidity(MapperConfig<?> config, JavaType baseType) {
        PolymorphicTypeValidator ptv = subTypeValidator(config);
        if (this._idType == JsonTypeInfo.Id.CLASS || this._idType == JsonTypeInfo.Id.MINIMAL_CLASS) {
            PolymorphicTypeValidator.Validity validity = ptv.validateBaseType(config, baseType);
            if (validity == PolymorphicTypeValidator.Validity.DENIED) {
                return reportInvalidBaseType(config, baseType, ptv);
            }
            if (validity == PolymorphicTypeValidator.Validity.ALLOWED) {
                return LaissezFaireSubTypeValidator.instance;
            }
        }
        return ptv;
    }

    protected PolymorphicTypeValidator reportInvalidBaseType(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator ptv) {
        throw new IllegalArgumentException(String.format("Configured `PolymorphicTypeValidator` (of type %s) denied resolution of all subtypes of base type %s", ClassUtil.classNameOf(ptv), ClassUtil.classNameOf(baseType.getRawClass())));
    }

    protected boolean allowPrimitiveTypes(MapperConfig<?> config, JavaType baseType) {
        return false;
    }

    protected boolean _strictTypeIdHandling(DeserializationConfig config, JavaType baseType) {
        if (config.isEnabled(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES)) {
            return true;
        }
        return _hasTypeResolver(config, baseType);
    }

    protected boolean _hasTypeResolver(DeserializationConfig config, JavaType baseType) {
        AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, baseType.getRawClass());
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        return ai.findTypeResolver(config, ac, baseType) != null;
    }
}
