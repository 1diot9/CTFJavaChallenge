package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers.class */
public abstract class PrimitiveArrayDeserializers<T> extends StdDeserializer<T> implements ContextualDeserializer {
    protected final Boolean _unwrapSingle;
    private transient Object _emptyValue;
    protected final NullValueProvider _nuller;

    protected abstract T _concat(T t, T t2);

    protected abstract T handleSingleElementUnwrapped(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    protected abstract PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nullValueProvider, Boolean bool);

    protected abstract T _constructEmpty();

    protected PrimitiveArrayDeserializers(Class<T> cls) {
        super((Class<?>) cls);
        this._unwrapSingle = null;
        this._nuller = null;
    }

    protected PrimitiveArrayDeserializers(PrimitiveArrayDeserializers<?> base, NullValueProvider nuller, Boolean unwrapSingle) {
        super(base._valueClass);
        this._unwrapSingle = unwrapSingle;
        this._nuller = nuller;
    }

    public static JsonDeserializer<?> forType(Class<?> rawType) {
        if (rawType == Integer.TYPE) {
            return IntDeser.instance;
        }
        if (rawType == Long.TYPE) {
            return LongDeser.instance;
        }
        if (rawType == Byte.TYPE) {
            return new ByteDeser();
        }
        if (rawType == Short.TYPE) {
            return new ShortDeser();
        }
        if (rawType == Float.TYPE) {
            return new FloatDeser();
        }
        if (rawType == Double.TYPE) {
            return new DoubleDeser();
        }
        if (rawType == Boolean.TYPE) {
            return new BooleanDeser();
        }
        if (rawType == Character.TYPE) {
            return new CharDeser();
        }
        throw new IllegalStateException();
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Boolean unwrapSingle = findFormatFeature(ctxt, property, this._valueClass, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        NullValueProvider nuller = null;
        Nulls nullStyle = findContentNullStyle(ctxt, property);
        if (nullStyle == Nulls.SKIP) {
            nuller = NullsConstantProvider.skipper();
        } else if (nullStyle == Nulls.FAIL) {
            if (property == null) {
                nuller = NullsFailProvider.constructForRootValue(ctxt.constructType(this._valueClass.getComponentType()));
            } else {
                nuller = NullsFailProvider.constructForProperty(property, property.getType().getContentType());
            }
        }
        if (Objects.equals(unwrapSingle, this._unwrapSingle) && nuller == this._nuller) {
            return this;
        }
        return withResolved(nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Array;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.TRUE;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        Object empty = this._emptyValue;
        if (empty == null) {
            Object _constructEmpty = _constructEmpty();
            empty = _constructEmpty;
            this._emptyValue = _constructEmpty;
        }
        return empty;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser p, DeserializationContext ctxt, T existing) throws IOException {
        T newValue = deserialize(p, ctxt);
        if (existing == null) {
            return newValue;
        }
        int len = Array.getLength(existing);
        if (len == 0) {
            return newValue;
        }
        return _concat(existing, newValue);
    }

    protected T handleNonArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.hasToken(JsonToken.VALUE_STRING)) {
            return _deserializeFromString(jsonParser, deserializationContext);
        }
        if (this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && deserializationContext.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY))) {
            return handleSingleElementUnwrapped(jsonParser, deserializationContext);
        }
        return (T) deserializationContext.handleUnexpectedToken(this._valueClass, jsonParser);
    }

    protected void _failOnNull(DeserializationContext ctxt) throws IOException {
        throw InvalidNullException.from(ctxt, (PropertyName) null, ctxt.constructType(this._valueClass));
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$CharDeser.class */
    static final class CharDeser extends PrimitiveArrayDeserializers<char[]> {
        private static final long serialVersionUID = 1;

        public CharDeser() {
            super(char[].class);
        }

        protected CharDeser(CharDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public char[] _constructEmpty() {
            return new char[0];
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public char[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String str;
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                char[] buffer = p.getTextCharacters();
                int offset = p.getTextOffset();
                int len = p.getTextLength();
                char[] result = new char[len];
                System.arraycopy(buffer, offset, result, 0, len);
                return result;
            }
            if (p.isExpectedStartArrayToken()) {
                StringBuilder sb = new StringBuilder(64);
                while (true) {
                    JsonToken t = p.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_STRING) {
                            str = p.getText();
                        } else if (t == JsonToken.VALUE_NULL) {
                            if (this._nuller != null) {
                                this._nuller.getNullValue(ctxt);
                            } else {
                                _verifyNullForPrimitive(ctxt);
                                str = "��";
                            }
                        } else {
                            CharSequence cs = (CharSequence) ctxt.handleUnexpectedToken(Character.TYPE, p);
                            str = cs.toString();
                        }
                        if (str.length() != 1) {
                            ctxt.reportInputMismatch(this, "Cannot convert a JSON String of length %d into a char element of char array", Integer.valueOf(str.length()));
                        }
                        sb.append(str.charAt(0));
                    } else {
                        return sb.toString().toCharArray();
                    }
                }
            } else {
                if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
                    Object ob = p.getEmbeddedObject();
                    if (ob == null) {
                        return null;
                    }
                    if (ob instanceof char[]) {
                        return (char[]) ob;
                    }
                    if (ob instanceof String) {
                        return ((String) ob).toCharArray();
                    }
                    if (ob instanceof byte[]) {
                        return Base64Variants.getDefaultVariant().encode((byte[]) ob, false).toCharArray();
                    }
                }
                return (char[]) ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public char[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return (char[]) ctxt.handleUnexpectedToken(this._valueClass, p);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public char[] _concat(char[] oldValue, char[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            char[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$BooleanDeser.class */
    static final class BooleanDeser extends PrimitiveArrayDeserializers<boolean[]> {
        private static final long serialVersionUID = 1;

        public BooleanDeser() {
            super(boolean[].class);
        }

        protected BooleanDeser(BooleanDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new BooleanDeser(this, nuller, unwrapSingle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public boolean[] _constructEmpty() {
            return new boolean[0];
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v19 */
        /* JADX WARN: Type inference failed for: r0v22, types: [boolean[]] */
        /* JADX WARN: Type inference failed for: r0v7, types: [boolean[]] */
        /* JADX WARN: Type inference failed for: r1v5 */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public boolean[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            boolean value;
            if (!p.isExpectedStartArrayToken()) {
                return handleNonArray(p, ctxt);
            }
            ArrayBuilders.BooleanBuilder builder = ctxt.getArrayBuilders().getBooleanBuilder();
            T resetAndStart = builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    JsonToken t = p.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_TRUE) {
                            value = true;
                        } else if (t == JsonToken.VALUE_FALSE) {
                            value = false;
                        } else if (t == JsonToken.VALUE_NULL) {
                            if (this._nuller != null) {
                                this._nuller.getNullValue(ctxt);
                            } else {
                                _verifyNullForPrimitive(ctxt);
                                value = false;
                            }
                        } else {
                            value = _parseBooleanPrimitive(p, ctxt);
                        }
                        if (ix >= resetAndStart.length) {
                            resetAndStart = builder.appendCompletedChunk(resetAndStart, ix);
                            ix = 0;
                        }
                        int i = ix;
                        ix++;
                        resetAndStart[i] = value;
                    } else {
                        return builder.completeAndClearBuffer(resetAndStart, ix);
                    }
                } catch (Exception e) {
                    throw JsonMappingException.wrapWithPath(e, resetAndStart, builder.bufferedSize() + ix);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public boolean[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new boolean[]{_parseBooleanPrimitive(p, ctxt)};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public boolean[] _concat(boolean[] oldValue, boolean[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            boolean[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$ByteDeser.class */
    static final class ByteDeser extends PrimitiveArrayDeserializers<byte[]> {
        private static final long serialVersionUID = 1;

        public ByteDeser() {
            super(byte[].class);
        }

        protected ByteDeser(ByteDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new ByteDeser(this, nuller, unwrapSingle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public byte[] _constructEmpty() {
            return new byte[0];
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers, com.fasterxml.jackson.databind.JsonDeserializer
        public LogicalType logicalType() {
            return LogicalType.Binary;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v21, types: [byte[]] */
        /* JADX WARN: Type inference failed for: r0v34 */
        /* JADX WARN: Type inference failed for: r0v37, types: [byte[]] */
        /* JADX WARN: Type inference failed for: r1v11 */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            byte value;
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_STRING) {
                try {
                    return p.getBinaryValue(ctxt.getBase64Variant());
                } catch (StreamReadException | DatabindException e) {
                    String msg = e.getOriginalMessage();
                    if (msg.contains("base64")) {
                        return (byte[]) ctxt.handleWeirdStringValue(byte[].class, p.getText(), msg, new Object[0]);
                    }
                }
            }
            if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
                Object ob = p.getEmbeddedObject();
                if (ob == null) {
                    return null;
                }
                if (ob instanceof byte[]) {
                    return (byte[]) ob;
                }
            }
            if (!p.isExpectedStartArrayToken()) {
                return handleNonArray(p, ctxt);
            }
            ArrayBuilders.ByteBuilder builder = ctxt.getArrayBuilders().getByteBuilder();
            T resetAndStart = builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    JsonToken t2 = p.nextToken();
                    if (t2 != JsonToken.END_ARRAY) {
                        if (t2 == JsonToken.VALUE_NUMBER_INT) {
                            value = p.getByteValue();
                        } else if (t2 == JsonToken.VALUE_NULL) {
                            if (this._nuller != null) {
                                this._nuller.getNullValue(ctxt);
                            } else {
                                _verifyNullForPrimitive(ctxt);
                                value = 0;
                            }
                        } else {
                            value = _parseBytePrimitive(p, ctxt);
                        }
                        if (ix >= resetAndStart.length) {
                            resetAndStart = builder.appendCompletedChunk(resetAndStart, ix);
                            ix = 0;
                        }
                        int i = ix;
                        ix++;
                        resetAndStart[i] = value;
                    } else {
                        return builder.completeAndClearBuffer(resetAndStart, ix);
                    }
                } catch (Exception e2) {
                    throw JsonMappingException.wrapWithPath(e2, resetAndStart, builder.bufferedSize() + ix);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public byte[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            byte value;
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                value = p.getByteValue();
            } else {
                if (t == JsonToken.VALUE_NULL) {
                    if (this._nuller != null) {
                        this._nuller.getNullValue(ctxt);
                        return (byte[]) getEmptyValue(ctxt);
                    }
                    _verifyNullForPrimitive(ctxt);
                    return null;
                }
                Number n = (Number) ctxt.handleUnexpectedToken(this._valueClass.getComponentType(), p);
                value = n.byteValue();
            }
            return new byte[]{value};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public byte[] _concat(byte[] oldValue, byte[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            byte[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$ShortDeser.class */
    static final class ShortDeser extends PrimitiveArrayDeserializers<short[]> {
        private static final long serialVersionUID = 1;

        public ShortDeser() {
            super(short[].class);
        }

        protected ShortDeser(ShortDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new ShortDeser(this, nuller, unwrapSingle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public short[] _constructEmpty() {
            return new short[0];
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v23, types: [short[]] */
        /* JADX WARN: Type inference failed for: r0v7, types: [short[]] */
        /* JADX WARN: Type inference failed for: r1v6 */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public short[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            short value;
            if (!p.isExpectedStartArrayToken()) {
                return handleNonArray(p, ctxt);
            }
            ArrayBuilders.ShortBuilder builder = ctxt.getArrayBuilders().getShortBuilder();
            T resetAndStart = builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    JsonToken t = p.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NULL) {
                            if (this._nuller != null) {
                                this._nuller.getNullValue(ctxt);
                            } else {
                                _verifyNullForPrimitive(ctxt);
                                value = 0;
                            }
                        } else {
                            value = _parseShortPrimitive(p, ctxt);
                        }
                        if (ix >= resetAndStart.length) {
                            resetAndStart = builder.appendCompletedChunk(resetAndStart, ix);
                            ix = 0;
                        }
                        int i = ix;
                        ix++;
                        resetAndStart[i] = value;
                    } else {
                        return builder.completeAndClearBuffer(resetAndStart, ix);
                    }
                } catch (Exception e) {
                    throw JsonMappingException.wrapWithPath(e, resetAndStart, builder.bufferedSize() + ix);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public short[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new short[]{_parseShortPrimitive(p, ctxt)};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public short[] _concat(short[] oldValue, short[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            short[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$IntDeser.class */
    static final class IntDeser extends PrimitiveArrayDeserializers<int[]> {
        private static final long serialVersionUID = 1;
        public static final IntDeser instance = new IntDeser();

        public IntDeser() {
            super(int[].class);
        }

        protected IntDeser(IntDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new IntDeser(this, nuller, unwrapSingle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public int[] _constructEmpty() {
            return new int[0];
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v20 */
        /* JADX WARN: Type inference failed for: r0v23, types: [int[]] */
        /* JADX WARN: Type inference failed for: r0v7, types: [int[]] */
        /* JADX WARN: Type inference failed for: r1v5 */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public int[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            int value;
            if (!p.isExpectedStartArrayToken()) {
                return handleNonArray(p, ctxt);
            }
            ArrayBuilders.IntBuilder builder = ctxt.getArrayBuilders().getIntBuilder();
            T resetAndStart = builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    JsonToken t = p.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NUMBER_INT) {
                            value = p.getIntValue();
                        } else if (t == JsonToken.VALUE_NULL) {
                            if (this._nuller != null) {
                                this._nuller.getNullValue(ctxt);
                            } else {
                                _verifyNullForPrimitive(ctxt);
                                value = 0;
                            }
                        } else {
                            value = _parseIntPrimitive(p, ctxt);
                        }
                        if (ix >= resetAndStart.length) {
                            resetAndStart = builder.appendCompletedChunk(resetAndStart, ix);
                            ix = 0;
                        }
                        int i = ix;
                        ix++;
                        resetAndStart[i] = value;
                    } else {
                        return builder.completeAndClearBuffer(resetAndStart, ix);
                    }
                } catch (Exception e) {
                    throw JsonMappingException.wrapWithPath(e, resetAndStart, builder.bufferedSize() + ix);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public int[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new int[]{_parseIntPrimitive(p, ctxt)};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public int[] _concat(int[] oldValue, int[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            int[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$LongDeser.class */
    static final class LongDeser extends PrimitiveArrayDeserializers<long[]> {
        private static final long serialVersionUID = 1;
        public static final LongDeser instance = new LongDeser();

        public LongDeser() {
            super(long[].class);
        }

        protected LongDeser(LongDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new LongDeser(this, nuller, unwrapSingle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public long[] _constructEmpty() {
            return new long[0];
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v20 */
        /* JADX WARN: Type inference failed for: r0v23, types: [long[]] */
        /* JADX WARN: Type inference failed for: r0v7, types: [long[]] */
        /* JADX WARN: Type inference failed for: r1v5 */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public long[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            long value;
            if (!p.isExpectedStartArrayToken()) {
                return handleNonArray(p, ctxt);
            }
            ArrayBuilders.LongBuilder builder = ctxt.getArrayBuilders().getLongBuilder();
            T resetAndStart = builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    JsonToken t = p.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NUMBER_INT) {
                            value = p.getLongValue();
                        } else if (t == JsonToken.VALUE_NULL) {
                            if (this._nuller != null) {
                                this._nuller.getNullValue(ctxt);
                            } else {
                                _verifyNullForPrimitive(ctxt);
                                value = 0;
                            }
                        } else {
                            value = _parseLongPrimitive(p, ctxt);
                        }
                        if (ix >= resetAndStart.length) {
                            resetAndStart = builder.appendCompletedChunk(resetAndStart, ix);
                            ix = 0;
                        }
                        int i = ix;
                        ix++;
                        resetAndStart[i] = value;
                    } else {
                        return builder.completeAndClearBuffer(resetAndStart, ix);
                    }
                } catch (Exception e) {
                    throw JsonMappingException.wrapWithPath(e, resetAndStart, builder.bufferedSize() + ix);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public long[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new long[]{_parseLongPrimitive(p, ctxt)};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public long[] _concat(long[] oldValue, long[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            long[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$FloatDeser.class */
    static final class FloatDeser extends PrimitiveArrayDeserializers<float[]> {
        private static final long serialVersionUID = 1;

        public FloatDeser() {
            super(float[].class);
        }

        protected FloatDeser(FloatDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new FloatDeser(this, nuller, unwrapSingle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public float[] _constructEmpty() {
            return new float[0];
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v20 */
        /* JADX WARN: Type inference failed for: r0v23, types: [float[]] */
        /* JADX WARN: Type inference failed for: r0v7, types: [float[]] */
        /* JADX WARN: Type inference failed for: r1v6 */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public float[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return handleNonArray(p, ctxt);
            }
            ArrayBuilders.FloatBuilder builder = ctxt.getArrayBuilders().getFloatBuilder();
            T resetAndStart = builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    JsonToken t = p.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NULL && this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                        } else {
                            float value = _parseFloatPrimitive(p, ctxt);
                            if (ix >= resetAndStart.length) {
                                resetAndStart = builder.appendCompletedChunk(resetAndStart, ix);
                                ix = 0;
                            }
                            int i = ix;
                            ix++;
                            resetAndStart[i] = value;
                        }
                    } else {
                        return builder.completeAndClearBuffer(resetAndStart, ix);
                    }
                } catch (Exception e) {
                    throw JsonMappingException.wrapWithPath(e, resetAndStart, builder.bufferedSize() + ix);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public float[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new float[]{_parseFloatPrimitive(p, ctxt)};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public float[] _concat(float[] oldValue, float[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            float[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }

    @JacksonStdImpl
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/PrimitiveArrayDeserializers$DoubleDeser.class */
    static final class DoubleDeser extends PrimitiveArrayDeserializers<double[]> {
        private static final long serialVersionUID = 1;

        public DoubleDeser() {
            super(double[].class);
        }

        protected DoubleDeser(DoubleDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
            return new DoubleDeser(this, nuller, unwrapSingle);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public double[] _constructEmpty() {
            return new double[0];
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v20 */
        /* JADX WARN: Type inference failed for: r0v23, types: [double[]] */
        /* JADX WARN: Type inference failed for: r0v7, types: [double[]] */
        /* JADX WARN: Type inference failed for: r1v6 */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public double[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return handleNonArray(p, ctxt);
            }
            ArrayBuilders.DoubleBuilder builder = ctxt.getArrayBuilders().getDoubleBuilder();
            T resetAndStart = builder.resetAndStart();
            int ix = 0;
            while (true) {
                try {
                    JsonToken t = p.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        if (t == JsonToken.VALUE_NULL && this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                        } else {
                            double value = _parseDoublePrimitive(p, ctxt);
                            if (ix >= resetAndStart.length) {
                                resetAndStart = builder.appendCompletedChunk(resetAndStart, ix);
                                ix = 0;
                            }
                            int i = ix;
                            ix++;
                            resetAndStart[i] = value;
                        }
                    } else {
                        return builder.completeAndClearBuffer(resetAndStart, ix);
                    }
                } catch (Exception e) {
                    throw JsonMappingException.wrapWithPath(e, resetAndStart, builder.bufferedSize() + ix);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public double[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new double[]{_parseDoublePrimitive(p, ctxt)};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers
        public double[] _concat(double[] oldValue, double[] newValue) {
            int len1 = oldValue.length;
            int len2 = newValue.length;
            double[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }
}
