package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

@JacksonStdImpl
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/ObjectArrayDeserializer.class */
public class ObjectArrayDeserializer extends ContainerDeserializerBase<Object[]> implements ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected final boolean _untyped;
    protected final Class<?> _elementClass;
    protected JsonDeserializer<Object> _elementDeserializer;
    protected final TypeDeserializer _elementTypeDeserializer;
    protected final Object[] _emptyValue;

    public ObjectArrayDeserializer(JavaType arrayType0, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser) {
        super(arrayType0, (NullValueProvider) null, (Boolean) null);
        ArrayType arrayType = (ArrayType) arrayType0;
        this._elementClass = arrayType.getContentType().getRawClass();
        this._untyped = this._elementClass == Object.class;
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
        this._emptyValue = arrayType.getEmptyArray();
    }

    protected ObjectArrayDeserializer(ObjectArrayDeserializer base, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser, NullValueProvider nuller, Boolean unwrapSingle) {
        super(base, nuller, unwrapSingle);
        this._elementClass = base._elementClass;
        this._untyped = base._untyped;
        this._emptyValue = base._emptyValue;
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
    }

    public ObjectArrayDeserializer withDeserializer(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser) {
        return withResolved(elemTypeDeser, elemDeser, this._nullProvider, this._unwrapSingle);
    }

    public ObjectArrayDeserializer withResolved(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser, NullValueProvider nuller, Boolean unwrapSingle) {
        if (Objects.equals(unwrapSingle, this._unwrapSingle) && nuller == this._nullProvider && elemDeser == this._elementDeserializer && elemTypeDeser == this._elementTypeDeserializer) {
            return this;
        }
        return new ObjectArrayDeserializer(this, elemDeser, elemTypeDeser, nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return this._elementDeserializer == null && this._elementTypeDeserializer == null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Array;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> valueDeser;
        JsonDeserializer<?> valueDeser2 = this._elementDeserializer;
        Boolean unwrapSingle = findFormatFeature(ctxt, property, this._containerType.getRawClass(), JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonDeserializer<?> valueDeser3 = findConvertingContentDeserializer(ctxt, property, valueDeser2);
        JavaType vt = this._containerType.getContentType();
        if (valueDeser3 == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser3, property, vt);
        }
        TypeDeserializer elemTypeDeser = this._elementTypeDeserializer;
        if (elemTypeDeser != null) {
            elemTypeDeser = elemTypeDeser.forProperty(property);
        }
        NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
        return withResolved(elemTypeDeser, valueDeser, nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._elementDeserializer;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return this._emptyValue;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object[] result;
        Object value;
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt);
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        TypeDeserializer typeDeser = this._elementTypeDeserializer;
        while (true) {
            try {
                JsonToken t = p.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    break;
                }
                if (t == JsonToken.VALUE_NULL) {
                    if (!this._skipNullValues) {
                        value = this._nullProvider.getNullValue(ctxt);
                    }
                } else if (typeDeser == null) {
                    value = this._elementDeserializer.deserialize(p, ctxt);
                } else {
                    value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                int i = ix;
                ix++;
                chunk[i] = value;
            } catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
            }
        }
        if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
        } else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
        }
        ctxt.returnObjectBuffer(buffer);
        return result;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object[] deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return (Object[]) typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object[] deserialize(JsonParser p, DeserializationContext ctxt, Object[] intoValue) throws IOException {
        Object[] result;
        Object value;
        if (!p.isExpectedStartArrayToken()) {
            Object[] arr = handleNonArray(p, ctxt);
            if (arr == null) {
                return intoValue;
            }
            int offset = intoValue.length;
            Object[] result2 = Arrays.copyOf(intoValue, offset + arr.length);
            System.arraycopy(arr, 0, result2, offset, arr.length);
            return result2;
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        int ix = intoValue.length;
        Object[] chunk = buffer.resetAndStart(intoValue, ix);
        TypeDeserializer typeDeser = this._elementTypeDeserializer;
        while (true) {
            try {
                JsonToken t = p.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    break;
                }
                if (t == JsonToken.VALUE_NULL) {
                    if (!this._skipNullValues) {
                        value = this._nullProvider.getNullValue(ctxt);
                    }
                } else if (typeDeser == null) {
                    value = this._elementDeserializer.deserialize(p, ctxt);
                } else {
                    value = this._elementDeserializer.deserializeWithType(p, ctxt, typeDeser);
                }
                if (ix >= chunk.length) {
                    chunk = buffer.appendCompletedChunk(chunk);
                    ix = 0;
                }
                int i = ix;
                ix++;
                chunk[i] = value;
            } catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
            }
        }
        if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
        } else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
        }
        ctxt.returnObjectBuffer(buffer);
        return result;
    }

    protected Byte[] deserializeFromBase64(JsonParser p, DeserializationContext ctxt) throws IOException {
        byte[] b = p.getBinaryValue(ctxt.getBase64Variant());
        Byte[] result = new Byte[b.length];
        int len = b.length;
        for (int i = 0; i < len; i++) {
            result[i] = Byte.valueOf(b[i]);
        }
        return result;
    }

    protected Object[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object value;
        CoercionAction act;
        Object[] result;
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                if (this._elementClass == Byte.class) {
                    return deserializeFromBase64(p, ctxt);
                }
                return _deserializeFromString(p, ctxt);
            }
            return (Object[]) ctxt.handleUnexpectedToken(this._containerType, p);
        }
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (this._skipNullValues) {
                return this._emptyValue;
            }
            value = this._nullProvider.getNullValue(ctxt);
        } else {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                String textValue = p.getText();
                if (textValue.isEmpty()) {
                    CoercionAction act2 = ctxt.findCoercionAction(logicalType(), handledType(), CoercionInputShape.EmptyString);
                    if (act2 != CoercionAction.Fail) {
                        return (Object[]) _deserializeFromEmptyString(p, ctxt, act2, handledType(), "empty String (\"\")");
                    }
                } else if (_isBlank(textValue) && (act = ctxt.findCoercionFromBlankString(logicalType(), handledType(), CoercionAction.Fail)) != CoercionAction.Fail) {
                    return (Object[]) _deserializeFromEmptyString(p, ctxt, act, handledType(), "blank String (all whitespace)");
                }
            }
            if (this._elementTypeDeserializer == null) {
                value = this._elementDeserializer.deserialize(p, ctxt);
            } else {
                value = this._elementDeserializer.deserializeWithType(p, ctxt, this._elementTypeDeserializer);
            }
        }
        if (this._untyped) {
            result = new Object[1];
        } else {
            result = (Object[]) Array.newInstance(this._elementClass, 1);
        }
        result[0] = value;
        return result;
    }
}
