package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.ext.Java7Support;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.AttributePropertyWriter;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.ExceptionUtil;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import java.io.Closeable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/JacksonAnnotationIntrospector.class */
public class JacksonAnnotationIntrospector extends AnnotationIntrospector implements Serializable {
    private static final long serialVersionUID = 1;
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_SER = {JsonSerialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonRawValue.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class};
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_DESER = {JsonDeserialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class, JsonMerge.class};
    private static final Java7Support _java7Helper;
    protected transient LRUMap<String, Boolean> _annotationsInside = new LRUMap<>(48, 48);
    protected boolean _cfgConstructorPropertiesImpliesCreator = true;

    static {
        Java7Support x = null;
        try {
            x = Java7Support.instance();
        } catch (Throwable t) {
            ExceptionUtil.rethrowIfFatal(t);
        }
        _java7Helper = x;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    protected Object readResolve() {
        if (this._annotationsInside == null) {
            this._annotationsInside = new LRUMap<>(48, 48);
        }
        return this;
    }

    public JacksonAnnotationIntrospector setConstructorPropertiesImpliesCreator(boolean b) {
        this._cfgConstructorPropertiesImpliesCreator = b;
        return this;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public boolean isAnnotationBundle(Annotation ann) {
        Class<?> type = ann.annotationType();
        String typeName = type.getName();
        Boolean b = this._annotationsInside.get(typeName);
        if (b == null) {
            b = Boolean.valueOf(type.getAnnotation(JacksonAnnotationsInside.class) != null);
            this._annotationsInside.putIfAbsent(typeName, b);
        }
        return b.booleanValue();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public String findEnumValue(Enum<?> value) {
        JsonProperty prop;
        String n;
        try {
            Field f = value.getDeclaringClass().getField(value.name());
            if (f != null && (prop = (JsonProperty) f.getAnnotation(JsonProperty.class)) != null && (n = prop.value()) != null) {
                if (!n.isEmpty()) {
                    return n;
                }
            }
        } catch (NoSuchFieldException e) {
        } catch (SecurityException e2) {
        }
        return value.name();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
        JsonProperty prop;
        HashMap<String, String> expl = null;
        for (Field f : enumType.getDeclaredFields()) {
            if (f.isEnumConstant() && (prop = (JsonProperty) f.getAnnotation(JsonProperty.class)) != null) {
                String n = prop.value();
                if (!n.isEmpty()) {
                    if (expl == null) {
                        expl = new HashMap<>();
                    }
                    expl.put(f.getName(), n);
                }
            }
        }
        if (expl != null) {
            int end = enumValues.length;
            for (int i = 0; i < end; i++) {
                String defName = enumValues[i].name();
                String explValue = expl.get(defName);
                if (explValue != null) {
                    names[i] = explValue;
                }
            }
        }
        return names;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliasList) {
        JsonAlias aliasAnnotation;
        for (Field f : enumType.getDeclaredFields()) {
            if (f.isEnumConstant() && (aliasAnnotation = (JsonAlias) f.getAnnotation(JsonAlias.class)) != null) {
                String[] aliases = aliasAnnotation.value();
                if (aliases.length != 0) {
                    String name = f.getName();
                    int end = enumValues.length;
                    for (int i = 0; i < end; i++) {
                        if (name.equals(enumValues[i].name())) {
                            aliasList[i] = aliases;
                        }
                    }
                }
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
        return ClassUtil.findFirstAnnotatedEnumValue(enumCls, JsonEnumDefaultValue.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findRootName(AnnotatedClass ac) {
        JsonRootName ann = (JsonRootName) _findAnnotation(ac, JsonRootName.class);
        if (ann == null) {
            return null;
        }
        String ns = ann.namespace();
        if (ns != null && ns.isEmpty()) {
            ns = null;
        }
        return PropertyName.construct(ann.value(), ns);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean isIgnorableType(AnnotatedClass ac) {
        JsonIgnoreType ignore = (JsonIgnoreType) _findAnnotation(ac, JsonIgnoreType.class);
        if (ignore == null) {
            return null;
        }
        return Boolean.valueOf(ignore.value());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated a) {
        JsonIgnoreProperties v = (JsonIgnoreProperties) _findAnnotation(a, JsonIgnoreProperties.class);
        if (v == null) {
            return JsonIgnoreProperties.Value.empty();
        }
        return JsonIgnoreProperties.Value.from(v);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated ac) {
        return findPropertyIgnoralByName(null, ac);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated a) {
        JsonIncludeProperties v = (JsonIncludeProperties) _findAnnotation(a, JsonIncludeProperties.class);
        if (v == null) {
            return JsonIncludeProperties.Value.all();
        }
        return JsonIncludeProperties.Value.from(v);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findFilterId(Annotated a) {
        JsonFilter ann = (JsonFilter) _findAnnotation(a, JsonFilter.class);
        if (ann != null) {
            String id = ann.value();
            if (!id.isEmpty()) {
                return id;
            }
            return null;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findNamingStrategy(AnnotatedClass ac) {
        JsonNaming ann = (JsonNaming) _findAnnotation(ac, JsonNaming.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findEnumNamingStrategy(MapperConfig<?> config, AnnotatedClass ac) {
        EnumNaming ann = (EnumNaming) _findAnnotation(ac, EnumNaming.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findClassDescription(AnnotatedClass ac) {
        JsonClassDescription ann = (JsonClassDescription) _findAnnotation(ac, JsonClassDescription.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [com.fasterxml.jackson.databind.introspect.VisibilityChecker, com.fasterxml.jackson.databind.introspect.VisibilityChecker<?>] */
    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        JsonAutoDetect ann = (JsonAutoDetect) _findAnnotation(ac, JsonAutoDetect.class);
        return ann == null ? checker : checker.with(ann);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findImplicitPropertyName(AnnotatedMember m) {
        PropertyName n = _findConstructorName(m);
        if (n == null) {
            return null;
        }
        return n.getSimpleName();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public List<PropertyName> findPropertyAliases(Annotated m) {
        JsonAlias ann = (JsonAlias) _findAnnotation(m, JsonAlias.class);
        if (ann == null) {
            return null;
        }
        String[] strs = ann.value();
        int len = strs.length;
        if (len == 0) {
            return Collections.emptyList();
        }
        List<PropertyName> result = new ArrayList<>(len);
        for (String str : strs) {
            result.add(PropertyName.construct(str));
        }
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return _isIgnorable(m);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasRequiredMarker(AnnotatedMember m) {
        JsonProperty ann = (JsonProperty) _findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return Boolean.valueOf(ann.required());
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonProperty.Access findPropertyAccess(Annotated m) {
        JsonProperty ann = (JsonProperty) _findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return ann.access();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findPropertyDescription(Annotated ann) {
        JsonPropertyDescription desc = (JsonPropertyDescription) _findAnnotation(ann, JsonPropertyDescription.class);
        if (desc == null) {
            return null;
        }
        return desc.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Integer findPropertyIndex(Annotated ann) {
        int ix;
        JsonProperty prop = (JsonProperty) _findAnnotation(ann, JsonProperty.class);
        if (prop != null && (ix = prop.index()) != -1) {
            return Integer.valueOf(ix);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findPropertyDefaultValue(Annotated ann) {
        JsonProperty prop = (JsonProperty) _findAnnotation(ann, JsonProperty.class);
        if (prop == null) {
            return null;
        }
        String str = prop.defaultValue();
        if (str.isEmpty()) {
            return null;
        }
        return str;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonFormat.Value findFormat(Annotated ann) {
        JsonFormat f = (JsonFormat) _findAnnotation(ann, JsonFormat.class);
        if (f == null) {
            return null;
        }
        return JsonFormat.Value.from(f);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
        JsonManagedReference ref1 = (JsonManagedReference) _findAnnotation(member, JsonManagedReference.class);
        if (ref1 != null) {
            return AnnotationIntrospector.ReferenceProperty.managed(ref1.value());
        }
        JsonBackReference ref2 = (JsonBackReference) _findAnnotation(member, JsonBackReference.class);
        if (ref2 != null) {
            return AnnotationIntrospector.ReferenceProperty.back(ref2.value());
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
        JsonUnwrapped ann = (JsonUnwrapped) _findAnnotation(member, JsonUnwrapped.class);
        if (ann == null || !ann.enabled()) {
            return null;
        }
        String prefix = ann.prefix();
        String suffix = ann.suffix();
        return NameTransformer.simpleTransformer(prefix, suffix);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
        Object id;
        JacksonInject ann = (JacksonInject) _findAnnotation(m, JacksonInject.class);
        if (ann == null) {
            return null;
        }
        JacksonInject.Value v = JacksonInject.Value.from(ann);
        if (!v.hasId()) {
            if (!(m instanceof AnnotatedMethod)) {
                id = m.getRawType().getName();
            } else {
                AnnotatedMethod am = (AnnotatedMethod) m;
                if (am.getParameterCount() == 0) {
                    id = m.getRawType().getName();
                } else {
                    id = am.getRawParameterType(0).getName();
                }
            }
            v = v.withId(id);
        }
        return v;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public Object findInjectableValueId(AnnotatedMember m) {
        JacksonInject.Value v = findInjectableValue(m);
        if (v == null) {
            return null;
        }
        return v.getId();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Class<?>[] findViews(Annotated a) {
        JsonView ann = (JsonView) _findAnnotation(a, JsonView.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
        Class<?> cls1 = setter1.getRawParameterType(0);
        Class<?> cls2 = setter2.getRawParameterType(0);
        if (cls1.isPrimitive()) {
            if (!cls2.isPrimitive()) {
                return setter1;
            }
            return null;
        }
        if (cls2.isPrimitive()) {
            return setter2;
        }
        if (cls1 == String.class) {
            if (cls2 != String.class) {
                return setter1;
            }
            return null;
        }
        if (cls2 == String.class) {
            return setter2;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
        return _findTypeResolver(config, ac, baseType);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
        if (baseType.isContainerType() || baseType.isReferenceType()) {
            return null;
        }
        return _findTypeResolver(config, am, baseType);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
        if (containerType.getContentType() == null) {
            throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
        }
        return _findTypeResolver(config, am, containerType);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public List<NamedType> findSubtypes(Annotated a) {
        JsonSubTypes t = (JsonSubTypes) _findAnnotation(a, JsonSubTypes.class);
        if (t == null) {
            return null;
        }
        JsonSubTypes.Type[] types = t.value();
        if (t.failOnRepeatedNames()) {
            return findSubtypesCheckRepeatedNames(a.getName(), types);
        }
        ArrayList<NamedType> result = new ArrayList<>(types.length);
        for (JsonSubTypes.Type type : types) {
            result.add(new NamedType(type.value(), type.name()));
            for (String name : type.names()) {
                result.add(new NamedType(type.value(), name));
            }
        }
        return result;
    }

    private List<NamedType> findSubtypesCheckRepeatedNames(String annotatedTypeName, JsonSubTypes.Type[] types) {
        ArrayList<NamedType> result = new ArrayList<>(types.length);
        Set<String> seenNames = new HashSet<>();
        for (JsonSubTypes.Type type : types) {
            String typeName = type.name();
            if (!typeName.isEmpty() && seenNames.contains(typeName)) {
                throw new IllegalArgumentException("Annotated type [" + annotatedTypeName + "] got repeated subtype name [" + typeName + "]");
            }
            seenNames.add(typeName);
            result.add(new NamedType(type.value(), typeName));
            for (String altName : type.names()) {
                if (!altName.isEmpty() && seenNames.contains(altName)) {
                    throw new IllegalArgumentException("Annotated type [" + annotatedTypeName + "] got repeated subtype name [" + altName + "]");
                }
                seenNames.add(altName);
                result.add(new NamedType(type.value(), altName));
            }
        }
        return result;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findTypeName(AnnotatedClass ac) {
        JsonTypeName tn = (JsonTypeName) _findAnnotation(ac, JsonTypeName.class);
        if (tn == null) {
            return null;
        }
        return tn.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean isTypeId(AnnotatedMember member) {
        return Boolean.valueOf(_hasAnnotation(member, JsonTypeId.class));
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public ObjectIdInfo findObjectIdInfo(Annotated ann) {
        JsonIdentityInfo info = (JsonIdentityInfo) _findAnnotation(ann, JsonIdentityInfo.class);
        if (info == null || info.generator() == ObjectIdGenerators.None.class) {
            return null;
        }
        PropertyName name = PropertyName.construct(info.property());
        return new ObjectIdInfo(name, info.scope(), info.generator(), info.resolver());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
        JsonIdentityReference ref = (JsonIdentityReference) _findAnnotation(ann, JsonIdentityReference.class);
        if (ref == null) {
            return objectIdInfo;
        }
        if (objectIdInfo == null) {
            objectIdInfo = ObjectIdInfo.empty();
        }
        return objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findSerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.using()) != JsonSerializer.None.class) {
            return serClass;
        }
        JsonRawValue annRaw = (JsonRawValue) _findAnnotation(a, JsonRawValue.class);
        if (annRaw != null && annRaw.value()) {
            Class<?> cls = a.getRawType();
            return new RawSerializer(cls);
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findKeySerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.keyUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findContentSerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.contentUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findNullSerializer(Annotated a) {
        Class<? extends JsonSerializer> serClass;
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null && (serClass = ann.nullsUsing()) != JsonSerializer.None.class) {
            return serClass;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonInclude.Value findPropertyInclusion(Annotated a) {
        JsonInclude inc = (JsonInclude) _findAnnotation(a, JsonInclude.class);
        JsonInclude.Value value = inc == null ? JsonInclude.Value.empty() : JsonInclude.Value.from(inc);
        if (value.getValueInclusion() == JsonInclude.Include.USE_DEFAULTS) {
            value = _refinePropertyInclusion(a, value);
        }
        return value;
    }

    private JsonInclude.Value _refinePropertyInclusion(Annotated a, JsonInclude.Value value) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            switch (ann.include()) {
                case ALWAYS:
                    return value.withValueInclusion(JsonInclude.Include.ALWAYS);
                case NON_NULL:
                    return value.withValueInclusion(JsonInclude.Include.NON_NULL);
                case NON_DEFAULT:
                    return value.withValueInclusion(JsonInclude.Include.NON_DEFAULT);
                case NON_EMPTY:
                    return value.withValueInclusion(JsonInclude.Include.NON_EMPTY);
            }
        }
        return value;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonSerialize.Typing findSerializationTyping(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann == null) {
            return null;
        }
        return ann.typing();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findSerializationConverter(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann == null) {
            return null;
        }
        return _classIfExplicit(ann.converter(), Converter.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findSerializationContentConverter(AnnotatedMember a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann == null) {
            return null;
        }
        return _classIfExplicit(ann.contentConverter(), Converter.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        JavaType contentType;
        JavaType keyType;
        JavaType type = baseType;
        TypeFactory tf = config.getTypeFactory();
        JsonSerialize jsonSer = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        Class<?> serClass = jsonSer == null ? null : _classIfExplicit(jsonSer.as());
        if (serClass != null) {
            if (type.hasRawClass(serClass)) {
                type = type.withStaticTyping();
            } else {
                Class<?> currRaw = type.getRawClass();
                try {
                    if (serClass.isAssignableFrom(currRaw)) {
                        type = tf.constructGeneralizedType(type, serClass);
                    } else if (currRaw.isAssignableFrom(serClass)) {
                        type = tf.constructSpecializedType(type, serClass);
                    } else if (_primitiveAndWrapper(currRaw, serClass)) {
                        type = type.withStaticTyping();
                    } else {
                        throw _databindException(String.format("Cannot refine serialization type %s into %s; types not related", type, serClass.getName()));
                    }
                } catch (IllegalArgumentException iae) {
                    throw _databindException(iae, String.format("Failed to widen type %s with annotation (value %s), from '%s': %s", type, serClass.getName(), a.getName(), iae.getMessage()));
                }
            }
        }
        if (type.isMapLikeType()) {
            JavaType keyType2 = type.getKeyType();
            Class<?> keyClass = jsonSer == null ? null : _classIfExplicit(jsonSer.keyAs());
            if (keyClass != null) {
                if (keyType2.hasRawClass(keyClass)) {
                    keyType = keyType2.withStaticTyping();
                } else {
                    Class<?> currRaw2 = keyType2.getRawClass();
                    try {
                        if (keyClass.isAssignableFrom(currRaw2)) {
                            keyType = tf.constructGeneralizedType(keyType2, keyClass);
                        } else if (currRaw2.isAssignableFrom(keyClass)) {
                            keyType = tf.constructSpecializedType(keyType2, keyClass);
                        } else if (_primitiveAndWrapper(currRaw2, keyClass)) {
                            keyType = keyType2.withStaticTyping();
                        } else {
                            throw _databindException(String.format("Cannot refine serialization key type %s into %s; types not related", keyType2, keyClass.getName()));
                        }
                    } catch (IllegalArgumentException iae2) {
                        throw _databindException(iae2, String.format("Failed to widen key type of %s with concrete-type annotation (value %s), from '%s': %s", type, keyClass.getName(), a.getName(), iae2.getMessage()));
                    }
                }
                type = ((MapLikeType) type).withKeyType(keyType);
            }
        }
        JavaType contentType2 = type.getContentType();
        if (contentType2 != null) {
            Class<?> contentClass = jsonSer == null ? null : _classIfExplicit(jsonSer.contentAs());
            if (contentClass != null) {
                if (contentType2.hasRawClass(contentClass)) {
                    contentType = contentType2.withStaticTyping();
                } else {
                    Class<?> currRaw3 = contentType2.getRawClass();
                    try {
                        if (contentClass.isAssignableFrom(currRaw3)) {
                            contentType = tf.constructGeneralizedType(contentType2, contentClass);
                        } else if (currRaw3.isAssignableFrom(contentClass)) {
                            contentType = tf.constructSpecializedType(contentType2, contentClass);
                        } else if (_primitiveAndWrapper(currRaw3, contentClass)) {
                            contentType = contentType2.withStaticTyping();
                        } else {
                            throw _databindException(String.format("Cannot refine serialization content type %s into %s; types not related", contentType2, contentClass.getName()));
                        }
                    } catch (IllegalArgumentException iae3) {
                        throw _databindException(iae3, String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s", type, contentClass.getName(), a.getName(), iae3.getMessage()));
                    }
                }
                type = type.withContentType(contentType);
            }
        }
        return type;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        JsonPropertyOrder order = (JsonPropertyOrder) _findAnnotation(ac, JsonPropertyOrder.class);
        if (order == null) {
            return null;
        }
        return order.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean findSerializationSortAlphabetically(Annotated ann) {
        return _findSortAlpha(ann);
    }

    private final Boolean _findSortAlpha(Annotated ann) {
        JsonPropertyOrder order = (JsonPropertyOrder) _findAnnotation(ann, JsonPropertyOrder.class);
        if (order != null && order.alphabetic()) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
        JsonAppend ann = (JsonAppend) _findAnnotation(ac, JsonAppend.class);
        if (ann == null) {
            return;
        }
        boolean prepend = ann.prepend();
        JavaType propType = null;
        JsonAppend.Attr[] attrs = ann.attrs();
        int len = attrs.length;
        for (int i = 0; i < len; i++) {
            if (propType == null) {
                propType = config.constructType(Object.class);
            }
            BeanPropertyWriter bpw = _constructVirtualProperty(attrs[i], config, ac, propType);
            if (prepend) {
                properties.add(i, bpw);
            } else {
                properties.add(bpw);
            }
        }
        JsonAppend.Prop[] props = ann.props();
        int len2 = props.length;
        for (int i2 = 0; i2 < len2; i2++) {
            BeanPropertyWriter bpw2 = _constructVirtualProperty(props[i2], config, ac);
            if (prepend) {
                properties.add(i2, bpw2);
            } else {
                properties.add(bpw2);
            }
        }
    }

    protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Attr attr, MapperConfig<?> config, AnnotatedClass ac, JavaType type) {
        PropertyMetadata metadata = attr.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        String attrName = attr.value();
        PropertyName propName = _propertyName(attr.propName(), attr.propNamespace());
        if (!propName.hasSimpleName()) {
            propName = PropertyName.construct(attrName);
        }
        AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), attrName, type);
        SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, attr.include());
        return AttributePropertyWriter.construct(attrName, propDef, ac.getAnnotations(), type);
    }

    protected BeanPropertyWriter _constructVirtualProperty(JsonAppend.Prop prop, MapperConfig<?> config, AnnotatedClass ac) {
        PropertyMetadata metadata = prop.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        PropertyName propName = _propertyName(prop.name(), prop.namespace());
        JavaType type = config.constructType(prop.type());
        AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), propName.getSimpleName(), type);
        SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, prop.include());
        Class<?> implClass = prop.value();
        HandlerInstantiator hi = config.getHandlerInstantiator();
        VirtualBeanPropertyWriter bpw = hi == null ? null : hi.virtualPropertyWriterInstance(config, implClass);
        if (bpw == null) {
            bpw = (VirtualBeanPropertyWriter) ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return bpw.withConfig(config, ac, propDef, type);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findNameForSerialization(Annotated a) {
        boolean useDefault = false;
        JsonGetter jg = (JsonGetter) _findAnnotation(a, JsonGetter.class);
        if (jg != null) {
            String s = jg.value();
            if (!s.isEmpty()) {
                return PropertyName.construct(s);
            }
            useDefault = true;
        }
        JsonProperty pann = (JsonProperty) _findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            String ns = pann.namespace();
            if (ns != null && ns.isEmpty()) {
                ns = null;
            }
            return PropertyName.construct(pann.value(), ns);
        }
        if (useDefault || _hasOneOf(a, ANNOTATIONS_TO_INFER_SER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
        JsonKey ann = (JsonKey) _findAnnotation(a, JsonKey.class);
        if (ann == null) {
            return null;
        }
        return Boolean.valueOf(ann.value());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAsValue(Annotated a) {
        JsonValue ann = (JsonValue) _findAnnotation(a, JsonValue.class);
        if (ann == null) {
            return null;
        }
        return Boolean.valueOf(ann.value());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAnyGetter(Annotated a) {
        JsonAnyGetter ann = (JsonAnyGetter) _findAnnotation(a, JsonAnyGetter.class);
        if (ann == null) {
            return null;
        }
        return Boolean.valueOf(ann.enabled());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return _hasAnnotation(am, JsonAnyGetter.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        JsonValue ann = (JsonValue) _findAnnotation(am, JsonValue.class);
        return ann != null && ann.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findDeserializer(Annotated a) {
        Class<? extends JsonDeserializer> deserClass;
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann != null && (deserClass = ann.using()) != JsonDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findKeyDeserializer(Annotated a) {
        Class<? extends KeyDeserializer> deserClass;
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann != null && (deserClass = ann.keyUsing()) != KeyDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findContentDeserializer(Annotated a) {
        Class<? extends JsonDeserializer> deserClass;
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann != null && (deserClass = ann.contentUsing()) != JsonDeserializer.None.class) {
            return deserClass;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findDeserializationConverter(Annotated a) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann == null) {
            return null;
        }
        return _classIfExplicit(ann.converter(), Converter.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findDeserializationContentConverter(AnnotatedMember a) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann == null) {
            return null;
        }
        return _classIfExplicit(ann.contentConverter(), Converter.None.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        JavaType type = baseType;
        TypeFactory tf = config.getTypeFactory();
        JsonDeserialize jsonDeser = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        Class<?> valueClass = jsonDeser == null ? null : _classIfExplicit(jsonDeser.as());
        if (valueClass != null && !type.hasRawClass(valueClass) && !_primitiveAndWrapper(type, valueClass)) {
            try {
                type = tf.constructSpecializedType(type, valueClass);
            } catch (IllegalArgumentException iae) {
                throw _databindException(iae, String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", type, valueClass.getName(), a.getName(), iae.getMessage()));
            }
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            Class<?> keyClass = jsonDeser == null ? null : _classIfExplicit(jsonDeser.keyAs());
            if (keyClass != null && !_primitiveAndWrapper(keyType, keyClass)) {
                try {
                    type = ((MapLikeType) type).withKeyType(tf.constructSpecializedType(keyType, keyClass));
                } catch (IllegalArgumentException iae2) {
                    throw _databindException(iae2, String.format("Failed to narrow key type of %s with concrete-type annotation (value %s), from '%s': %s", type, keyClass.getName(), a.getName(), iae2.getMessage()));
                }
            }
        }
        JavaType contentType = type.getContentType();
        if (contentType != null) {
            Class<?> contentClass = jsonDeser == null ? null : _classIfExplicit(jsonDeser.contentAs());
            if (contentClass != null && !_primitiveAndWrapper(contentType, contentClass)) {
                try {
                    type = type.withContentType(tf.constructSpecializedType(contentType, contentClass));
                } catch (IllegalArgumentException iae3) {
                    throw _databindException(iae3, String.format("Failed to narrow value type of %s with concrete-type annotation (value %s), from '%s': %s", type, contentClass.getName(), a.getName(), iae3.getMessage()));
                }
            }
        }
        return type;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Object findValueInstantiator(AnnotatedClass ac) {
        JsonValueInstantiator ann = (JsonValueInstantiator) _findAnnotation(ac, JsonValueInstantiator.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(ac, JsonDeserialize.class);
        if (ann == null) {
            return null;
        }
        return _classIfExplicit(ann.builder());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
        JsonPOJOBuilder ann = (JsonPOJOBuilder) _findAnnotation(ac, JsonPOJOBuilder.class);
        if (ann == null) {
            return null;
        }
        return new JsonPOJOBuilder.Value(ann);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public PropertyName findNameForDeserialization(Annotated a) {
        boolean useDefault = false;
        JsonSetter js = (JsonSetter) _findAnnotation(a, JsonSetter.class);
        if (js != null) {
            String s = js.value();
            if (s.isEmpty()) {
                useDefault = true;
            } else {
                return PropertyName.construct(s);
            }
        }
        JsonProperty pann = (JsonProperty) _findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            String ns = pann.namespace();
            if (ns != null && ns.isEmpty()) {
                ns = null;
            }
            return PropertyName.construct(pann.value(), ns);
        }
        if (useDefault || _hasOneOf(a, ANNOTATIONS_TO_INFER_DESER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean hasAnySetter(Annotated a) {
        JsonAnySetter ann = (JsonAnySetter) _findAnnotation(a, JsonAnySetter.class);
        if (ann == null) {
            return null;
        }
        return Boolean.valueOf(ann.enabled());
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonSetter.Value findSetterInfo(Annotated a) {
        return JsonSetter.Value.from((JsonSetter) _findAnnotation(a, JsonSetter.class));
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public Boolean findMergeInfo(Annotated a) {
        JsonMerge ann = (JsonMerge) _findAnnotation(a, JsonMerge.class);
        if (ann == null) {
            return null;
        }
        return ann.value().asBoolean();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return _hasAnnotation(am, JsonAnySetter.class);
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public boolean hasCreatorAnnotation(Annotated a) {
        Boolean b;
        JsonCreator ann = (JsonCreator) _findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            return ann.mode() != JsonCreator.Mode.DISABLED;
        }
        if (this._cfgConstructorPropertiesImpliesCreator && (a instanceof AnnotatedConstructor) && _java7Helper != null && (b = _java7Helper.hasCreatorAnnotation(a)) != null) {
            return b.booleanValue();
        }
        return false;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    @Deprecated
    public JsonCreator.Mode findCreatorBinding(Annotated a) {
        JsonCreator ann = (JsonCreator) _findAnnotation(a, JsonCreator.class);
        if (ann == null) {
            return null;
        }
        return ann.mode();
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        Boolean b;
        JsonCreator ann = (JsonCreator) _findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            return ann.mode();
        }
        if (this._cfgConstructorPropertiesImpliesCreator && config.isEnabled(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES) && (a instanceof AnnotatedConstructor) && _java7Helper != null && (b = _java7Helper.hasCreatorAnnotation(a)) != null && b.booleanValue()) {
            return JsonCreator.Mode.PROPERTIES;
        }
        return null;
    }

    protected boolean _isIgnorable(Annotated a) {
        Boolean b;
        JsonIgnore ann = (JsonIgnore) _findAnnotation(a, JsonIgnore.class);
        if (ann != null) {
            return ann.value();
        }
        if (_java7Helper != null && (b = _java7Helper.findTransient(a)) != null) {
            return b.booleanValue();
        }
        return false;
    }

    protected Class<?> _classIfExplicit(Class<?> cls) {
        if (cls == null || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }

    protected Class<?> _classIfExplicit(Class<?> cls, Class<?> implicit) {
        Class<?> cls2 = _classIfExplicit(cls);
        if (cls2 == null || cls2 == implicit) {
            return null;
        }
        return cls2;
    }

    protected PropertyName _propertyName(String localName, String namespace) {
        if (localName.isEmpty()) {
            return PropertyName.USE_DEFAULT;
        }
        if (namespace == null || namespace.isEmpty()) {
            return PropertyName.construct(localName);
        }
        return PropertyName.construct(localName, namespace);
    }

    protected PropertyName _findConstructorName(Annotated a) {
        PropertyName name;
        if (a instanceof AnnotatedParameter) {
            AnnotatedParameter p = (AnnotatedParameter) a;
            AnnotatedWithParams ctor = p.getOwner();
            if (ctor != null && _java7Helper != null && (name = _java7Helper.findConstructorName(p)) != null) {
                return name;
            }
            return null;
        }
        return null;
    }

    /* JADX WARN: Type inference failed for: r0v24, types: [com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder] */
    protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {
        TypeResolverBuilder<?> b;
        JsonTypeInfo info = (JsonTypeInfo) _findAnnotation(ann, JsonTypeInfo.class);
        JsonTypeResolver resAnn = (JsonTypeResolver) _findAnnotation(ann, JsonTypeResolver.class);
        if (resAnn != null) {
            if (info == null) {
                return null;
            }
            b = config.typeResolverBuilderInstance(ann, resAnn.value());
        } else {
            if (info == null) {
                return null;
            }
            if (info.use() == JsonTypeInfo.Id.NONE) {
                return _constructNoTypeResolverBuilder();
            }
            b = _constructStdTypeResolverBuilder();
        }
        JsonTypeIdResolver idResInfo = (JsonTypeIdResolver) _findAnnotation(ann, JsonTypeIdResolver.class);
        TypeIdResolver idRes = idResInfo == null ? null : config.typeIdResolverInstance(ann, idResInfo.value());
        if (idRes != null) {
            idRes.init(baseType);
        }
        ?? init = b.init(info.use(), idRes);
        JsonTypeInfo.As inclusion = info.include();
        if (inclusion == JsonTypeInfo.As.EXTERNAL_PROPERTY && (ann instanceof AnnotatedClass)) {
            inclusion = JsonTypeInfo.As.PROPERTY;
        }
        TypeResolverBuilder<?> b2 = init.inclusion(inclusion);
        TypeResolverBuilder typeProperty = b2.typeProperty(info.property());
        Class<?> defaultImpl = info.defaultImpl();
        if (defaultImpl != JsonTypeInfo.None.class && !defaultImpl.isAnnotation()) {
            typeProperty = typeProperty.defaultImpl(defaultImpl);
        }
        TypeResolverBuilder<?> b3 = typeProperty.typeIdVisibility(info.visible());
        return b3;
    }

    protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
        return new StdTypeResolverBuilder();
    }

    protected StdTypeResolverBuilder _constructNoTypeResolverBuilder() {
        return StdTypeResolverBuilder.noTypeInfoBuilder();
    }

    private boolean _primitiveAndWrapper(Class<?> baseType, Class<?> refinement) {
        return baseType.isPrimitive() ? baseType == ClassUtil.primitiveType(refinement) : refinement.isPrimitive() && refinement == ClassUtil.primitiveType(baseType);
    }

    private boolean _primitiveAndWrapper(JavaType baseType, Class<?> refinement) {
        if (baseType.isPrimitive()) {
            return baseType.hasRawClass(ClassUtil.primitiveType(refinement));
        }
        return refinement.isPrimitive() && refinement == ClassUtil.primitiveType(baseType.getRawClass());
    }

    private JsonMappingException _databindException(String msg) {
        return new JsonMappingException((Closeable) null, msg);
    }

    private JsonMappingException _databindException(Throwable t, String msg) {
        return new JsonMappingException((Closeable) null, msg, t);
    }
}
