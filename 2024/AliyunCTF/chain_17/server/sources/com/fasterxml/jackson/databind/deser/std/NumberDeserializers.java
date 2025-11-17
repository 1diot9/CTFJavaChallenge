package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers.class */
public class NumberDeserializers {
    private static final HashSet<String> _classNames = new HashSet<>();

    static {
        Class<?>[] numberTypes = {Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class};
        for (Class<?> cls : numberTypes) {
            _classNames.add(cls.getName());
        }
    }

    public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
        if (rawType.isPrimitive()) {
            if (rawType == Integer.TYPE) {
                return IntegerDeserializer.primitiveInstance;
            }
            if (rawType == Boolean.TYPE) {
                return BooleanDeserializer.primitiveInstance;
            }
            if (rawType == Long.TYPE) {
                return LongDeserializer.primitiveInstance;
            }
            if (rawType == Double.TYPE) {
                return DoubleDeserializer.primitiveInstance;
            }
            if (rawType == Character.TYPE) {
                return CharacterDeserializer.primitiveInstance;
            }
            if (rawType == Byte.TYPE) {
                return ByteDeserializer.primitiveInstance;
            }
            if (rawType == Short.TYPE) {
                return ShortDeserializer.primitiveInstance;
            }
            if (rawType == Float.TYPE) {
                return FloatDeserializer.primitiveInstance;
            }
            if (rawType == Void.TYPE) {
                return NullifyingDeserializer.instance;
            }
        } else if (_classNames.contains(clsName)) {
            if (rawType == Integer.class) {
                return IntegerDeserializer.wrapperInstance;
            }
            if (rawType == Boolean.class) {
                return BooleanDeserializer.wrapperInstance;
            }
            if (rawType == Long.class) {
                return LongDeserializer.wrapperInstance;
            }
            if (rawType == Double.class) {
                return DoubleDeserializer.wrapperInstance;
            }
            if (rawType == Character.class) {
                return CharacterDeserializer.wrapperInstance;
            }
            if (rawType == Byte.class) {
                return ByteDeserializer.wrapperInstance;
            }
            if (rawType == Short.class) {
                return ShortDeserializer.wrapperInstance;
            }
            if (rawType == Float.class) {
                return FloatDeserializer.wrapperInstance;
            }
            if (rawType == Number.class) {
                return NumberDeserializer.instance;
            }
            if (rawType == BigDecimal.class) {
                return BigDecimalDeserializer.instance;
            }
            if (rawType == BigInteger.class) {
                return BigIntegerDeserializer.instance;
            }
        } else {
            return null;
        }
        throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$PrimitiveOrWrapperDeserializer.class */
    public static abstract class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T> {
        private static final long serialVersionUID = 1;
        protected final LogicalType _logicalType;
        protected final T _nullValue;
        protected final T _emptyValue;
        protected final boolean _primitive;

        protected PrimitiveOrWrapperDeserializer(Class<T> vc, LogicalType logicalType, T nvl, T empty) {
            super((Class<?>) vc);
            this._logicalType = logicalType;
            this._nullValue = nvl;
            this._emptyValue = empty;
            this._primitive = vc.isPrimitive();
        }

