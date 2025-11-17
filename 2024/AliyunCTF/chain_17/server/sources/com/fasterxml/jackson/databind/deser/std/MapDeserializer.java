package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/MapDeserializer.class */
public class MapDeserializer extends ContainerDeserializerBase<Map<Object, Object>> implements ContextualDeserializer, ResolvableDeserializer {
    private static final long serialVersionUID = 1;
    protected final KeyDeserializer _keyDeserializer;
    protected boolean _standardStringKey;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;
    protected final boolean _hasDefaultCreator;
    protected Set<String> _ignorableProperties;
    protected Set<String> _includableProperties;
    protected IgnorePropertiesUtil.Checker _inclusionChecker;
    protected boolean _checkDupSquash;

    public MapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
        super(mapType, (NullValueProvider) null, (Boolean) null);
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
        this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
        this._delegateDeserializer = null;
        this._propertyBasedCreator = null;
        this._standardStringKey = _isStdKeyDeser(mapType, keyDeser);
        this._inclusionChecker = null;
        this._checkDupSquash = mapType.getContentType().hasRawClass(Object.class);
    }

    protected MapDeserializer(MapDeserializer src) {
        super(src);
        this._keyDeserializer = src._keyDeserializer;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._hasDefaultCreator = src._hasDefaultCreator;
        this._ignorableProperties = src._ignorableProperties;
        this._includableProperties = src._includableProperties;
        this._inclusionChecker = src._inclusionChecker;
        this._standardStringKey = src._standardStringKey;
        this._checkDupSquash = src._checkDupSquash;
    }

    protected MapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller, Set<String> ignorable) {
        this(src, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable, null);
    }

    protected MapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller, Set<String> ignorable, Set<String> includable) {
        super(src, nuller, src._unwrapSingle);
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = src._valueInstantiator;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._hasDefaultCreator = src._hasDefaultCreator;
        this._ignorableProperties = ignorable;
        this._includableProperties = includable;
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(ignorable, includable);
        this._standardStringKey = _isStdKeyDeser(this._containerType, keyDeser);
        this._checkDupSquash = src._checkDupSquash;
    }

    protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable) {
        return withResolved(keyDeser, valueTypeDeser, valueDeser, nuller, ignorable, this._includableProperties);
    }

    protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable, Set<String> includable) {
        if (this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser && this._nullProvider == nuller && this._ignorableProperties == ignorable && this._includableProperties == includable) {
            return this;
        }
        return new MapDeserializer(this, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable, includable);
    }

    protected final boolean _isStdKeyDeser(JavaType mapType, KeyDeserializer keyDeser) {
        JavaType keyType;
        if (keyDeser == null || (keyType = mapType.getKeyType()) == null) {
            return true;
        }
        Class<?> rawKeyType = keyType.getRawClass();
        return (rawKeyType == String.class || rawKeyType == Object.class) && isDefaultKeyDeserializer(keyDeser);
    }

    @Deprecated
    public void setIgnorableProperties(String[] ignorable) {
        this._ignorableProperties = (ignorable == null || ignorable.length == 0) ? null : ArrayBuilders.arrayToSet(ignorable);
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
    }

    public void setIgnorableProperties(Set<String> ignorable) {
        this._ignorableProperties = (ignorable == null || ignorable.isEmpty()) ? null : ignorable;
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
    }

    public void setIncludableProperties(Set<String> includable) {
        this._includableProperties = includable;
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignorableProperties, this._includableProperties);
    }

    @Override // com.fasterxml.jackson.databind.deser.ResolvableDeserializer
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }
            this._delegateDeserializer = findDeserializer(ctxt, delegateType, null);
        } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
            JavaType delegateType2 = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            if (delegateType2 == null) {
                ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }
            this._delegateDeserializer = findDeserializer(ctxt, delegateType2, null);
        }
        if (this._valueInstantiator.canCreateFromObjectWith()) {
            SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        }
        this._standardStringKey = _isStdKeyDeser(this._containerType, this._keyDeserializer);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v0 */
    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        KeyDeserializer createContextual;
        JsonDeserializer<?> handleSecondaryContextualization;
        AnnotatedMember member;
        Set<String> included;
        ?? r12 = this._keyDeserializer;
        if (r12 == 0) {
            createContextual = deserializationContext.findKeyDeserializer(this._containerType.getKeyType(), beanProperty);
        } else {
            boolean z = r12 instanceof ContextualKeyDeserializer;
            createContextual = r12;
            if (z) {
                createContextual = ((ContextualKeyDeserializer) r12).createContextual(deserializationContext, beanProperty);
            }
        }
        JsonDeserializer<?> jsonDeserializer = this._valueDeserializer;
        if (beanProperty != null) {
            jsonDeserializer = findConvertingContentDeserializer(deserializationContext, beanProperty, jsonDeserializer);
        }
        JavaType contentType = this._containerType.getContentType();
        if (jsonDeserializer == null) {
            handleSecondaryContextualization = deserializationContext.findContextualValueDeserializer(contentType, beanProperty);
        } else {
            handleSecondaryContextualization = deserializationContext.handleSecondaryContextualization(jsonDeserializer, beanProperty, contentType);
        }
        TypeDeserializer typeDeserializer = this._valueTypeDeserializer;
        if (typeDeserializer != null) {
            typeDeserializer = typeDeserializer.forProperty(beanProperty);
        }
        Set<String> set = this._ignorableProperties;
        Set<String> set2 = this._includableProperties;
        AnnotationIntrospector annotationIntrospector = deserializationContext.getAnnotationIntrospector();
        if (_neitherNull(annotationIntrospector, beanProperty) && (member = beanProperty.getMember()) != null) {
            DeserializationConfig config = deserializationContext.getConfig();
            JsonIgnoreProperties.Value findPropertyIgnoralByName = annotationIntrospector.findPropertyIgnoralByName(config, member);
            if (findPropertyIgnoralByName != null) {
                Set<String> findIgnoredForDeserialization = findPropertyIgnoralByName.findIgnoredForDeserialization();
                if (!findIgnoredForDeserialization.isEmpty()) {
                    set = set == null ? new HashSet() : new HashSet(set);
                    Iterator<String> it = findIgnoredForDeserialization.iterator();
                    while (it.hasNext()) {
                        set.add(it.next());
                    }
                }
            }
            JsonIncludeProperties.Value findPropertyInclusionByName = annotationIntrospector.findPropertyInclusionByName(config, member);
            if (findPropertyInclusionByName != null && (included = findPropertyInclusionByName.getIncluded()) != null) {
                HashSet hashSet = new HashSet();
                if (set2 == null) {
                    hashSet = new HashSet(included);
                } else {
                    for (String str : included) {
                        if (set2.contains(str)) {
                            hashSet.add(str);
                        }
                    }
                }
                set2 = hashSet;
            }
        }
        return withResolved(createContextual, typeDeserializer, handleSecondaryContextualization, findContentNullProvider(deserializationContext, beanProperty, handleSecondaryContextualization), set, set2);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null && this._ignorableProperties == null && this._includableProperties == null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Map;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return _deserializeUsingCreator(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return (Map) this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (!this._hasDefaultCreator) {
            return (Map) ctxt.handleMissingInstantiator(getMapClass(), getValueInstantiator(), p, "no default constructor found", new Object[0]);
        }
        switch (p.currentTokenId()) {
            case 1:
            case 2:
            case 5:
                Map<Object, Object> result = (Map) this._valueInstantiator.createUsingDefault(ctxt);
                if (this._standardStringKey) {
                    return _readAndBindStringKeyMap(p, ctxt, result);
                }
                return _readAndBind(p, ctxt, result);
            case 3:
                return _deserializeFromArray(p, ctxt);
            case 4:
            default:
                return (Map) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            case 6:
                return _deserializeFromString(p, ctxt);
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
        p.setCurrentValue(result);
        JsonToken t = p.currentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            return (Map) ctxt.handleUnexpectedToken(getMapClass(), p);
        }
        if (this._standardStringKey) {
            _readAndUpdateStringKeyMap(p, ctxt, result);
            return result;
        }
        _readAndUpdate(p, ctxt, result);
        return result;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    public final Class<?> getMapClass() {
        return this._containerType.getRawClass();
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase, com.fasterxml.jackson.databind.deser.std.StdDeserializer
    public JavaType getValueType() {
        return this._containerType;
    }

    protected final Map<Object, Object> _readAndBind(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
        String currentName;
        Object value;
        KeyDeserializer keyDes = this._keyDeserializer;
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        MapReferringAccumulator referringAccumulator = null;
        boolean useObjectId = valueDes.getObjectIdReader() != null;
        if (useObjectId) {
            referringAccumulator = new MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
        }
        if (p.isExpectedStartObjectToken()) {
            currentName = p.nextFieldName();
        } else {
            JsonToken t = p.currentToken();
            if (t != JsonToken.FIELD_NAME) {
                if (t == JsonToken.END_OBJECT) {
                    return result;
                }
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, (String) null, new Object[0]);
            }
            currentName = p.currentName();
        }
        while (true) {
            String keyStr = currentName;
            if (keyStr != null) {
                Object key = keyDes.deserializeKey(keyStr, ctxt);
                JsonToken t2 = p.nextToken();
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyStr)) {
                    p.skipChildren();
                } else {
                    try {
                        if (t2 == JsonToken.VALUE_NULL) {
                            if (!this._skipNullValues) {
                                value = this._nullProvider.getNullValue(ctxt);
                            }
                        } else if (typeDeser == null) {
                            value = valueDes.deserialize(p, ctxt);
                        } else {
                            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                        }
                        if (useObjectId) {
                            referringAccumulator.put(key, value);
                        } else {
                            Object oldValue = result.put(key, value);
                            if (oldValue != null) {
                                _squashDups(ctxt, result, key, oldValue, value);
                            }
                        }
                    } catch (UnresolvedForwardReference reference) {
                        handleUnresolvedReference(ctxt, referringAccumulator, key, reference);
                    } catch (Exception e) {
                        wrapAndThrow(ctxt, e, result, keyStr);
                    }
                }
                currentName = p.nextFieldName();
            } else {
                return result;
            }
        }
    }

    protected final Map<Object, Object> _readAndBindStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
        String currentName;
        Object value;
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        MapReferringAccumulator referringAccumulator = null;
        boolean useObjectId = valueDes.getObjectIdReader() != null;
        if (useObjectId) {
            referringAccumulator = new MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
        }
        if (p.isExpectedStartObjectToken()) {
            currentName = p.nextFieldName();
        } else {
            JsonToken t = p.currentToken();
            if (t == JsonToken.END_OBJECT) {
                return result;
            }
            if (t != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, (String) null, new Object[0]);
            }
            currentName = p.currentName();
        }
        while (true) {
            String key = currentName;
            if (key != null) {
                JsonToken t2 = p.nextToken();
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
                    p.skipChildren();
                } else {
                    try {
                        if (t2 == JsonToken.VALUE_NULL) {
                            if (!this._skipNullValues) {
                                value = this._nullProvider.getNullValue(ctxt);
                            }
                        } else if (typeDeser == null) {
                            value = valueDes.deserialize(p, ctxt);
                        } else {
                            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                        }
                        if (useObjectId) {
                            referringAccumulator.put(key, value);
                        } else {
                            Object oldValue = result.put(key, value);
                            if (oldValue != null) {
                                _squashDups(ctxt, result, key, oldValue, value);
                            }
                        }
                    } catch (UnresolvedForwardReference reference) {
                        handleUnresolvedReference(ctxt, referringAccumulator, key, reference);
                    } catch (Exception e) {
                        wrapAndThrow(ctxt, e, result, key);
                    }
                }
                currentName = p.nextFieldName();
            } else {
                return result;
            }
        }
    }

    public Map<Object, Object> _deserializeUsingCreator(JsonParser p, DeserializationContext ctxt) throws IOException {
        String str;
        Object value;
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        if (p.isExpectedStartObjectToken()) {
            str = p.nextFieldName();
        } else if (p.hasToken(JsonToken.FIELD_NAME)) {
            str = p.currentName();
        } else {
            str = null;
        }
        while (true) {
            String key = str;
            if (key != null) {
                JsonToken t = p.nextToken();
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
                    p.skipChildren();
                } else {
                    SettableBeanProperty prop = creator.findCreatorProperty(key);
                    if (prop != null) {
                        if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
                            p.nextToken();
                            try {
                                Map<Object, Object> result = (Map) creator.build(ctxt, buffer);
                                return _readAndBind(p, ctxt, result);
                            } catch (Exception e) {
                                return (Map) wrapAndThrow(ctxt, e, this._containerType.getRawClass(), key);
                            }
                        }
                    } else {
                        Object actualKey = this._keyDeserializer.deserializeKey(key, ctxt);
                        try {
                            if (t == JsonToken.VALUE_NULL) {
                                if (!this._skipNullValues) {
                                    value = this._nullProvider.getNullValue(ctxt);
                                }
                            } else if (typeDeser == null) {
                                value = valueDes.deserialize(p, ctxt);
                            } else {
                                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                            }
                            buffer.bufferMapProperty(actualKey, value);
                        } catch (Exception e2) {
                            wrapAndThrow(ctxt, e2, this._containerType.getRawClass(), key);
                            return null;
                        }
                    }
                }
                str = p.nextFieldName();
            } else {
                try {
                    return (Map) creator.build(ctxt, buffer);
                } catch (Exception e3) {
                    wrapAndThrow(ctxt, e3, this._containerType.getRawClass(), key);
                    return null;
                }
            }
        }
    }

    protected final void _readAndUpdate(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
        String currentName;
        Object value;
        KeyDeserializer keyDes = this._keyDeserializer;
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        if (p.isExpectedStartObjectToken()) {
            currentName = p.nextFieldName();
        } else {
            JsonToken t = p.currentToken();
            if (t == JsonToken.END_OBJECT) {
                return;
            }
            if (t != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, (String) null, new Object[0]);
            }
            currentName = p.currentName();
        }
        while (true) {
            String keyStr = currentName;
            if (keyStr != null) {
                Object key = keyDes.deserializeKey(keyStr, ctxt);
                JsonToken t2 = p.nextToken();
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyStr)) {
                    p.skipChildren();
                } else {
                    try {
                        if (t2 == JsonToken.VALUE_NULL) {
                            if (!this._skipNullValues) {
                                result.put(key, this._nullProvider.getNullValue(ctxt));
                            }
                        } else {
                            Object old = result.get(key);
                            if (old != null) {
                                if (typeDeser == null) {
                                    value = valueDes.deserialize(p, ctxt, old);
                                } else {
                                    value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
                                }
                            } else if (typeDeser == null) {
                                value = valueDes.deserialize(p, ctxt);
                            } else {
                                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                            }
                            if (value != old) {
                                result.put(key, value);
                            }
                        }
                    } catch (Exception e) {
                        wrapAndThrow(ctxt, e, result, keyStr);
                    }
                }
                currentName = p.nextFieldName();
            } else {
                return;
            }
        }
    }

    protected final void _readAndUpdateStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
        String currentName;
        Object value;
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        if (p.isExpectedStartObjectToken()) {
            currentName = p.nextFieldName();
        } else {
            JsonToken t = p.currentToken();
            if (t == JsonToken.END_OBJECT) {
                return;
            }
            if (t != JsonToken.FIELD_NAME) {
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, (String) null, new Object[0]);
            }
            currentName = p.currentName();
        }
        while (true) {
            String key = currentName;
            if (key != null) {
                JsonToken t2 = p.nextToken();
                if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(key)) {
                    p.skipChildren();
                } else {
                    try {
                        if (t2 == JsonToken.VALUE_NULL) {
                            if (!this._skipNullValues) {
                                result.put(key, this._nullProvider.getNullValue(ctxt));
                            }
                        } else {
                            Object old = result.get(key);
                            if (old != null) {
                                if (typeDeser == null) {
                                    value = valueDes.deserialize(p, ctxt, old);
                                } else {
                                    value = valueDes.deserializeWithType(p, ctxt, typeDeser, old);
                                }
                            } else if (typeDeser == null) {
                                value = valueDes.deserialize(p, ctxt);
                            } else {
                                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                            }
                            if (value != old) {
                                result.put(key, value);
                            }
                        }
                    } catch (Exception e) {
                        wrapAndThrow(ctxt, e, result, key);
                    }
                }
                currentName = p.nextFieldName();
            } else {
                return;
            }
        }
    }

    protected void _squashDups(DeserializationContext ctxt, Map<Object, Object> result, Object key, Object oldValue, Object newValue) {
        if (this._checkDupSquash && ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES)) {
            if (oldValue instanceof List) {
                ((List) oldValue).add(newValue);
                result.put(key, oldValue);
            } else {
                ArrayList<Object> l = new ArrayList<>();
                l.add(oldValue);
                l.add(newValue);
                result.put(key, l);
            }
        }
    }

    private void handleUnresolvedReference(DeserializationContext ctxt, MapReferringAccumulator accumulator, Object key, UnresolvedForwardReference reference) throws JsonMappingException {
        if (accumulator == null) {
            ctxt.reportInputMismatch(this, "Unresolved forward reference but no identity info: " + reference, new Object[0]);
        }
        ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
        reference.getRoid().appendReferring(referring);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/MapDeserializer$MapReferringAccumulator.class */
    public static final class MapReferringAccumulator {
        private final Class<?> _valueType;
        private Map<Object, Object> _result;
        private List<MapReferring> _accumulator = new ArrayList();

        public MapReferringAccumulator(Class<?> valueType, Map<Object, Object> result) {
            this._valueType = valueType;
            this._result = result;
        }

        public void put(Object key, Object value) {
            if (this._accumulator.isEmpty()) {
                this._result.put(key, value);
            } else {
                MapReferring ref = this._accumulator.get(this._accumulator.size() - 1);
                ref.next.put(key, value);
            }
        }

        public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference, Object key) {
            MapReferring id = new MapReferring(this, reference, this._valueType, key);
            this._accumulator.add(id);
            return id;
        }

        public void resolveForwardReference(Object id, Object value) throws IOException {
            Iterator<MapReferring> iterator = this._accumulator.iterator();
            Map<Object, Object> map = this._result;
            while (true) {
                Map<Object, Object> previous = map;
                if (iterator.hasNext()) {
                    MapReferring ref = iterator.next();
                    if (ref.hasId(id)) {
                        iterator.remove();
                        previous.put(ref.key, value);
                        previous.putAll(ref.next);
                        return;
                    }
                    map = ref.next;
                } else {
                    throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/MapDeserializer$MapReferring.class */
    public static class MapReferring extends ReadableObjectId.Referring {
        private final MapReferringAccumulator _parent;
        public final Map<Object, Object> next;
        public final Object key;

        MapReferring(MapReferringAccumulator parent, UnresolvedForwardReference ref, Class<?> valueType, Object key) {
            super(ref, valueType);
            this.next = new LinkedHashMap();
            this._parent = parent;
            this.key = key;
        }

        @Override // com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring
        public void handleResolvedForwardReference(Object id, Object value) throws IOException {
            this._parent.resolveForwardReference(id, value);
        }
    }
}
