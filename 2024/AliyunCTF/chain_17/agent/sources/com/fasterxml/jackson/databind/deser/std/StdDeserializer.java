package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import org.h2.engine.Constants;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/StdDeserializer.class */
public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable, ValueInstantiator.Gettable {
    private static final long serialVersionUID = 1;
    protected static final int F_MASK_INT_COERCIONS = DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.getMask() | DeserializationFeature.USE_LONG_FOR_INTS.getMask();

    @Deprecated
    protected static final int F_MASK_ACCEPT_ARRAYS = DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS.getMask() | DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.getMask();
    protected final Class<?> _valueClass;
    protected final JavaType _valueType;

    /* JADX INFO: Access modifiers changed from: protected */
    public StdDeserializer(Class<?> vc) {
        this._valueClass = vc;
        this._valueType = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StdDeserializer(JavaType valueType) {
        this._valueClass = valueType == null ? Object.class : valueType.getRawClass();
        this._valueType = valueType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StdDeserializer(StdDeserializer<?> src) {
        this._valueClass = src._valueClass;
        this._valueType = src._valueType;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Class<?> handledType() {
        return this._valueClass;
    }

    @Deprecated
    public final Class<?> getValueClass() {
        return this._valueClass;
    }

    public JavaType getValueType() {
        return this._valueType;
    }

    public JavaType getValueType(DeserializationContext ctxt) {
        if (this._valueType != null) {
            return this._valueType;
        }
        return ctxt.constructType(this._valueClass);
    }

    public ValueInstantiator getValueInstantiator() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDefaultDeserializer(JsonDeserializer<?> deserializer) {
        return ClassUtil.isJacksonStdImpl(deserializer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
        return ClassUtil.isJacksonStdImpl(keyDeser);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T _deserializeFromArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        CoercionAction _findCoercionFromEmptyArray = _findCoercionFromEmptyArray(deserializationContext);
        boolean isEnabled = deserializationContext.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        if (isEnabled || _findCoercionFromEmptyArray != CoercionAction.Fail) {
            if (jsonParser.nextToken() == JsonToken.END_ARRAY) {
                switch (_findCoercionFromEmptyArray) {
                    case AsEmpty:
                        return (T) getEmptyValue(deserializationContext);
                    case AsNull:
                    case TryConvert:
                        return getNullValue(deserializationContext);
                }
            }
            if (isEnabled) {
                T _deserializeWrappedValue = _deserializeWrappedValue(jsonParser, deserializationContext);
                if (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(jsonParser, deserializationContext);
                }
                return _deserializeWrappedValue;
            }
        }
        return (T) deserializationContext.handleUnexpectedToken(getValueType(deserializationContext), JsonToken.START_ARRAY, jsonParser, (String) null, new Object[0]);
    }

    @Deprecated
    protected T _deserializeFromEmpty(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.hasToken(JsonToken.START_ARRAY) && deserializationContext.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
            if (jsonParser.nextToken() == JsonToken.END_ARRAY) {
                return null;
            }
            return (T) deserializationContext.handleUnexpectedToken(getValueType(deserializationContext), jsonParser);
        }
        return (T) deserializationContext.handleUnexpectedToken(getValueType(deserializationContext), jsonParser);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T _deserializeFromString(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ValueInstantiator valueInstantiator = getValueInstantiator();
        Class<?> handledType = handledType();
        String valueAsString = jsonParser.getValueAsString();
        if (valueInstantiator != null && valueInstantiator.canCreateFromString()) {
            return (T) valueInstantiator.createFromString(deserializationContext, valueAsString);
        }
        if (valueAsString.isEmpty()) {
            return (T) _deserializeFromEmptyString(jsonParser, deserializationContext, deserializationContext.findCoercionAction(logicalType(), handledType, CoercionInputShape.EmptyString), handledType, "empty String (\"\")");
        }
        if (_isBlank(valueAsString)) {
            return (T) _deserializeFromEmptyString(jsonParser, deserializationContext, deserializationContext.findCoercionFromBlankString(logicalType(), handledType, CoercionAction.Fail), handledType, "blank String (all whitespace)");
        }
        if (valueInstantiator != null) {
            valueAsString = valueAsString.trim();
            if (valueInstantiator.canCreateFromInt() && deserializationContext.findCoercionAction(LogicalType.Integer, Integer.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
                return (T) valueInstantiator.createFromInt(deserializationContext, _parseIntPrimitive(deserializationContext, valueAsString));
            }
            if (valueInstantiator.canCreateFromLong() && deserializationContext.findCoercionAction(LogicalType.Integer, Long.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
                return (T) valueInstantiator.createFromLong(deserializationContext, _parseLongPrimitive(deserializationContext, valueAsString));
            }
            if (valueInstantiator.canCreateFromBoolean() && deserializationContext.findCoercionAction(LogicalType.Boolean, Boolean.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
                String trim = valueAsString.trim();
                if ("true".equals(trim)) {
                    return (T) valueInstantiator.createFromBoolean(deserializationContext, true);
                }
                if ("false".equals(trim)) {
                    return (T) valueInstantiator.createFromBoolean(deserializationContext, false);
                }
            }
        }
        return (T) deserializationContext.handleMissingInstantiator(handledType, valueInstantiator, deserializationContext.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", valueAsString);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object _deserializeFromEmptyString(JsonParser p, DeserializationContext ctxt, CoercionAction act, Class<?> rawTargetType, String desc) throws IOException {
        switch (act) {
            case AsEmpty:
                return getEmptyValue(ctxt);
            case AsNull:
            case TryConvert:
            default:
                return null;
            case Fail:
                _checkCoercionFail(ctxt, act, rawTargetType, "", "empty String (\"\")");
                return null;
        }
    }

    protected T _deserializeWrappedValue(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.hasToken(JsonToken.START_ARRAY)) {
            return (T) handleNestedArrayForSingle(jsonParser, deserializationContext);
        }
        return deserialize(jsonParser, deserializationContext);
    }

    @Deprecated
    protected final boolean _parseBooleanPrimitive(DeserializationContext ctxt, JsonParser p, Class<?> targetType) throws IOException {
        return _parseBooleanPrimitive(p, ctxt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00c7  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean _parseBooleanPrimitive(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 303
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseBooleanPrimitive(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):boolean");
    }

    protected boolean _isTrue(String text) {
        char c = text.charAt(0);
        if (c == 't') {
            return "true".equals(text);
        }
        if (c == 'T') {
            return Constants.CLUSTERING_ENABLED.equals(text) || "True".equals(text);
        }
        return false;
    }

    protected boolean _isFalse(String text) {
        char c = text.charAt(0);
        if (c == 'f') {
            return "false".equals(text);
        }
        if (c == 'F') {
            return "FALSE".equals(text) || "False".equals(text);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Boolean _parseBoolean(JsonParser jsonParser, DeserializationContext deserializationContext, Class<?> cls) throws IOException {
        String extractScalarFromObject;
        switch (jsonParser.currentTokenId()) {
            case 1:
                extractScalarFromObject = deserializationContext.extractScalarFromObject(jsonParser, this, cls);
                break;
            case 2:
            case 4:
            case 5:
            case 8:
            default:
                return (Boolean) deserializationContext.handleUnexpectedToken(cls, jsonParser);
            case 3:
                return (Boolean) _deserializeFromArray(jsonParser, deserializationContext);
            case 6:
                extractScalarFromObject = jsonParser.getText();
                break;
            case 7:
                return _coerceBooleanFromInt(jsonParser, deserializationContext, cls);
            case 9:
                return true;
            case 10:
                return false;
            case 11:
                return null;
        }
        CoercionAction _checkFromStringCoercion = _checkFromStringCoercion(deserializationContext, extractScalarFromObject, LogicalType.Boolean, cls);
        if (_checkFromStringCoercion == CoercionAction.AsNull) {
            return null;
        }
        if (_checkFromStringCoercion == CoercionAction.AsEmpty) {
            return false;
        }
        String trim = extractScalarFromObject.trim();
        int length = trim.length();
        if (length == 4) {
            if (_isTrue(trim)) {
                return true;
            }
        } else if (length == 5 && _isFalse(trim)) {
            return false;
        }
        if (_checkTextualNull(deserializationContext, trim)) {
            return null;
        }
        return (Boolean) deserializationContext.handleWeirdStringValue(cls, trim, "only \"true\" or \"false\" recognized", new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final byte _parseBytePrimitive(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 330
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseBytePrimitive(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):byte");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00e0  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final short _parseShortPrimitive(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 328
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseShortPrimitive(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):short");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00e3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final int _parseIntPrimitive(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 265
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseIntPrimitive(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):int");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int _parseIntPrimitive(DeserializationContext ctxt, String text) throws IOException {
        try {
            if (text.length() > 9) {
                long l = NumberInput.parseLong(text);
                if (_intOverflow(l)) {
                    Number v = (Number) ctxt.handleWeirdStringValue(Integer.TYPE, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    return _nonNullNumber(v).intValue();
                }
                return (int) l;
            }
            return NumberInput.parseInt(text);
        } catch (IllegalArgumentException e) {
            Number v2 = (Number) ctxt.handleWeirdStringValue(Integer.TYPE, text, "not a valid `int` value", new Object[0]);
            return _nonNullNumber(v2).intValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Integer _parseInteger(JsonParser jsonParser, DeserializationContext deserializationContext, Class<?> cls) throws IOException {
        String extractScalarFromObject;
        switch (jsonParser.currentTokenId()) {
            case 1:
                extractScalarFromObject = deserializationContext.extractScalarFromObject(jsonParser, this, cls);
                break;
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            default:
                return (Integer) deserializationContext.handleUnexpectedToken(getValueType(deserializationContext), jsonParser);
            case 3:
                return (Integer) _deserializeFromArray(jsonParser, deserializationContext);
            case 6:
                extractScalarFromObject = jsonParser.getText();
                break;
            case 7:
                return Integer.valueOf(jsonParser.getIntValue());
            case 8:
                CoercionAction _checkFloatToIntCoercion = _checkFloatToIntCoercion(jsonParser, deserializationContext, cls);
                if (_checkFloatToIntCoercion == CoercionAction.AsNull) {
                    return (Integer) getNullValue(deserializationContext);
                }
                if (_checkFloatToIntCoercion == CoercionAction.AsEmpty) {
                    return (Integer) getEmptyValue(deserializationContext);
                }
                return Integer.valueOf(jsonParser.getValueAsInt());
            case 11:
                return (Integer) getNullValue(deserializationContext);
        }
        CoercionAction _checkFromStringCoercion = _checkFromStringCoercion(deserializationContext, extractScalarFromObject);
        if (_checkFromStringCoercion == CoercionAction.AsNull) {
            return (Integer) getNullValue(deserializationContext);
        }
        if (_checkFromStringCoercion == CoercionAction.AsEmpty) {
            return (Integer) getEmptyValue(deserializationContext);
        }
        String trim = extractScalarFromObject.trim();
        if (_checkTextualNull(deserializationContext, trim)) {
            return (Integer) getNullValue(deserializationContext);
        }
        return _parseInteger(deserializationContext, trim);
    }

    protected final Integer _parseInteger(DeserializationContext ctxt, String text) throws IOException {
        try {
            if (text.length() > 9) {
                long l = NumberInput.parseLong(text);
                if (_intOverflow(l)) {
                    return (Integer) ctxt.handleWeirdStringValue(Integer.class, text, "Overflow: numeric value (%s) out of range of `java.lang.Integer` (%d -%d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE);
                }
                return Integer.valueOf((int) l);
            }
            return Integer.valueOf(NumberInput.parseInt(text));
        } catch (IllegalArgumentException e) {
            return (Integer) ctxt.handleWeirdStringValue(Integer.class, text, "not a valid `java.lang.Integer` value", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00e3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final long _parseLongPrimitive(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 265
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseLongPrimitive(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):long");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final long _parseLongPrimitive(DeserializationContext ctxt, String text) throws IOException {
        try {
            return NumberInput.parseLong(text);
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(Long.TYPE, text, "not a valid `long` value", new Object[0]);
            return _nonNullNumber(v).longValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Long _parseLong(JsonParser jsonParser, DeserializationContext deserializationContext, Class<?> cls) throws IOException {
        String extractScalarFromObject;
        switch (jsonParser.currentTokenId()) {
            case 1:
                extractScalarFromObject = deserializationContext.extractScalarFromObject(jsonParser, this, cls);
                break;
            case 2:
            case 4:
            case 5:
            case 9:
            case 10:
            default:
                return (Long) deserializationContext.handleUnexpectedToken(getValueType(deserializationContext), jsonParser);
            case 3:
                return (Long) _deserializeFromArray(jsonParser, deserializationContext);
            case 6:
                extractScalarFromObject = jsonParser.getText();
                break;
            case 7:
                return Long.valueOf(jsonParser.getLongValue());
            case 8:
                CoercionAction _checkFloatToIntCoercion = _checkFloatToIntCoercion(jsonParser, deserializationContext, cls);
                if (_checkFloatToIntCoercion == CoercionAction.AsNull) {
                    return (Long) getNullValue(deserializationContext);
                }
                if (_checkFloatToIntCoercion == CoercionAction.AsEmpty) {
                    return (Long) getEmptyValue(deserializationContext);
                }
                return Long.valueOf(jsonParser.getValueAsLong());
            case 11:
                return (Long) getNullValue(deserializationContext);
        }
        CoercionAction _checkFromStringCoercion = _checkFromStringCoercion(deserializationContext, extractScalarFromObject);
        if (_checkFromStringCoercion == CoercionAction.AsNull) {
            return (Long) getNullValue(deserializationContext);
        }
        if (_checkFromStringCoercion == CoercionAction.AsEmpty) {
            return (Long) getEmptyValue(deserializationContext);
        }
        String trim = extractScalarFromObject.trim();
        if (_checkTextualNull(deserializationContext, trim)) {
            return (Long) getNullValue(deserializationContext);
        }
        return _parseLong(deserializationContext, trim);
    }

    protected final Long _parseLong(DeserializationContext ctxt, String text) throws IOException {
        try {
            return Long.valueOf(NumberInput.parseLong(text));
        } catch (IllegalArgumentException e) {
            return (Long) ctxt.handleWeirdStringValue(Long.class, text, "not a valid `java.lang.Long` value", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00d3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final float _parseFloatPrimitive(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 279
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseFloatPrimitive(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):float");
    }

    protected final float _parseFloatPrimitive(DeserializationContext ctxt, String text) throws IOException {
        try {
            return NumberInput.parseFloat(text);
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(Float.TYPE, text, "not a valid `float` value", new Object[0]);
            return _nonNullNumber(v).floatValue();
        }
    }

    protected final float _parseFloatPrimitive(JsonParser p, DeserializationContext ctxt, String text) throws IOException {
        try {
            return NumberInput.parseFloat(text, p.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(Float.TYPE, text, "not a valid `float` value", new Object[0]);
            return _nonNullNumber(v).floatValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Float _checkFloatSpecialValue(String text) {
        if (!text.isEmpty()) {
            switch (text.charAt(0)) {
                case '-':
                    if (_isNegInf(text)) {
                        return Float.valueOf(Float.NEGATIVE_INFINITY);
                    }
                    return null;
                case 'I':
                    if (_isPosInf(text)) {
                        return Float.valueOf(Float.POSITIVE_INFINITY);
                    }
                    return null;
                case 'N':
                    if (_isNaN(text)) {
                        return Float.valueOf(Float.NaN);
                    }
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:6:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x00d3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final double _parseDoublePrimitive(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 279
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseDoublePrimitive(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):double");
    }

    protected final double _parseDoublePrimitive(DeserializationContext ctxt, String text) throws IOException {
        try {
            return _parseDouble(text);
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(Double.TYPE, text, "not a valid `double` value (as String to convert)", new Object[0]);
            return _nonNullNumber(v).doubleValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final double _parseDoublePrimitive(JsonParser p, DeserializationContext ctxt, String text) throws IOException {
        try {
            return _parseDouble(text, p.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
        } catch (IllegalArgumentException e) {
            Number v = (Number) ctxt.handleWeirdStringValue(Double.TYPE, text, "not a valid `double` value (as String to convert)", new Object[0]);
            return _nonNullNumber(v).doubleValue();
        }
    }

    protected static final double _parseDouble(String numStr) throws NumberFormatException {
        return _parseDouble(numStr, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final double _parseDouble(String numStr, boolean useFastParser) throws NumberFormatException {
        return NumberInput.parseDouble(numStr, useFastParser);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Double _checkDoubleSpecialValue(String text) {
        if (!text.isEmpty()) {
            switch (text.charAt(0)) {
                case '-':
                    if (_isNegInf(text)) {
                        return Double.valueOf(Double.NEGATIVE_INFINITY);
                    }
                    return null;
                case 'I':
                    if (_isPosInf(text)) {
                        return Double.valueOf(Double.POSITIVE_INFINITY);
                    }
                    return null;
                case 'N':
                    if (_isNaN(text)) {
                        return Double.valueOf(Double.NaN);
                    }
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Date _parseDate(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        long longValue;
        String extractScalarFromObject;
        switch (jsonParser.currentTokenId()) {
            case 1:
                extractScalarFromObject = deserializationContext.extractScalarFromObject(jsonParser, this, this._valueClass);
                break;
            case 2:
            case 4:
            case 5:
            case 8:
            case 9:
            case 10:
            default:
                return (Date) deserializationContext.handleUnexpectedToken(this._valueClass, jsonParser);
            case 3:
                return _parseDateFromArray(jsonParser, deserializationContext);
            case 6:
                extractScalarFromObject = jsonParser.getText();
                break;
            case 7:
                try {
                    longValue = jsonParser.getLongValue();
                } catch (StreamReadException e) {
                    longValue = ((Number) deserializationContext.handleWeirdNumberValue(this._valueClass, jsonParser.getNumberValue(), "not a valid 64-bit `long` for creating `java.util.Date`", new Object[0])).longValue();
                }
                return new Date(longValue);
            case 11:
                return (Date) getNullValue(deserializationContext);
        }
        return _parseDate(extractScalarFromObject.trim(), deserializationContext);
    }

    protected Date _parseDateFromArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        CoercionAction _findCoercionFromEmptyArray = _findCoercionFromEmptyArray(deserializationContext);
        boolean isEnabled = deserializationContext.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        if (isEnabled || _findCoercionFromEmptyArray != CoercionAction.Fail) {
            JsonToken nextToken = jsonParser.nextToken();
            if (nextToken == JsonToken.END_ARRAY) {
                switch (_findCoercionFromEmptyArray) {
                    case AsEmpty:
                        return (Date) getEmptyValue(deserializationContext);
                    case AsNull:
                    case TryConvert:
                        return (Date) getNullValue(deserializationContext);
                }
            }
            if (isEnabled) {
                if (nextToken == JsonToken.START_ARRAY) {
                    return (Date) handleNestedArrayForSingle(jsonParser, deserializationContext);
                }
                Date _parseDate = _parseDate(jsonParser, deserializationContext);
                _verifyEndArrayForSingle(jsonParser, deserializationContext);
                return _parseDate;
            }
        }
        return (Date) deserializationContext.handleUnexpectedToken(this._valueClass, JsonToken.START_ARRAY, jsonParser, (String) null, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Date _parseDate(String value, DeserializationContext ctxt) throws IOException {
        try {
            if (value.isEmpty()) {
                CoercionAction act = _checkFromStringCoercion(ctxt, value);
                switch (act) {
                    case AsEmpty:
                        return new Date(0L);
                    case AsNull:
                    case TryConvert:
                    default:
                        return null;
                }
            }
            if (_hasTextualNull(value)) {
                return null;
            }
            return ctxt.parseDate(value);
        } catch (IllegalArgumentException iae) {
            return (Date) ctxt.handleWeirdStringValue(this._valueClass, value, "not a valid representation (error: %s)", ClassUtil.exceptionMessage(iae));
        }
    }

    @Deprecated
    protected final String _parseString(JsonParser p, DeserializationContext ctxt) throws IOException {
        return _parseString(p, ctxt, NullsConstantProvider.nuller());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String _parseString(JsonParser p, DeserializationContext ctxt, NullValueProvider nullProvider) throws IOException {
        String text;
        CoercionAction act = CoercionAction.TryConvert;
        switch (p.currentTokenId()) {
            case 1:
                return ctxt.extractScalarFromObject(p, this, this._valueClass);
            case 6:
                return p.getText();
            case 7:
                act = _checkIntToStringCoercion(p, ctxt, String.class);
                break;
            case 8:
                act = _checkFloatToStringCoercion(p, ctxt, String.class);
                break;
            case 9:
            case 10:
                act = _checkBooleanToStringCoercion(p, ctxt, String.class);
                break;
            case 12:
                Object ob = p.getEmbeddedObject();
                if (ob instanceof byte[]) {
                    return ctxt.getBase64Variant().encode((byte[]) ob, false);
                }
                if (ob == null) {
                    return null;
                }
                return ob.toString();
        }
        if (act == CoercionAction.AsNull) {
            return (String) nullProvider.getNullValue(ctxt);
        }
        if (act == CoercionAction.AsEmpty) {
            return "";
        }
        if (p.currentToken().isScalarValue() && (text = p.getValueAsString()) != null) {
            return text;
        }
        return (String) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean _hasTextualNull(String value) {
        return "null".equals(value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean _isNegInf(String text) {
        return "-Infinity".equals(text) || "-INF".equals(text);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean _isPosInf(String text) {
        return "Infinity".equals(text) || "INF".equals(text);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean _isNaN(String text) {
        return "NaN".equals(text);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final boolean _isBlank(String text) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            if (text.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionAction _checkFromStringCoercion(DeserializationContext ctxt, String value) throws IOException {
        return _checkFromStringCoercion(ctxt, value, logicalType(), handledType());
    }

    protected CoercionAction _checkFromStringCoercion(DeserializationContext ctxt, String value, LogicalType logicalType, Class<?> rawTargetType) throws IOException {
        if (value.isEmpty()) {
            return _checkCoercionFail(ctxt, ctxt.findCoercionAction(logicalType, rawTargetType, CoercionInputShape.EmptyString), rawTargetType, value, "empty String (\"\")");
        }
        if (_isBlank(value)) {
            return _checkCoercionFail(ctxt, ctxt.findCoercionFromBlankString(logicalType, rawTargetType, CoercionAction.Fail), rawTargetType, value, "blank String (all whitespace)");
        }
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)) {
            return CoercionAction.TryConvert;
        }
        CoercionAction act = ctxt.findCoercionAction(logicalType, rawTargetType, CoercionInputShape.String);
        if (act == CoercionAction.Fail) {
            ctxt.reportInputMismatch(this, "Cannot coerce String value (\"%s\") to %s (but might if coercion using `CoercionConfig` was enabled)", value, _coercedTypeDesc());
        }
        return act;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionAction _checkFloatToIntCoercion(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
        CoercionAction act = ctxt.findCoercionAction(LogicalType.Integer, rawTargetType, CoercionInputShape.Float);
        if (act == CoercionAction.Fail) {
            return _checkCoercionFail(ctxt, act, rawTargetType, p.getNumberValue(), "Floating-point value (" + p.getText() + ")");
        }
        return act;
    }

    protected CoercionAction _checkIntToStringCoercion(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
        return _checkToStringCoercion(p, ctxt, rawTargetType, p.getNumberValue(), CoercionInputShape.Integer);
    }

    protected CoercionAction _checkFloatToStringCoercion(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
        return _checkToStringCoercion(p, ctxt, rawTargetType, p.getNumberValue(), CoercionInputShape.Float);
    }

    protected CoercionAction _checkBooleanToStringCoercion(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
        return _checkToStringCoercion(p, ctxt, rawTargetType, Boolean.valueOf(p.getBooleanValue()), CoercionInputShape.Boolean);
    }

    protected CoercionAction _checkToStringCoercion(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType, Object inputValue, CoercionInputShape inputShape) throws IOException {
        CoercionAction act = ctxt.findCoercionAction(LogicalType.Textual, rawTargetType, inputShape);
        if (act == CoercionAction.Fail) {
            return _checkCoercionFail(ctxt, act, rawTargetType, inputValue, inputShape.name() + " value (" + p.getText() + ")");
        }
        return act;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionAction _checkIntToFloatCoercion(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
        CoercionAction act = ctxt.findCoercionAction(LogicalType.Float, rawTargetType, CoercionInputShape.Integer);
        if (act == CoercionAction.Fail) {
            return _checkCoercionFail(ctxt, act, rawTargetType, p.getNumberValue(), "Integer value (" + p.getText() + ")");
        }
        return act;
    }

    protected Boolean _coerceBooleanFromInt(JsonParser p, DeserializationContext ctxt, Class<?> rawTargetType) throws IOException {
        CoercionAction act = ctxt.findCoercionAction(LogicalType.Boolean, rawTargetType, CoercionInputShape.Integer);
        switch (act) {
            case AsEmpty:
                return Boolean.FALSE;
            case AsNull:
                return null;
            case TryConvert:
            default:
                if (p.getNumberType() == JsonParser.NumberType.INT) {
                    return Boolean.valueOf(p.getIntValue() != 0);
                }
                return Boolean.valueOf(!CustomBooleanEditor.VALUE_0.equals(p.getText()));
            case Fail:
                _checkCoercionFail(ctxt, act, rawTargetType, p.getNumberValue(), "Integer value (" + p.getText() + ")");
                return Boolean.FALSE;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionAction _checkCoercionFail(DeserializationContext ctxt, CoercionAction act, Class<?> targetType, Object inputValue, String inputDesc) throws IOException {
        if (act == CoercionAction.Fail) {
            ctxt.reportBadCoercion(this, targetType, inputValue, "Cannot coerce %s to %s (but could if coercion was enabled using `CoercionConfig`)", inputDesc, _coercedTypeDesc(targetType));
        }
        return act;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean _checkTextualNull(DeserializationContext ctxt, String text) throws JsonMappingException {
        if (_hasTextualNull(text)) {
            if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
                _reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, "String \"null\"");
                return true;
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object _coerceIntegral(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
            return p.getBigIntegerValue();
        }
        if (ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS)) {
            return Long.valueOf(p.getLongValue());
        }
        return p.getNumberValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void _verifyNullForPrimitive(DeserializationContext ctxt) throws JsonMappingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            ctxt.reportInputMismatch(this, "Cannot coerce `null` to %s (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)", _coercedTypeDesc());
        }
    }

    protected final void _verifyNullForPrimitiveCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        } else if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        } else {
            return;
        }
        String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
        _reportFailedNullCoerce(ctxt, enable, feat, strDesc);
    }

    protected void _reportFailedNullCoerce(DeserializationContext ctxt, boolean state, Enum<?> feature, String inputDesc) throws JsonMappingException {
        String enableDesc = state ? "enable" : "disable";
        ctxt.reportInputMismatch(this, "Cannot coerce %s to Null value as %s (%s `%s.%s` to allow)", inputDesc, _coercedTypeDesc(), enableDesc, feature.getDeclaringClass().getSimpleName(), feature.name());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String _coercedTypeDesc() {
        boolean structured;
        String typeDesc;
        JavaType t = getValueType();
        if (t != null && !t.isPrimitive()) {
            structured = t.isContainerType() || t.isReferenceType();
            typeDesc = ClassUtil.getTypeDescription(t);
        } else {
            Class<?> cls = handledType();
            structured = ClassUtil.isCollectionMapOrArray(cls);
            typeDesc = ClassUtil.getClassDescription(cls);
        }
        if (structured) {
            return "element of " + typeDesc;
        }
        return typeDesc + " value";
    }

    protected String _coercedTypeDesc(Class<?> rawTargetType) {
        String typeDesc = ClassUtil.getClassDescription(rawTargetType);
        if (ClassUtil.isCollectionMapOrArray(rawTargetType)) {
            return "element of " + typeDesc;
        }
        return typeDesc + " value";
    }

    @Deprecated
    protected boolean _parseBooleanFromInt(JsonParser p, DeserializationContext ctxt) throws IOException {
        _verifyNumberForScalarCoercion(ctxt, p);
        return !CustomBooleanEditor.VALUE_0.equals(p.getText());
    }

    @Deprecated
    protected void _verifyStringForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
        MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            ctxt.reportInputMismatch(this, "Cannot coerce String \"%s\" to %s (enable `%s.%s` to allow)", str, _coercedTypeDesc(), feat.getDeclaringClass().getSimpleName(), feat.name());
        }
    }

    @Deprecated
    protected Object _coerceEmptyString(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        } else if (isPrimitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        } else {
            return getNullValue(ctxt);
        }
        _reportFailedNullCoerce(ctxt, enable, feat, "empty String (\"\")");
        return null;
    }

    @Deprecated
    protected void _failDoubleToIntCoercion(JsonParser p, DeserializationContext ctxt, String type) throws IOException {
        ctxt.reportInputMismatch(handledType(), "Cannot coerce a floating-point value ('%s') into %s (enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow)", p.getValueAsString(), type);
    }

    @Deprecated
    protected final void _verifyNullForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
            _reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, strDesc);
        }
    }

    @Deprecated
    protected void _verifyNumberForScalarCoercion(DeserializationContext ctxt, JsonParser p) throws IOException {
        MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            String valueDesc = p.getText();
            ctxt.reportInputMismatch(this, "Cannot coerce Number (%s) to %s (enable `%s.%s` to allow)", valueDesc, _coercedTypeDesc(), feat.getDeclaringClass().getSimpleName(), feat.name());
        }
    }

    @Deprecated
    protected Object _coerceNullToken(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
        if (isPrimitive) {
            _verifyNullForPrimitive(ctxt);
        }
        return getNullValue(ctxt);
    }

    @Deprecated
    protected Object _coerceTextualNull(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            _reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, "String \"null\"");
        }
        return getNullValue(ctxt);
    }

    @Deprecated
    protected boolean _isEmptyOrTextualNull(String value) {
        return value.isEmpty() || "null".equals(value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property) throws JsonMappingException {
        return ctxt.findContextualValueDeserializer(type, property);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean _isIntNumber(String text) {
        int i;
        int len = text.length();
        if (len > 0) {
            char c = text.charAt(0);
            if (c == '-' || c == '+') {
                if (len == 1) {
                    return false;
                }
                i = 1;
            } else {
                i = 0;
            }
            while (i < len) {
                int ch2 = text.charAt(i);
                if (ch2 <= 57 && ch2 >= 48) {
                    i++;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
        AnnotatedMember member;
        Object convDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (_neitherNull(intr, prop) && (member = prop.getMember()) != null && (convDef = intr.findDeserializationContentConverter(member)) != null) {
            Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
            JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
            if (existingDeserializer == null) {
                existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
            }
            return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
        }
        return existingDeserializer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonFormat.Value findFormatOverrides(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults);
        }
        return ctxt.getDefaultPropertyFormat(typeForDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Boolean findFormatFeature(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat) {
        JsonFormat.Value format = findFormatOverrides(ctxt, prop, typeForDefaults);
        if (format != null) {
            return format.getFeature(feat);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final NullValueProvider findValueNullProvider(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
        if (prop != null) {
            return _findNullProvider(ctxt, prop, propMetadata.getValueNulls(), prop.getValueDeserializer());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public NullValueProvider findContentNullProvider(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> valueDeser) throws JsonMappingException {
        Nulls nulls = findContentNullStyle(ctxt, prop);
        if (nulls == Nulls.SKIP) {
            return NullsConstantProvider.skipper();
        }
        if (nulls == Nulls.FAIL) {
            if (prop == null) {
                JavaType type = ctxt.constructType(valueDeser.handledType());
                if (type.isContainerType()) {
                    type = type.getContentType();
                }
                return NullsFailProvider.constructForRootValue(type);
            }
            return NullsFailProvider.constructForProperty(prop, prop.getType().getContentType());
        }
        NullValueProvider prov = _findNullProvider(ctxt, prop, nulls, valueDeser);
        if (prov != null) {
            return prov;
        }
        return valueDeser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Nulls findContentNullStyle(DeserializationContext ctxt, BeanProperty prop) throws JsonMappingException {
        if (prop != null) {
            return prop.getMetadata().getContentNulls();
        }
        return ctxt.getConfig().getDefaultSetterInfo().getContentNulls();
    }

    protected final NullValueProvider _findNullProvider(DeserializationContext ctxt, BeanProperty prop, Nulls nulls, JsonDeserializer<?> valueDeser) throws JsonMappingException {
        if (nulls == Nulls.FAIL) {
            if (prop == null) {
                Class<?> rawType = valueDeser == null ? Object.class : valueDeser.handledType();
                return NullsFailProvider.constructForRootValue(ctxt.constructType(rawType));
            }
            return NullsFailProvider.constructForProperty(prop);
        }
        if (nulls == Nulls.AS_EMPTY) {
            if (valueDeser == null) {
                return null;
            }
            if (valueDeser instanceof BeanDeserializerBase) {
                BeanDeserializerBase bd = (BeanDeserializerBase) valueDeser;
                ValueInstantiator vi = bd.getValueInstantiator();
                if (!vi.canCreateUsingDefault()) {
                    JavaType type = prop == null ? bd.getValueType() : prop.getType();
                    return (NullValueProvider) ctxt.reportBadDefinition(type, String.format("Cannot create empty instance of %s, no default Creator", type));
                }
            }
            AccessPattern access = valueDeser.getEmptyAccessPattern();
            if (access == AccessPattern.ALWAYS_NULL) {
                return NullsConstantProvider.nuller();
            }
            if (access == AccessPattern.CONSTANT) {
                return NullsConstantProvider.forValue(valueDeser.getEmptyValue(ctxt));
            }
            return new NullsAsEmptyProvider(valueDeser);
        }
        if (nulls == Nulls.SKIP) {
            return NullsConstantProvider.skipper();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionAction _findCoercionFromEmptyString(DeserializationContext ctxt) {
        return ctxt.findCoercionAction(logicalType(), handledType(), CoercionInputShape.EmptyString);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionAction _findCoercionFromEmptyArray(DeserializationContext ctxt) {
        return ctxt.findCoercionAction(logicalType(), handledType(), CoercionInputShape.EmptyArray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CoercionAction _findCoercionFromBlankString(DeserializationContext ctxt) {
        return ctxt.findCoercionFromBlankString(logicalType(), handledType(), CoercionAction.Fail);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object instanceOrClass, String propName) throws IOException {
        if (instanceOrClass == null) {
            instanceOrClass = handledType();
        }
        if (ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
            return;
        }
        p.skipChildren();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleMissingEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
        ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Attempted to unwrap '%s' value from an array (with `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS`) but it contains more than one value", handledType().getName());
    }

    protected Object handleNestedArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
        String msg = String.format("Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s", ClassUtil.nameOf(this._valueClass), JsonToken.START_ARRAY, "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS");
        return ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p, msg, new Object[0]);
    }

    protected void _verifyEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
            handleMissingEndArrayForSingle(p, ctxt);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final boolean _neitherNull(Object a, Object b) {
        return (a == null || b == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean _byteOverflow(int value) {
        return value < -128 || value > 255;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean _shortOverflow(int value) {
        return value < -32768 || value > 32767;
    }

    protected final boolean _intOverflow(long value) {
        return value < -2147483648L || value > 2147483647L;
    }

    protected Number _nonNullNumber(Number n) {
        if (n == null) {
            n = 0;
        }
        return n;
    }
}
