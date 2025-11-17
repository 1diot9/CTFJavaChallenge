package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.LRUMap;
import java.io.Serializable;
import java.util.HashMap;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/DeserializerCache.class */
public final class DeserializerCache implements Serializable {
    private static final long serialVersionUID = 1;
    protected final LRUMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers;
    protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers;

    public DeserializerCache() {
        this(2000);
    }

    public DeserializerCache(int maxSize) {
        this._incompleteDeserializers = new HashMap<>(8);
        int initial = Math.min(64, maxSize >> 2);
        this._cachedDeserializers = new LRUMap<>(initial, maxSize);
    }

    Object writeReplace() {
        this._incompleteDeserializers.clear();
        return this;
    }

    public int cachedDeserializersCount() {
        return this._cachedDeserializers.size();
    }

    public void flushCachedDeserializers() {
        this._cachedDeserializers.clear();
    }

    public JsonDeserializer<Object> findValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType propertyType) throws JsonMappingException {
        JsonDeserializer<Object> deser = _findCachedDeserializer(propertyType);
        if (deser == null) {
            deser = _createAndCacheValueDeserializer(ctxt, factory, propertyType);
            if (deser == null) {
                deser = _handleUnknownValueDeserializer(ctxt, propertyType);
            }
        }
        return deser;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public KeyDeserializer findKeyDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        KeyDeserializer createKeyDeserializer = factory.createKeyDeserializer(ctxt, type);
        if (createKeyDeserializer == 0) {
            return _handleUnknownKeyDeserializer(ctxt, type);
        }
        if (createKeyDeserializer instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer) createKeyDeserializer).resolve(ctxt);
        }
        return createKeyDeserializer;
    }

    public boolean hasValueDeserializerFor(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = _findCachedDeserializer(type);
        if (deser == null) {
            deser = _createAndCacheValueDeserializer(ctxt, factory, type);
        }
        return deser != null;
    }

    protected JsonDeserializer<Object> _findCachedDeserializer(JavaType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null JavaType passed");
        }
        if (_hasCustomHandlers(type)) {
            return null;
        }
        return this._cachedDeserializers.get(type);
    }

    protected JsonDeserializer<Object> _createAndCacheValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser;
        synchronized (this._incompleteDeserializers) {
            JsonDeserializer<Object> deser2 = _findCachedDeserializer(type);
            if (deser2 != null) {
                return deser2;
            }
            int count = this._incompleteDeserializers.size();
            if (count > 0 && (deser = this._incompleteDeserializers.get(type)) != null) {
                return deser;
            }
            try {
                JsonDeserializer<Object> _createAndCache2 = _createAndCache2(ctxt, factory, type);
                if (count == 0 && this._incompleteDeserializers.size() > 0) {
                    this._incompleteDeserializers.clear();
                }
                return _createAndCache2;
            } catch (Throwable th) {
                if (count == 0 && this._incompleteDeserializers.size() > 0) {
                    this._incompleteDeserializers.clear();
                }
                throw th;
            }
        }
    }

    protected JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser;
        try {
            deser = _createDeserializer(ctxt, factory, type);
        } catch (IllegalArgumentException iae) {
            ctxt.reportBadDefinition(type, ClassUtil.exceptionMessage(iae));
            deser = null;
        }
        if (deser == null) {
            return null;
        }
        boolean addToCache = !_hasCustomHandlers(type) && deser.isCachable();
        if (deser instanceof ResolvableDeserializer) {
            this._incompleteDeserializers.put(type, deser);
            ((ResolvableDeserializer) deser).resolve(ctxt);
            this._incompleteDeserializers.remove(type);
        }
        if (addToCache) {
            this._cachedDeserializers.put(type, deser);
        }
        return deser;
    }

    protected JsonDeserializer<Object> _createDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
            type = factory.mapAbstractType(config, type);
        }
        BeanDescription beanDesc = config.introspect(type);
        JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (deser != null) {
            return deser;
        }
        JavaType newType = modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
        if (newType != type) {
            type = newType;
            beanDesc = config.introspect(newType);
        }
        Class<?> builder = beanDesc.findPOJOBuilder();
        if (builder != null) {
            return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
        }
        Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
        if (conv == null) {
            return _createDeserializer2(ctxt, factory, type, beanDesc);
        }
        JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
        }
        return new StdDelegatingDeserializer(conv, delegateType, _createDeserializer2(ctxt, factory, delegateType, beanDesc));
    }

    protected JsonDeserializer<?> _createDeserializer2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        if (type.isEnumType()) {
            return factory.createEnumDeserializer(ctxt, type, beanDesc);
        }
        if (type.isContainerType()) {
            if (type.isArrayType()) {
                return factory.createArrayDeserializer(ctxt, (ArrayType) type, beanDesc);
            }
            if (type.isMapLikeType()) {
                JsonFormat.Value format = beanDesc.findExpectedFormat(null);
                if (format.getShape() != JsonFormat.Shape.OBJECT) {
                    MapLikeType mlt = (MapLikeType) type;
                    if (mlt instanceof MapType) {
                        return factory.createMapDeserializer(ctxt, (MapType) mlt, beanDesc);
                    }
                    return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
                }
            }
            if (type.isCollectionLikeType()) {
                JsonFormat.Value format2 = beanDesc.findExpectedFormat(null);
                if (format2.getShape() != JsonFormat.Shape.OBJECT) {
                    CollectionLikeType clt = (CollectionLikeType) type;
                    if (clt instanceof CollectionType) {
                        return factory.createCollectionDeserializer(ctxt, (CollectionType) clt, beanDesc);
                    }
                    return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
                }
            }
        }
        if (type.isReferenceType()) {
            return factory.createReferenceDeserializer(ctxt, (ReferenceType) type, beanDesc);
        }
        if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
            return factory.createTreeDeserializer(config, type, beanDesc);
        }
        return factory.createBeanDeserializer(ctxt, type, beanDesc);
    }

    protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
        if (deserDef == null) {
            return null;
        }
        JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
        return findConvertingDeserializer(ctxt, ann, deser);
    }

    protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, Annotated a, JsonDeserializer<Object> deser) throws JsonMappingException {
        Converter<Object, Object> conv = findConverter(ctxt, a);
        if (conv == null) {
            return deser;
        }
        JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        return new StdDelegatingDeserializer(conv, delegateType, deser);
    }

    protected Converter<Object, Object> findConverter(DeserializationContext ctxt, Annotated a) throws JsonMappingException {
        Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
        if (convDef == null) {
            return null;
        }
        return ctxt.converterInstance(a, convDef);
    }

    private JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
        Object cdDef;
        JavaType keyType;
        Object kdDef;
        KeyDeserializer kd;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType() && (keyType = type.getKeyType()) != null && keyType.getValueHandler() == null && (kdDef = intr.findKeyDeserializer(a)) != null && (kd = ctxt.keyDeserializerInstance(a, kdDef)) != null) {
            type = ((MapLikeType) type).withKeyValueHandler(kd);
        }
        JavaType contentType = type.getContentType();
        if (contentType != null && contentType.getValueHandler() == null && (cdDef = intr.findContentDeserializer(a)) != null) {
            JsonDeserializer<?> cd = null;
            if (cdDef instanceof JsonDeserializer) {
                cd = (JsonDeserializer) cdDef;
            } else {
                Class<?> cdClass = _verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
                if (cdClass != null) {
                    cd = ctxt.deserializerInstance(a, cdClass);
                }
            }
            if (cd != null) {
                type = type.withContentValueHandler(cd);
            }
        }
        return intr.refineDeserializationType(ctxt.getConfig(), a, type);
    }

    private boolean _hasCustomHandlers(JavaType t) {
        if (t.isContainerType()) {
            JavaType ct = t.getContentType();
            if (ct != null && (ct.getValueHandler() != null || ct.getTypeHandler() != null)) {
                return true;
            }
            if (t.isMapLikeType()) {
                JavaType kt = t.getKeyType();
                if (kt.getValueHandler() != null) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass) {
        if (src == null) {
            return null;
        }
        if (!(src instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
        }
        Class<?> cls = (Class) src;
        if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }

    protected JsonDeserializer<Object> _handleUnknownValueDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        Class<?> rawClass = type.getRawClass();
        if (!ClassUtil.isConcrete(rawClass)) {
            return (JsonDeserializer) ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for abstract type " + type);
        }
        return (JsonDeserializer) ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for type " + type);
    }

    protected KeyDeserializer _handleUnknownKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        return (KeyDeserializer) ctxt.reportBadDefinition(type, "Cannot find a (Map) Key deserializer for type " + type);
    }
}