        @Deprecated
        protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl, T empty) {
            this(vc, LogicalType.OtherScalar, nvl, empty);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public AccessPattern getNullAccessPattern() {
            if (this._primitive) {
                return AccessPattern.DYNAMIC;
            }
            if (this._nullValue == null) {
                return AccessPattern.ALWAYS_NULL;
            }
            return AccessPattern.CONSTANT;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public final T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                ctxt.reportInputMismatch(this, "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", ClassUtil.classNameOf(handledType()));
            }
            return this._nullValue;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
            return this._emptyValue;
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public final LogicalType logicalType() {
            return this._logicalType;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$BooleanDeserializer.class */
    public static final class BooleanDeserializer extends PrimitiveOrWrapperDeserializer<Boolean> {
        private static final long serialVersionUID = 1;
        static final BooleanDeserializer primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
        static final BooleanDeserializer wrapperInstance = new BooleanDeserializer(Boolean.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
            super(cls, LogicalType.Boolean, nvl, Boolean.FALSE);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (this._primitive) {
                return Boolean.valueOf(_parseBooleanPrimitive(p, ctxt));
            }
            return _parseBoolean(p, ctxt, this._valueClass);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (this._primitive) {
                return Boolean.valueOf(_parseBooleanPrimitive(p, ctxt));
            }
            return _parseBoolean(p, ctxt, this._valueClass);
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$ByteDeserializer.class */
    public static class ByteDeserializer extends PrimitiveOrWrapperDeserializer<Byte> {
        private static final long serialVersionUID = 1;
        static final ByteDeserializer primitiveInstance = new ByteDeserializer(Byte.TYPE, (byte) 0);
        static final ByteDeserializer wrapperInstance = new ByteDeserializer(Byte.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public ByteDeserializer(Class<Byte> cls, Byte nvl) {
            super(cls, LogicalType.Integer, nvl, (byte) 0);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Byte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return Byte.valueOf(p.getByteValue());
            }
            if (this._primitive) {
                return Byte.valueOf(_parseBytePrimitive(p, ctxt));
            }
            return _parseByte(p, ctxt);
        }

        protected Byte _parseByte(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text;
            switch (p.currentTokenId()) {
                case 1:
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                case 2:
                case 4:
                case 5:
                case 9:
                case 10:
                default:
                    return (Byte) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 6:
                    text = p.getText();
                    break;
                case 7:
                    return Byte.valueOf(p.getByteValue());
                case 8:
                    CoercionAction act = _checkFloatToIntCoercion(p, ctxt, this._valueClass);
                    if (act == CoercionAction.AsNull) {
                        return getNullValue(ctxt);
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return (Byte) getEmptyValue(ctxt);
                    }
                    return Byte.valueOf(p.getByteValue());
                case 11:
                    return getNullValue(ctxt);
            }
            CoercionAction act2 = _checkFromStringCoercion(ctxt, text);
            if (act2 == CoercionAction.AsNull) {
                return getNullValue(ctxt);
            }
            if (act2 == CoercionAction.AsEmpty) {
                return (Byte) getEmptyValue(ctxt);
            }
            String text2 = text.trim();
            if (_checkTextualNull(ctxt, text2)) {
                return getNullValue(ctxt);
            }
            try {
                int value = NumberInput.parseInt(text2);
                if (_byteOverflow(value)) {
                    return (Byte) ctxt.handleWeirdStringValue(this._valueClass, text2, "overflow, value cannot be represented as 8-bit value", new Object[0]);
                }
                return Byte.valueOf((byte) value);
            } catch (IllegalArgumentException e) {
                return (Byte) ctxt.handleWeirdStringValue(this._valueClass, text2, "not a valid Byte value", new Object[0]);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$ShortDeserializer.class */
    public static class ShortDeserializer extends PrimitiveOrWrapperDeserializer<Short> {
        private static final long serialVersionUID = 1;
        static final ShortDeserializer primitiveInstance = new ShortDeserializer(Short.TYPE, 0);
        static final ShortDeserializer wrapperInstance = new ShortDeserializer(Short.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public ShortDeserializer(Class<Short> cls, Short nvl) {
            super(cls, LogicalType.Integer, nvl, (short) 0);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Short deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return Short.valueOf(p.getShortValue());
            }
            if (this._primitive) {
                return Short.valueOf(_parseShortPrimitive(p, ctxt));
            }
            return _parseShort(p, ctxt);
        }

        protected Short _parseShort(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text;
            switch (p.currentTokenId()) {
                case 1:
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                case 2:
                case 4:
                case 5:
                case 9:
                case 10:
                default:
                    return (Short) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 6:
                    text = p.getText();
                    break;
                case 7:
                    return Short.valueOf(p.getShortValue());
                case 8:
                    CoercionAction act = _checkFloatToIntCoercion(p, ctxt, this._valueClass);
                    if (act == CoercionAction.AsNull) {
                        return getNullValue(ctxt);
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return (Short) getEmptyValue(ctxt);
                    }
                    return Short.valueOf(p.getShortValue());
                case 11:
                    return getNullValue(ctxt);
            }
            CoercionAction act2 = _checkFromStringCoercion(ctxt, text);
            if (act2 == CoercionAction.AsNull) {
                return getNullValue(ctxt);
            }
            if (act2 == CoercionAction.AsEmpty) {
                return (Short) getEmptyValue(ctxt);
            }
            String text2 = text.trim();
            if (_checkTextualNull(ctxt, text2)) {
                return getNullValue(ctxt);
            }
            try {
                int value = NumberInput.parseInt(text2);
                if (_shortOverflow(value)) {
                    return (Short) ctxt.handleWeirdStringValue(this._valueClass, text2, "overflow, value cannot be represented as 16-bit value", new Object[0]);
                }
                return Short.valueOf((short) value);
            } catch (IllegalArgumentException e) {
                return (Short) ctxt.handleWeirdStringValue(this._valueClass, text2, "not a valid Short value", new Object[0]);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$CharacterDeserializer.class */
    public static class CharacterDeserializer extends PrimitiveOrWrapperDeserializer<Character> {
        private static final long serialVersionUID = 1;
        static final CharacterDeserializer primitiveInstance = new CharacterDeserializer(Character.TYPE, 0);
        static final CharacterDeserializer wrapperInstance = new CharacterDeserializer(Character.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public CharacterDeserializer(Class<Character> cls, Character nvl) {
            super(cls, LogicalType.Integer, nvl, (char) 0);
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:24:0x0062. Please report as an issue. */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Character deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text;
            switch (p.currentTokenId()) {
                case 1:
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                case 2:
                case 4:
                case 5:
                case 8:
                case 9:
                case 10:
                default:
                    return (Character) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 6:
                    text = p.getText();
                    break;
                case 7:
                    CoercionAction act = ctxt.findCoercionAction(logicalType(), this._valueClass, CoercionInputShape.Integer);
                    switch (act) {
                        case Fail:
                            _checkCoercionFail(ctxt, act, this._valueClass, p.getNumberValue(), "Integer value (" + p.getText() + ")");
                        case AsNull:
                            return getNullValue(ctxt);
                        case AsEmpty:
                            return (Character) getEmptyValue(ctxt);
                        default:
                            int value = p.getIntValue();
                            if (value >= 0 && value <= 65535) {
                                return Character.valueOf((char) value);
                            }
                            return (Character) ctxt.handleWeirdNumberValue(handledType(), Integer.valueOf(value), "value outside valid Character range (0x0000 - 0xFFFF)", new Object[0]);
                    }
                    break;
                case 11:
                    if (this._primitive) {
                        _verifyNullForPrimitive(ctxt);
                    }
                    return getNullValue(ctxt);
            }
            if (text.length() == 1) {
                return Character.valueOf(text.charAt(0));
            }
            CoercionAction act2 = _checkFromStringCoercion(ctxt, text);
            if (act2 == CoercionAction.AsNull) {
                return getNullValue(ctxt);
            }
            if (act2 == CoercionAction.AsEmpty) {
                return (Character) getEmptyValue(ctxt);
            }
            String text2 = text.trim();
            if (_checkTextualNull(ctxt, text2)) {
                return getNullValue(ctxt);
            }
            return (Character) ctxt.handleWeirdStringValue(handledType(), text2, "Expected either Integer value code or 1-character String", new Object[0]);
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$IntegerDeserializer.class */
    public static final class IntegerDeserializer extends PrimitiveOrWrapperDeserializer<Integer> {
        private static final long serialVersionUID = 1;
        static final IntegerDeserializer primitiveInstance = new IntegerDeserializer(Integer.TYPE, 0);
        static final IntegerDeserializer wrapperInstance = new IntegerDeserializer(Integer.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
            super(cls, LogicalType.Integer, nvl, 0);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public boolean isCachable() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return Integer.valueOf(p.getIntValue());
            }
            if (this._primitive) {
                return Integer.valueOf(_parseIntPrimitive(p, ctxt));
            }
            return _parseInteger(p, ctxt, Integer.class);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return Integer.valueOf(p.getIntValue());
            }
            if (this._primitive) {
                return Integer.valueOf(_parseIntPrimitive(p, ctxt));
            }
            return _parseInteger(p, ctxt, Integer.class);
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$LongDeserializer.class */
    public static final class LongDeserializer extends PrimitiveOrWrapperDeserializer<Long> {
        private static final long serialVersionUID = 1;
        static final LongDeserializer primitiveInstance = new LongDeserializer(Long.TYPE, 0L);
        static final LongDeserializer wrapperInstance = new LongDeserializer(Long.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public LongDeserializer(Class<Long> cls, Long nvl) {
            super(cls, LogicalType.Integer, nvl, 0L);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public boolean isCachable() {
            return true;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.isExpectedNumberIntToken()) {
                return Long.valueOf(p.getLongValue());
            }
            if (this._primitive) {
                return Long.valueOf(_parseLongPrimitive(p, ctxt));
            }
            return _parseLong(p, ctxt, Long.class);
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$FloatDeserializer.class */
    public static class FloatDeserializer extends PrimitiveOrWrapperDeserializer<Float> {
        private static final long serialVersionUID = 1;
        static final FloatDeserializer primitiveInstance = new FloatDeserializer(Float.TYPE, Float.valueOf(0.0f));
        static final FloatDeserializer wrapperInstance = new FloatDeserializer(Float.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public FloatDeserializer(Class<Float> cls, Float nvl) {
            super(cls, LogicalType.Float, nvl, Float.valueOf(0.0f));
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return Float.valueOf(p.getFloatValue());
            }
            if (this._primitive) {
                return Float.valueOf(_parseFloatPrimitive(p, ctxt));
            }
            return _parseFloat(p, ctxt);
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
        /* JADX WARN: Removed duplicated region for block: B:6:0x00b9  */
        /* JADX WARN: Removed duplicated region for block: B:9:0x00bc  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected final java.lang.Float _parseFloat(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
            /*
                Method dump skipped, instructions count: 289
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.NumberDeserializers.FloatDeserializer._parseFloat(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):java.lang.Float");
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$DoubleDeserializer.class */
    public static class DoubleDeserializer extends PrimitiveOrWrapperDeserializer<Double> {
        private static final long serialVersionUID = 1;
        static final DoubleDeserializer primitiveInstance = new DoubleDeserializer(Double.TYPE, Double.valueOf(0.0d));
        static final DoubleDeserializer wrapperInstance = new DoubleDeserializer(Double.class, null);

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public /* bridge */ /* synthetic */ Object getEmptyValue(DeserializationContext deserializationContext) throws JsonMappingException {
            return super.getEmptyValue(deserializationContext);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.NumberDeserializers.PrimitiveOrWrapperDeserializer, com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer, com.fasterxml.jackson.databind.deser.NullValueProvider
        public /* bridge */ /* synthetic */ AccessPattern getNullAccessPattern() {
            return super.getNullAccessPattern();
        }

        public DoubleDeserializer(Class<Double> cls, Double nvl) {
            super(cls, LogicalType.Float, nvl, Double.valueOf(0.0d));
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return Double.valueOf(p.getDoubleValue());
            }
            if (this._primitive) {
                return Double.valueOf(_parseDoublePrimitive(p, ctxt));
            }
            return _parseDouble(p, ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Double deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
                return Double.valueOf(p.getDoubleValue());
            }
            if (this._primitive) {
                return Double.valueOf(_parseDoublePrimitive(p, ctxt));
            }
            return _parseDouble(p, ctxt);
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
        /* JADX WARN: Removed duplicated region for block: B:6:0x00b9  */
        /* JADX WARN: Removed duplicated region for block: B:9:0x00bc  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected final java.lang.Double _parseDouble(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
            /*
                Method dump skipped, instructions count: 300
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.NumberDeserializers.DoubleDeserializer._parseDouble(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):java.lang.Double");
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$NumberDeserializer.class */
    public static class NumberDeserializer extends StdScalarDeserializer<Object> {
        public static final NumberDeserializer instance = new NumberDeserializer();

        public NumberDeserializer() {
            super((Class<?>) Number.class);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public final LogicalType logicalType() {
            return LogicalType.Integer;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text;
            switch (p.currentTokenId()) {
                case 1:
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                case 2:
                case 4:
                case 5:
                default:
                    return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 6:
                    text = p.getText();
                    break;
                case 7:
                    if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                        return _coerceIntegral(p, ctxt);
                    }
                    return p.getNumberValue();
                case 8:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) && !p.isNaN()) {
                        return p.getDecimalValue();
                    }
                    return p.getNumberValue();
            }
            CoercionAction act = _checkFromStringCoercion(ctxt, text);
            if (act == CoercionAction.AsNull) {
                return getNullValue(ctxt);
            }
            if (act == CoercionAction.AsEmpty) {
                return getEmptyValue(ctxt);
            }
            String text2 = text.trim();
            if (_hasTextualNull(text2)) {
                return getNullValue(ctxt);
            }
            if (_isPosInf(text2)) {
                return Double.valueOf(Double.POSITIVE_INFINITY);
            }
            if (_isNegInf(text2)) {
                return Double.valueOf(Double.NEGATIVE_INFINITY);
            }
            if (_isNaN(text2)) {
                return Double.valueOf(Double.NaN);
            }
            try {
                if (!_isIntNumber(text2)) {
                    p.streamReadConstraints().validateFPLength(text2.length());
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return NumberInput.parseBigDecimal(text2, p.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                    }
                    return Double.valueOf(NumberInput.parseDouble(text2, p.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER)));
                }
                p.streamReadConstraints().validateIntegerLength(text2.length());
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return NumberInput.parseBigInteger(text2, p.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                }
                long value = NumberInput.parseLong(text2);
                if (!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) && value <= 2147483647L && value >= -2147483648L) {
                    return Integer.valueOf((int) value);
                }
                return Long.valueOf(value);
            } catch (IllegalArgumentException e) {
                return ctxt.handleWeirdStringValue(this._valueClass, text2, "not a valid number", new Object[0]);
            }
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            switch (p.currentTokenId()) {
                case 6:
                case 7:
                case 8:
                    return deserialize(p, ctxt);
                default:
                    return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$BigIntegerDeserializer.class */
    public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger> {
        public static final BigIntegerDeserializer instance = new BigIntegerDeserializer();

        public BigIntegerDeserializer() {
            super((Class<?>) BigInteger.class);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return BigInteger.ZERO;
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public final LogicalType logicalType() {
            return LogicalType.Integer;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text;
            if (p.isExpectedNumberIntToken()) {
                return p.getBigIntegerValue();
            }
            switch (p.currentTokenId()) {
                case 1:
                    text = ctxt.extractScalarFromObject(p, this, this._valueClass);
                    break;
                case 2:
                case 4:
                case 5:
                case 7:
                default:
                    return (BigInteger) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                case 3:
                    return _deserializeFromArray(p, ctxt);
                case 6:
                    text = p.getText();
                    break;
                case 8:
                    CoercionAction act = _checkFloatToIntCoercion(p, ctxt, this._valueClass);
                    if (act == CoercionAction.AsNull) {
                        return getNullValue(ctxt);
                    }
                    if (act == CoercionAction.AsEmpty) {
                        return (BigInteger) getEmptyValue(ctxt);
                    }
                    BigDecimal bd = p.getDecimalValue();
                    p.streamReadConstraints().validateBigIntegerScale(bd.scale());
                    return bd.toBigInteger();
            }
            CoercionAction act2 = _checkFromStringCoercion(ctxt, text);
            if (act2 == CoercionAction.AsNull) {
                return getNullValue(ctxt);
            }
            if (act2 == CoercionAction.AsEmpty) {
                return (BigInteger) getEmptyValue(ctxt);
            }
            String text2 = text.trim();
            if (_hasTextualNull(text2)) {
                return getNullValue(ctxt);
            }
            p.streamReadConstraints().validateIntegerLength(text2.length());
            try {
                return NumberInput.parseBigInteger(text2, p.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
            } catch (IllegalArgumentException e) {
                return (BigInteger) ctxt.handleWeirdStringValue(this._valueClass, text2, "not a valid representation", new Object[0]);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/NumberDeserializers$BigDecimalDeserializer.class */
    public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal> {
        public static final BigDecimalDeserializer instance = new BigDecimalDeserializer();

        public BigDecimalDeserializer() {
            super((Class<?>) BigDecimal.class);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object getEmptyValue(DeserializationContext ctxt) {
            return BigDecimal.ZERO;
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public final LogicalType logicalType() {
            return LogicalType.Float;
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
        /* JADX WARN: Removed duplicated region for block: B:17:0x00a5  */
        /* JADX WARN: Removed duplicated region for block: B:19:0x00ae  */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.math.BigDecimal deserialize(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8) throws java.io.IOException {
            /*
                Method dump skipped, instructions count: 257
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.NumberDeserializers.BigDecimalDeserializer.deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):java.math.BigDecimal");
        }
    }
}
