package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationIntrospectorPair.class */
public class AnnotationIntrospectorPair extends AnnotationIntrospector implements Serializable {
    private static final long serialVersionUID = 1;
    protected final AnnotationIntrospector _primary;
    protected final AnnotationIntrospector _secondary;

    public AnnotationIntrospectorPair(AnnotationIntrospector p, AnnotationIntrospector s) {
        this._primary = p;
        this._secondary = s;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return this._primary.version();
    }

    public static AnnotationIntrospector create(AnnotationIntrospector primary, AnnotationIntrospector secondary) {
        if (primary == null) {
            return secondary;
        }
        if (secondary == null) {
            return primary;
        }
        return new AnnotationIntrospectorPair(primary, secondary);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Collection<AnnotationIntrospector> allIntrospectors() {
        return allIntrospectors(new ArrayList());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
        this._primary.allIntrospectors(result);
        this._secondary.allIntrospectors(result);
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public boolean isAnnotationBundle(Annotation ann) {
        return this._primary.isAnnotationBundle(ann) || this._secondary.isAnnotationBundle(ann);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findRootName(AnnotatedClass ac) {
        PropertyName name1 = this._primary.findRootName(ac);
        if (name1 == null) {
            return this._secondary.findRootName(ac);
        }
        if (name1.hasSimpleName()) {
            return name1;
        }
        PropertyName name2 = this._secondary.findRootName(ac);
        return name2 == null ? name1 : name2;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated ann) {
        JsonIgnoreProperties.Value v2 = this._secondary.findPropertyIgnoralByName(config, ann);
        JsonIgnoreProperties.Value v1 = this._primary.findPropertyIgnoralByName(config, ann);
        return v2 == null ? v1 : v2.withOverrides(v1);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated a) {
        JsonIncludeProperties.Value v2 = this._secondary.findPropertyInclusionByName(config, a);
        JsonIncludeProperties.Value v1 = this._primary.findPropertyInclusionByName(config, a);
        return v2 == null ? v1 : v2.withOverrides(v1);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean isIgnorableType(AnnotatedClass ac) {
        Boolean result = this._primary.isIgnorableType(ac);
        if (result == null) {
            result = this._secondary.isIgnorableType(ac);
        }
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findFilterId(Annotated ann) {
        Object id = this._primary.findFilterId(ann);
        if (id == null) {
            id = this._secondary.findFilterId(ann);
        }
        return id;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findNamingStrategy(AnnotatedClass ac) {
        Object str = this._primary.findNamingStrategy(ac);
        if (str == null) {
            str = this._secondary.findNamingStrategy(ac);
        }
        return str;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findEnumNamingStrategy(MapperConfig<?> config, AnnotatedClass ac) {
        Object str = this._primary.findEnumNamingStrategy(config, ac);
        if (str == null) {
            str = this._secondary.findEnumNamingStrategy(config, ac);
        }
        return str;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findClassDescription(AnnotatedClass ac) {
        String str = this._primary.findClassDescription(ac);
        if (str == null || str.isEmpty()) {
            str = this._secondary.findClassDescription(ac);
        }
        return str;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
        String[] result = this._primary.findPropertiesToIgnore(ac, forSerialization);
        if (result == null) {
            result = this._secondary.findPropertiesToIgnore(ac, forSerialization);
        }
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
        Boolean result = this._primary.findIgnoreUnknownProperties(ac);
        if (result == null) {
            result = this._secondary.findIgnoreUnknownProperties(ac);
        }
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated a) {
        JsonIgnoreProperties.Value v2 = this._secondary.findPropertyIgnorals(a);
        JsonIgnoreProperties.Value v1 = this._primary.findPropertyIgnorals(a);
        return v2 == null ? v1 : v2.withOverrides(v1);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        return this._primary.findAutoDetectVisibility(ac, this._secondary.findAutoDetectVisibility(ac, checker));
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findTypeResolver(config, ac, baseType);
        if (b == null) {
            b = this._secondary.findTypeResolver(config, ac, baseType);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findPropertyTypeResolver(config, am, baseType);
        if (b == null) {
            b = this._secondary.findPropertyTypeResolver(config, am, baseType);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findPropertyContentTypeResolver(config, am, baseType);
        if (b == null) {
            b = this._secondary.findPropertyContentTypeResolver(config, am, baseType);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public List<NamedType> findSubtypes(Annotated a) {
        List<NamedType> types1 = this._primary.findSubtypes(a);
        List<NamedType> types2 = this._secondary.findSubtypes(a);
        if (types1 == null || types1.isEmpty()) {
            return types2;
        }
        if (types2 == null || types2.isEmpty()) {
            return types1;
        }
        ArrayList<NamedType> result = new ArrayList<>(types1.size() + types2.size());
        result.addAll(types1);
        result.addAll(types2);
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findTypeName(AnnotatedClass ac) {
        String name = this._primary.findTypeName(ac);
        if (name == null || name.isEmpty()) {
            name = this._secondary.findTypeName(ac);
        }
        return name;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
        AnnotationIntrospector.ReferenceProperty r = this._primary.findReferenceType(member);
        return r == null ? this._secondary.findReferenceType(member) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
        NameTransformer r = this._primary.findUnwrappingNameTransformer(member);
        return r == null ? this._secondary.findUnwrappingNameTransformer(member) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
        JacksonInject.Value r = this._primary.findInjectableValue(m);
        if (r == null || r.getUseInput() == null) {
            JacksonInject.Value secondary = this._secondary.findInjectableValue(m);
            if (secondary != null) {
                r = r == null ? secondary : r.withUseInput(secondary.getUseInput());
            }
        }
        return r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return this._primary.hasIgnoreMarker(m) || this._secondary.hasIgnoreMarker(m);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasRequiredMarker(AnnotatedMember m) {
        Boolean r = this._primary.hasRequiredMarker(m);
        return r == null ? this._secondary.hasRequiredMarker(m) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public Object findInjectableValueId(AnnotatedMember m) {
        Object r = this._primary.findInjectableValueId(m);
        return r == null ? this._secondary.findInjectableValueId(m) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findSerializer(Annotated am) {
        Object r = this._primary.findSerializer(am);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(this._secondary.findSerializer(am), JsonSerializer.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findKeySerializer(Annotated a) {
        Object r = this._primary.findKeySerializer(a);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(this._secondary.findKeySerializer(a), JsonSerializer.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findContentSerializer(Annotated a) {
        Object r = this._primary.findContentSerializer(a);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(this._secondary.findContentSerializer(a), JsonSerializer.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findNullSerializer(Annotated a) {
        Object r = this._primary.findNullSerializer(a);
        if (_isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(this._secondary.findNullSerializer(a), JsonSerializer.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonInclude.Value findPropertyInclusion(Annotated a) {
        JsonInclude.Value v2 = this._secondary.findPropertyInclusion(a);
        JsonInclude.Value v1 = this._primary.findPropertyInclusion(a);
        if (v2 == null) {
            return v1;
        }
        return v2.withOverrides(v1);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonSerialize.Typing findSerializationTyping(Annotated a) {
        JsonSerialize.Typing r = this._primary.findSerializationTyping(a);
        return r == null ? this._secondary.findSerializationTyping(a) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findSerializationConverter(Annotated a) {
        Object r = this._primary.findSerializationConverter(a);
        return r == null ? this._secondary.findSerializationConverter(a) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findSerializationContentConverter(AnnotatedMember a) {
        Object r = this._primary.findSerializationContentConverter(a);
        return r == null ? this._secondary.findSerializationContentConverter(a) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Class<?>[] findViews(Annotated a) {
        Class<?>[] result = this._primary.findViews(a);
        if (result == null) {
            result = this._secondary.findViews(a);
        }
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean isTypeId(AnnotatedMember member) {
        Boolean b = this._primary.isTypeId(member);
        return b == null ? this._secondary.isTypeId(member) : b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public ObjectIdInfo findObjectIdInfo(Annotated ann) {
        ObjectIdInfo r = this._primary.findObjectIdInfo(ann);
        return r == null ? this._secondary.findObjectIdInfo(ann) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
        return this._primary.findObjectReferenceInfo(ann, this._secondary.findObjectReferenceInfo(ann, objectIdInfo));
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonFormat.Value findFormat(Annotated ann) {
        JsonFormat.Value v1 = this._primary.findFormat(ann);
        JsonFormat.Value v2 = this._secondary.findFormat(ann);
        if (v2 == null) {
            return v1;
        }
        return v2.withOverrides(v1);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findWrapperName(Annotated ann) {
        PropertyName name2;
        PropertyName name = this._primary.findWrapperName(ann);
        if (name == null) {
            name = this._secondary.findWrapperName(ann);
        } else if (name == PropertyName.USE_DEFAULT && (name2 = this._secondary.findWrapperName(ann)) != null) {
            name = name2;
        }
        return name;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findPropertyDefaultValue(Annotated ann) {
        String str = this._primary.findPropertyDefaultValue(ann);
        return (str == null || str.isEmpty()) ? this._secondary.findPropertyDefaultValue(ann) : str;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findPropertyDescription(Annotated ann) {
        String r = this._primary.findPropertyDescription(ann);
        return r == null ? this._secondary.findPropertyDescription(ann) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Integer findPropertyIndex(Annotated ann) {
        Integer r = this._primary.findPropertyIndex(ann);
        return r == null ? this._secondary.findPropertyIndex(ann) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findImplicitPropertyName(AnnotatedMember ann) {
        String r = this._primary.findImplicitPropertyName(ann);
        return r == null ? this._secondary.findImplicitPropertyName(ann) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public List<PropertyName> findPropertyAliases(Annotated ann) {
        List<PropertyName> r = this._primary.findPropertyAliases(ann);
        return r == null ? this._secondary.findPropertyAliases(ann) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonProperty.Access findPropertyAccess(Annotated ann) {
        JsonProperty.Access acc = this._primary.findPropertyAccess(ann);
        if (acc != null && acc != JsonProperty.Access.AUTO) {
            return acc;
        }
        JsonProperty.Access acc2 = this._secondary.findPropertyAccess(ann);
        if (acc2 != null) {
            return acc2;
        }
        return JsonProperty.Access.AUTO;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
        AnnotatedMethod res = this._primary.resolveSetterConflict(config, setter1, setter2);
        if (res == null) {
            res = this._secondary.resolveSetterConflict(config, setter1, setter2);
        }
        return res;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
        PropertyName n = this._secondary.findRenameByField(config, f, implName);
        if (n == null) {
            n = this._primary.findRenameByField(config, f, implName);
        }
        return n;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        JavaType t = this._secondary.refineSerializationType(config, a, baseType);
        return this._primary.refineSerializationType(config, a, t);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        String[] r = this._primary.findSerializationPropertyOrder(ac);
        return r == null ? this._secondary.findSerializationPropertyOrder(ac) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean findSerializationSortAlphabetically(Annotated ann) {
        Boolean r = this._primary.findSerializationSortAlphabetically(ann);
        return r == null ? this._secondary.findSerializationSortAlphabetically(ann) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
        this._primary.findAndAddVirtualProperties(config, ac, properties);
        this._secondary.findAndAddVirtualProperties(config, ac, properties);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findNameForSerialization(Annotated a) {
        PropertyName n2;
        PropertyName n = this._primary.findNameForSerialization(a);
        if (n == null) {
            n = this._secondary.findNameForSerialization(a);
        } else if (n == PropertyName.USE_DEFAULT && (n2 = this._secondary.findNameForSerialization(a)) != null) {
            n = n2;
        }
        return n;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
        Boolean b = this._primary.hasAsKey(config, a);
        if (b == null) {
            b = this._secondary.hasAsKey(config, a);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAsValue(Annotated a) {
        Boolean b = this._primary.hasAsValue(a);
        if (b == null) {
            b = this._secondary.hasAsValue(a);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAnyGetter(Annotated a) {
        Boolean b = this._primary.hasAnyGetter(a);
        if (b == null) {
            b = this._secondary.hasAnyGetter(a);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
        return this._primary.findEnumValues(enumType, enumValues, this._secondary.findEnumValues(enumType, enumValues, names));
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliases) {
        this._secondary.findEnumAliases(enumType, enumValues, aliases);
        this._primary.findEnumAliases(enumType, enumValues, aliases);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
        Enum<?> en = this._primary.findDefaultEnumValue(enumCls);
        return en == null ? this._secondary.findDefaultEnumValue(enumCls) : en;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public String findEnumValue(Enum<?> value) {
        String r = this._primary.findEnumValue(value);
        return r == null ? this._secondary.findEnumValue(value) : r;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        return this._primary.hasAsValueAnnotation(am) || this._secondary.hasAsValueAnnotation(am);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return this._primary.hasAnyGetterAnnotation(am) || this._secondary.hasAnyGetterAnnotation(am);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findDeserializer(Annotated a) {
        Object r = this._primary.findDeserializer(a);
        if (_isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(this._secondary.findDeserializer(a), JsonDeserializer.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findKeyDeserializer(Annotated a) {
        Object r = this._primary.findKeyDeserializer(a);
        if (_isExplicitClassOrOb(r, KeyDeserializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(this._secondary.findKeyDeserializer(a), KeyDeserializer.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findContentDeserializer(Annotated am) {
        Object r = this._primary.findContentDeserializer(am);
        if (_isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
            return r;
        }
        return _explicitClassOrOb(this._secondary.findContentDeserializer(am), JsonDeserializer.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findDeserializationConverter(Annotated a) {
        Object ob = this._primary.findDeserializationConverter(a);
        return ob == null ? this._secondary.findDeserializationConverter(a) : ob;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findDeserializationContentConverter(AnnotatedMember a) {
        Object ob = this._primary.findDeserializationContentConverter(a);
        return ob == null ? this._secondary.findDeserializationContentConverter(a) : ob;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        JavaType t = this._secondary.refineDeserializationType(config, a, baseType);
        return this._primary.refineDeserializationType(config, a, t);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findValueInstantiator(AnnotatedClass ac) {
        Object result = this._primary.findValueInstantiator(ac);
        return result == null ? this._secondary.findValueInstantiator(ac) : result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        Class<?> result = this._primary.findPOJOBuilder(ac);
        return result == null ? this._secondary.findPOJOBuilder(ac) : result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
        JsonPOJOBuilder.Value result = this._primary.findPOJOBuilderConfig(ac);
        return result == null ? this._secondary.findPOJOBuilderConfig(ac) : result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findNameForDeserialization(Annotated a) {
        PropertyName n2;
        PropertyName n = this._primary.findNameForDeserialization(a);
        if (n == null) {
            n = this._secondary.findNameForDeserialization(a);
        } else if (n == PropertyName.USE_DEFAULT && (n2 = this._secondary.findNameForDeserialization(a)) != null) {
            n = n2;
        }
        return n;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAnySetter(Annotated a) {
        Boolean b = this._primary.hasAnySetter(a);
        if (b == null) {
            b = this._secondary.hasAnySetter(a);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonSetter.Value findSetterInfo(Annotated a) {
        JsonSetter.Value v2 = this._secondary.findSetterInfo(a);
        JsonSetter.Value v1 = this._primary.findSetterInfo(a);
        return v2 == null ? v1 : v2.withOverrides(v1);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean findMergeInfo(Annotated a) {
        Boolean b = this._primary.findMergeInfo(a);
        if (b == null) {
            b = this._secondary.findMergeInfo(a);
        }
        return b;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasCreatorAnnotation(Annotated a) {
        return this._primary.hasCreatorAnnotation(a) || this._secondary.hasCreatorAnnotation(a);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public JsonCreator.Mode findCreatorBinding(Annotated a) {
        JsonCreator.Mode mode = this._primary.findCreatorBinding(a);
        if (mode != null) {
            return mode;
        }
        return this._secondary.findCreatorBinding(a);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        JsonCreator.Mode mode = this._primary.findCreatorAnnotation(config, a);
        return mode == null ? this._secondary.findCreatorAnnotation(config, a) : mode;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return this._primary.hasAnySetterAnnotation(am) || this._secondary.hasAnySetterAnnotation(am);
    }

    protected boolean _isExplicitClassOrOb(Object maybeCls, Class<?> implicit) {
        if (maybeCls == null || maybeCls == implicit) {
            return false;
        }
        return ((maybeCls instanceof Class) && ClassUtil.isBogusClass((Class) maybeCls)) ? false : true;
    }

    protected Object _explicitClassOrOb(Object maybeCls, Class<?> implicit) {
        if (maybeCls == null || maybeCls == implicit) {
            return null;
        }
        if ((maybeCls instanceof Class) && ClassUtil.isBogusClass((Class) maybeCls)) {
            return null;
        }
        return maybeCls;
    }
}
