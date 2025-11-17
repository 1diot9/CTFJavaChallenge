package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/BasicClassIntrospector.class */
public class BasicClassIntrospector extends ClassIntrospector implements Serializable {
    private static final long serialVersionUID = 2;
    private static final Class<?> CLS_OBJECT = Object.class;
    private static final Class<?> CLS_STRING = String.class;
    private static final Class<?> CLS_JSON_NODE = JsonNode.class;
    protected static final BasicBeanDescription STRING_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(String.class), AnnotatedClassResolver.createPrimordial(CLS_STRING));
    protected static final BasicBeanDescription BOOLEAN_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Boolean.TYPE), AnnotatedClassResolver.createPrimordial(Boolean.TYPE));
    protected static final BasicBeanDescription INT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Integer.TYPE), AnnotatedClassResolver.createPrimordial(Integer.TYPE));
    protected static final BasicBeanDescription LONG_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Long.TYPE), AnnotatedClassResolver.createPrimordial(Long.TYPE));
    protected static final BasicBeanDescription OBJECT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Object.class), AnnotatedClassResolver.createPrimordial(CLS_OBJECT));

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public /* bridge */ /* synthetic */ BeanDescription forDirectClassAnnotations(MapperConfig mapperConfig, JavaType javaType, ClassIntrospector.MixInResolver mixInResolver) {
        return forDirectClassAnnotations((MapperConfig<?>) mapperConfig, javaType, mixInResolver);
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public /* bridge */ /* synthetic */ BeanDescription forClassAnnotations(MapperConfig mapperConfig, JavaType javaType, ClassIntrospector.MixInResolver mixInResolver) {
        return forClassAnnotations((MapperConfig<?>) mapperConfig, javaType, mixInResolver);
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public ClassIntrospector copy() {
        return new BasicClassIntrospector();
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forSerialization(SerializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(config, type);
        if (desc == null) {
            desc = _findStdJdkCollectionDesc(config, type);
            if (desc == null) {
                desc = BasicBeanDescription.forSerialization(collectProperties(config, type, r, true));
            }
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forDeserialization(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(config, type);
        if (desc == null) {
            desc = _findStdJdkCollectionDesc(config, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(collectProperties(config, type, r, false));
            }
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig config, JavaType builderType, ClassIntrospector.MixInResolver r, BeanDescription valueTypeDesc) {
        return BasicBeanDescription.forDeserialization(collectPropertiesWithBuilder(config, builderType, r, valueTypeDesc, false));
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    @Deprecated
    public BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
        return BasicBeanDescription.forDeserialization(collectPropertiesWithBuilder(config, type, r, null, false));
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forCreation(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(config, type);
        if (desc == null) {
            desc = _findStdJdkCollectionDesc(config, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(collectProperties(config, type, r, false));
            }
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(config, type);
        if (desc == null) {
            desc = BasicBeanDescription.forOtherUse(config, type, _resolveAnnotatedClass(config, type, r));
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(config, type);
        if (desc == null) {
            desc = BasicBeanDescription.forOtherUse(config, type, _resolveAnnotatedWithoutSuperTypes(config, type, r));
        }
        return desc;
    }

    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
        AccessorNamingStrategy forPOJO;
        AnnotatedClass classDef = _resolveAnnotatedClass(config, type, r);
        if (type.isRecordType()) {
            forPOJO = config.getAccessorNaming().forRecord(config, classDef);
        } else {
            forPOJO = config.getAccessorNaming().forPOJO(config, classDef);
        }
        AccessorNamingStrategy accNaming = forPOJO;
        return constructPropertyCollector(config, classDef, type, forSerialization, accNaming);
    }

    @Deprecated
    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization, String mutatorPrefix) {
        AnnotatedClass classDef = _resolveAnnotatedClass(config, type, r);
        AccessorNamingStrategy accNaming = new DefaultAccessorNamingStrategy.Provider().withSetterPrefix(mutatorPrefix).forPOJO(config, classDef);
        return constructPropertyCollector(config, classDef, type, forSerialization, accNaming);
    }

    protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, BeanDescription valueTypeDesc, boolean forSerialization) {
        AnnotatedClass builderClassDef = _resolveAnnotatedClass(config, type, r);
        AccessorNamingStrategy accNaming = config.getAccessorNaming().forBuilder(config, builderClassDef, valueTypeDesc);
        return constructPropertyCollector(config, builderClassDef, type, forSerialization, accNaming);
    }

    @Deprecated
    protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
        return collectPropertiesWithBuilder(config, type, r, null, forSerialization);
    }

    protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass classDef, JavaType type, boolean forSerialization, AccessorNamingStrategy accNaming) {
        return new POJOPropertiesCollector(config, forSerialization, type, classDef, accNaming);
    }

    @Deprecated
    protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass ac, JavaType type, boolean forSerialization, String mutatorPrefix) {
        return new POJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
    }

    protected BasicBeanDescription _findStdTypeDesc(MapperConfig<?> config, JavaType type) {
        Class<?> cls = type.getRawClass();
        if (cls.isPrimitive()) {
            if (cls == Integer.TYPE) {
                return INT_DESC;
            }
            if (cls == Long.TYPE) {
                return LONG_DESC;
            }
            if (cls == Boolean.TYPE) {
                return BOOLEAN_DESC;
            }
            return null;
        }
        if (ClassUtil.isJDKClass(cls)) {
            if (cls == CLS_OBJECT) {
                return OBJECT_DESC;
            }
            if (cls == CLS_STRING) {
                return STRING_DESC;
            }
            if (cls == Integer.class) {
                return INT_DESC;
            }
            if (cls == Long.class) {
                return LONG_DESC;
            }
            if (cls == Boolean.class) {
                return BOOLEAN_DESC;
            }
            return null;
        }
        if (CLS_JSON_NODE.isAssignableFrom(cls)) {
            return BasicBeanDescription.forOtherUse(config, type, AnnotatedClassResolver.createPrimordial(cls));
        }
        return null;
    }

    protected boolean _isStdJDKCollection(JavaType type) {
        if (!type.isContainerType() || type.isArrayType()) {
            return false;
        }
        Class<?> raw = type.getRawClass();
        if (ClassUtil.isJDKClass(raw)) {
            if (Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw)) {
                return true;
            }
            return false;
        }
        return false;
    }

    protected BasicBeanDescription _findStdJdkCollectionDesc(MapperConfig<?> cfg, JavaType type) {
        if (_isStdJDKCollection(type)) {
            return BasicBeanDescription.forOtherUse(cfg, type, _resolveAnnotatedClass(cfg, type, cfg));
        }
        return null;
    }

    protected AnnotatedClass _resolveAnnotatedClass(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        return AnnotatedClassResolver.resolve(config, type, r);
    }

    protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
    }
}
