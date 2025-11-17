package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.util.EnumMap;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/EnumMapDeserializer.class */
public class EnumMapDeserializer extends ContainerDeserializerBase<EnumMap<?, ?>> implements ContextualDeserializer, ResolvableDeserializer {
    private static final long serialVersionUID = 1;
    protected final Class<?> _enumClass;
    protected KeyDeserializer _keyDeserializer;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;

    public EnumMapDeserializer(JavaType mapType, ValueInstantiator valueInst, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller) {
        super(mapType, nuller, (Boolean) null);
        this._enumClass = mapType.getKeyType().getRawClass();
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = vtd;
        this._valueInstantiator = valueInst;
    }

    protected EnumMapDeserializer(EnumMapDeserializer base, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller) {
        super(base, nuller, base._unwrapSingle);
        this._enumClass = base._enumClass;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = vtd;
        this._valueInstantiator = base._valueInstantiator;
        this._delegateDeserializer = base._delegateDeserializer;
        this._propertyBasedCreator = base._propertyBasedCreator;
    }

    @Deprecated
    public EnumMapDeserializer(JavaType mapType, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd) {
        this(mapType, null, keyDeser, valueDeser, vtd, null);
    }

    public EnumMapDeserializer withResolved(KeyDeserializer keyDeserializer, JsonDeserializer<?> valueDeserializer, TypeDeserializer valueTypeDeser, NullValueProvider nuller) {
        if (keyDeserializer == this._keyDeserializer && nuller == this._nullProvider && valueDeserializer == this._valueDeserializer && valueTypeDeser == this._valueTypeDeserializer) {
            return this;
        }
        return new EnumMapDeserializer(this, keyDeserializer, valueDeserializer, valueTypeDeser, nuller);
    }

    @Override // com.fasterxml.jackson.databind.deser.ResolvableDeserializer
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        if (this._valueInstantiator != null) {
            if (this._valueInstantiator.canCreateUsingDelegate()) {
                JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                if (delegateType == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                this._delegateDeserializer = findDeserializer(ctxt, delegateType, null);
                return;
            }
            if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
                JavaType delegateType2 = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
                if (delegateType2 == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                this._delegateDeserializer = findDeserializer(ctxt, delegateType2, null);
                return;
            }
            if (this._valueInstantiator.canCreateFromObjectWith()) {
                SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
                this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> valueDeser;
        KeyDeserializer keyDeser = this._keyDeserializer;
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
        }
        JsonDeserializer<?> valueDeser2 = this._valueDeserializer;
        JavaType vt = this._containerType.getContentType();
        if (valueDeser2 == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser2, property, vt);
        }
        TypeDeserializer vtd = this._valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return withResolved(keyDeser, valueDeser, vtd, findContentNullProvider(ctxt, property, valueDeser));
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Map;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return constructMap(ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return _deserializeUsingProperties(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return (EnumMap) this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        switch (p.currentTokenId()) {
            case 1:
            case 2:
            case 5:
                return deserialize(p, ctxt, (EnumMap) constructMap(ctxt));
            case 3:
                return _deserializeFromArray(p, ctxt);
            case 4:
            default:
                return (EnumMap) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            case 6:
                return _deserializeFromString(p, ctxt);
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt, EnumMap result) throws IOException {
        String currentName;
        Object value;
        p.setCurrentValue(result);
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
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
                Enum<?> key = (Enum) this._keyDeserializer.deserializeKey(keyStr, ctxt);
                JsonToken t2 = p.nextToken();
                if (key == null) {
                    if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                        return (EnumMap) ctxt.handleWeirdStringValue(this._enumClass, keyStr, "value not one of declared Enum instance names for %s", this._containerType.getKeyType());
                    }
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
                        result.put((EnumMap) key, (Enum<?>) value);
                    } catch (Exception e) {
                        return (EnumMap) wrapAndThrow(ctxt, e, result, keyStr);
                    }
                }
                currentName = p.nextFieldName();
            } else {
                return result;
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    protected EnumMap<?, ?> constructMap(DeserializationContext ctxt) throws JsonMappingException {
        if (this._valueInstantiator == null) {
            return new EnumMap<>(this._enumClass);
        }
        try {
            if (!this._valueInstantiator.canCreateUsingDefault()) {
                return (EnumMap) ctxt.handleMissingInstantiator(handledType(), getValueInstantiator(), null, "no default constructor found", new Object[0]);
            }
            return (EnumMap) this._valueInstantiator.createUsingDefault(ctxt);
        } catch (IOException e) {
            return (EnumMap) ClassUtil.throwAsMappingException(ctxt, e);
        }
    }

    public EnumMap<?, ?> _deserializeUsingProperties(JsonParser p, DeserializationContext ctxt) throws IOException {
        String str;
        Object value;
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
        if (p.isExpectedStartObjectToken()) {
            str = p.nextFieldName();
        } else if (p.hasToken(JsonToken.FIELD_NAME)) {
            str = p.currentName();
        } else {
            str = null;
        }
        while (true) {
            String keyName = str;
            if (keyName != null) {
                JsonToken t = p.nextToken();
                SettableBeanProperty prop = creator.findCreatorProperty(keyName);
                if (prop != null) {
                    if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
                        p.nextToken();
                        try {
                            EnumMap<?, ?> result = (EnumMap) creator.build(ctxt, buffer);
                            return deserialize(p, ctxt, (EnumMap) result);
                        } catch (Exception e) {
                            return (EnumMap) wrapAndThrow(ctxt, e, this._containerType.getRawClass(), keyName);
                        }
                    }
                } else {
                    Enum<?> key = (Enum) this._keyDeserializer.deserializeKey(keyName, ctxt);
                    if (key == null) {
                        if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                            return (EnumMap) ctxt.handleWeirdStringValue(this._enumClass, keyName, "value not one of declared Enum instance names for %s", this._containerType.getKeyType());
                        }
                        p.nextToken();
                        p.skipChildren();
                    } else {
                        try {
                            if (t == JsonToken.VALUE_NULL) {
                                if (!this._skipNullValues) {
                                    value = this._nullProvider.getNullValue(ctxt);
                                }
                            } else if (this._valueTypeDeserializer == null) {
                                value = this._valueDeserializer.deserialize(p, ctxt);
                            } else {
                                value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
                            }
                            buffer.bufferMapProperty(key, value);
                        } catch (Exception e2) {
                            wrapAndThrow(ctxt, e2, this._containerType.getRawClass(), keyName);
                            return null;
                        }
                    }
                }
                str = p.nextFieldName();
            } else {
                try {
                    return (EnumMap) creator.build(ctxt, buffer);
                } catch (Exception e3) {
                    wrapAndThrow(ctxt, e3, this._containerType.getRawClass(), keyName);
                    return null;
                }
            }
        }
    }
}
