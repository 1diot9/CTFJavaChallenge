package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JacksonStdImpl
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/UntypedObjectDeserializer.class */
public class UntypedObjectDeserializer extends StdDeserializer<Object> implements ResolvableDeserializer, ContextualDeserializer {
    private static final long serialVersionUID = 1;
    protected static final Object[] NO_OBJECTS = new Object[0];
    protected JsonDeserializer<Object> _mapDeserializer;
    protected JsonDeserializer<Object> _listDeserializer;
    protected JsonDeserializer<Object> _stringDeserializer;
    protected JsonDeserializer<Object> _numberDeserializer;
    protected JavaType _listType;
    protected JavaType _mapType;
    protected final boolean _nonMerging;

    @Deprecated
    public UntypedObjectDeserializer() {
        this((JavaType) null, (JavaType) null);
    }

    public UntypedObjectDeserializer(JavaType listType, JavaType mapType) {
        super((Class<?>) Object.class);
        this._listType = listType;
        this._mapType = mapType;
        this._nonMerging = false;
    }

    public UntypedObjectDeserializer(UntypedObjectDeserializer base, JsonDeserializer<?> mapDeser, JsonDeserializer<?> listDeser, JsonDeserializer<?> stringDeser, JsonDeserializer<?> numberDeser) {
        super((Class<?>) Object.class);
        this._mapDeserializer = mapDeser;
        this._listDeserializer = listDeser;
        this._stringDeserializer = stringDeser;
        this._numberDeserializer = numberDeser;
        this._listType = base._listType;
        this._mapType = base._mapType;
        this._nonMerging = base._nonMerging;
    }

    protected UntypedObjectDeserializer(UntypedObjectDeserializer base, boolean nonMerging) {
        super((Class<?>) Object.class);
        this._mapDeserializer = base._mapDeserializer;
        this._listDeserializer = base._listDeserializer;
        this._stringDeserializer = base._stringDeserializer;
        this._numberDeserializer = base._numberDeserializer;
        this._listType = base._listType;
        this._mapType = base._mapType;
        this._nonMerging = nonMerging;
    }

    @Override // com.fasterxml.jackson.databind.deser.ResolvableDeserializer
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        JavaType obType = ctxt.constructType(Object.class);
        JavaType stringType = ctxt.constructType(String.class);
        TypeFactory tf = ctxt.getTypeFactory();
        if (this._listType == null) {
            this._listDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructCollectionType(List.class, obType)));
        } else {
            this._listDeserializer = _findCustomDeser(ctxt, this._listType);
        }
        if (this._mapType == null) {
            this._mapDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructMapType(Map.class, stringType, obType)));
        } else {
            this._mapDeserializer = _findCustomDeser(ctxt, this._mapType);
        }
        this._stringDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, stringType));
        this._numberDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructType(Number.class)));
        JavaType unknown = TypeFactory.unknownType();
        this._mapDeserializer = ctxt.handleSecondaryContextualization(this._mapDeserializer, null, unknown);
        this._listDeserializer = ctxt.handleSecondaryContextualization(this._listDeserializer, null, unknown);
        this._stringDeserializer = ctxt.handleSecondaryContextualization(this._stringDeserializer, null, unknown);
        this._numberDeserializer = ctxt.handleSecondaryContextualization(this._numberDeserializer, null, unknown);
    }

    protected JsonDeserializer<Object> _findCustomDeser(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        return ctxt.findNonContextualValueDeserializer(type);
    }

    protected JsonDeserializer<Object> _clearIfStdImpl(JsonDeserializer<Object> deser) {
        if (ClassUtil.isJacksonStdImpl(deser)) {
            return null;
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        boolean preventMerge = property == null && Boolean.FALSE.equals(ctxt.getConfig().getDefaultMergeable(Object.class));
        if (this._stringDeserializer == null && this._numberDeserializer == null && this._mapDeserializer == null && this._listDeserializer == null && getClass() == UntypedObjectDeserializer.class) {
            return UntypedObjectDeserializerNR.instance(preventMerge);
        }
        if (preventMerge != this._nonMerging) {
            return new UntypedObjectDeserializer(this, preventMerge);
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentTokenId()) {
            case 1:
            case 2:
            case 5:
                if (this._mapDeserializer != null) {
                    return this._mapDeserializer.deserialize(p, ctxt);
                }
                return mapObject(p, ctxt);
            case 3:
                if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                    return mapArrayToArray(p, ctxt);
                }
                if (this._listDeserializer != null) {
                    return this._listDeserializer.deserialize(p, ctxt);
                }
                return mapArray(p, ctxt);
            case 4:
            default:
                return ctxt.handleUnexpectedToken(Object.class, p);
            case 6:
                if (this._stringDeserializer != null) {
                    return this._stringDeserializer.deserialize(p, ctxt);
                }
                return p.getText();
            case 7:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return _coerceIntegral(p, ctxt);
                }
                return p.getNumberValue();
            case 8:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return p.getNumberValue();
            case 9:
                return Boolean.TRUE;
            case 10:
                return Boolean.FALSE;
            case 11:
                return null;
            case 12:
                return p.getEmbeddedObject();
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        switch (p.currentTokenId()) {
            case 1:
            case 3:
            case 5:
                return typeDeserializer.deserializeTypedFromAny(p, ctxt);
            case 2:
            case 4:
            default:
                return ctxt.handleUnexpectedToken(Object.class, p);
            case 6:
                if (this._stringDeserializer != null) {
                    return this._stringDeserializer.deserialize(p, ctxt);
                }
                return p.getText();
            case 7:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return _coerceIntegral(p, ctxt);
                }
                return p.getNumberValue();
            case 8:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return p.getNumberValue();
            case 9:
                return Boolean.TRUE;
            case 10:
                return Boolean.FALSE;
            case 11:
                return null;
            case 12:
                return p.getEmbeddedObject();
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
        if (this._nonMerging) {
            return deserialize(p, ctxt);
        }
        switch (p.currentTokenId()) {
            case 1:
            case 2:
            case 5:
                if (this._mapDeserializer != null) {
                    return this._mapDeserializer.deserialize(p, ctxt, intoValue);
                }
                if (intoValue instanceof Map) {
                    return mapObject(p, ctxt, (Map) intoValue);
                }
                return mapObject(p, ctxt);
            case 3:
                if (this._listDeserializer != null) {
                    return this._listDeserializer.deserialize(p, ctxt, intoValue);
                }
                if (intoValue instanceof Collection) {
                    return mapArray(p, ctxt, (Collection) intoValue);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                    return mapArrayToArray(p, ctxt);
                }
                return mapArray(p, ctxt);
            case 4:
            default:
                return deserialize(p, ctxt);
            case 6:
                if (this._stringDeserializer != null) {
                    return this._stringDeserializer.deserialize(p, ctxt, intoValue);
                }
                return p.getText();
            case 7:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt, intoValue);
                }
                if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                    return _coerceIntegral(p, ctxt);
                }
                return p.getNumberValue();
            case 8:
                if (this._numberDeserializer != null) {
                    return this._numberDeserializer.deserialize(p, ctxt, intoValue);
                }
                if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return p.getDecimalValue();
                }
                return p.getNumberValue();
            case 9:
                return Boolean.TRUE;
            case 10:
                return Boolean.FALSE;
            case 11:
                return null;
            case 12:
                return p.getEmbeddedObject();
        }
    }

    protected Object mapArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return new ArrayList(2);
        }
        Object value = deserialize(p, ctxt);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            ArrayList<Object> l = new ArrayList<>(2);
            l.add(value);
            return l;
        }
        Object value2 = deserialize(p, ctxt);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            ArrayList<Object> l2 = new ArrayList<>(2);
            l2.add(value);
            l2.add(value2);
            return l2;
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0 + 1;
        values[0] = value;
        int ptr2 = ptr + 1;
        values[ptr] = value2;
        int totalSize = ptr2;
        do {
            Object value3 = deserialize(p, ctxt);
            totalSize++;
            if (ptr2 >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr2 = 0;
            }
            int i = ptr2;
            ptr2++;
            values[i] = value3;
        } while (p.nextToken() != JsonToken.END_ARRAY);
        ArrayList<Object> result = new ArrayList<>(totalSize);
        buffer.completeAndClearBuffer(values, ptr2, result);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }

    protected Object mapArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
        while (p.nextToken() != JsonToken.END_ARRAY) {
            result.add(deserialize(p, ctxt));
        }
        return result;
    }

    protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        String key1;
        String nextFieldName;
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            key1 = p.nextFieldName();
        } else if (t == JsonToken.FIELD_NAME) {
            key1 = p.currentName();
        } else {
            if (t != JsonToken.END_OBJECT) {
                return ctxt.handleUnexpectedToken(handledType(), p);
            }
            key1 = null;
        }
        if (key1 == null) {
            return new LinkedHashMap(2);
        }
        p.nextToken();
        Object value1 = deserialize(p, ctxt);
        String key2 = p.nextFieldName();
        if (key2 == null) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<>(2);
            result.put(key1, value1);
            return result;
        }
        p.nextToken();
        Object value2 = deserialize(p, ctxt);
        String key = p.nextFieldName();
        if (key == null) {
            LinkedHashMap<String, Object> result2 = new LinkedHashMap<>(4);
            result2.put(key1, value1);
            if (result2.put(key2, value2) != null) {
                return _mapObjectWithDups(p, ctxt, result2, key1, value1, value2, key);
            }
            return result2;
        }
        LinkedHashMap<String, Object> result3 = new LinkedHashMap<>();
        result3.put(key1, value1);
        if (result3.put(key2, value2) != null) {
            return _mapObjectWithDups(p, ctxt, result3, key1, value1, value2, key);
        }
        do {
            p.nextToken();
            Object newValue = deserialize(p, ctxt);
            Object oldValue = result3.put(key, newValue);
            if (oldValue != null) {
                return _mapObjectWithDups(p, ctxt, result3, key, oldValue, newValue, p.nextFieldName());
            }
            nextFieldName = p.nextFieldName();
            key = nextFieldName;
        } while (nextFieldName != null);
        return result3;
    }

    protected Object _mapObjectWithDups(JsonParser p, DeserializationContext ctxt, Map<String, Object> result, String key, Object oldValue, Object newValue, String nextKey) throws IOException {
        boolean squashDups = ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES);
        if (squashDups) {
            _squashDups(result, key, oldValue, newValue);
        }
        while (nextKey != null) {
            p.nextToken();
            Object newValue2 = deserialize(p, ctxt);
            Object oldValue2 = result.put(nextKey, newValue2);
            if (oldValue2 != null && squashDups) {
                _squashDups(result, key, oldValue2, newValue2);
            }
            nextKey = p.nextFieldName();
        }
        return result;
    }

    private void _squashDups(Map<String, Object> result, String key, Object oldValue, Object newValue) {
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

    protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return NO_OBJECTS;
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0;
        do {
            Object value = deserialize(p, ctxt);
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            int i = ptr;
            ptr++;
            values[i] = value;
        } while (p.nextToken() != JsonToken.END_ARRAY);
        Object[] result = buffer.completeAndClearBuffer(values, ptr);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }

    protected Object mapObject(JsonParser p, DeserializationContext ctxt, Map<Object, Object> m) throws IOException {
        Object newV;
        String nextFieldName;
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        if (t == JsonToken.END_OBJECT) {
            return m;
        }
        String key = p.currentName();
        do {
            p.nextToken();
            Object old = m.get(key);
            if (old != null) {
                newV = deserialize(p, ctxt, old);
            } else {
                newV = deserialize(p, ctxt);
            }
            if (newV != old) {
                m.put(key, newV);
            }
            nextFieldName = p.nextFieldName();
            key = nextFieldName;
        } while (nextFieldName != null);
        return m;
    }

    @JacksonStdImpl
    @Deprecated
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/UntypedObjectDeserializer$Vanilla.class */
    public static class Vanilla extends StdDeserializer<Object> {
        private static final long serialVersionUID = 1;
        public static final Vanilla std = new Vanilla();
        protected final boolean _nonMerging;

        public Vanilla() {
            this(false);
        }

        protected Vanilla(boolean nonMerging) {
            super((Class<?>) Object.class);
            this._nonMerging = nonMerging;
        }

        public static Vanilla instance(boolean nonMerging) {
            if (nonMerging) {
                return new Vanilla(true);
            }
            return std;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public LogicalType logicalType() {
            return LogicalType.Untyped;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Boolean supportsUpdate(DeserializationConfig config) {
            if (this._nonMerging) {
                return Boolean.FALSE;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.currentTokenId()) {
                case 1:
                    JsonToken t = p.nextToken();
                    if (t == JsonToken.END_OBJECT) {
                        return new LinkedHashMap(2);
                    }
                    break;
                case 2:
                    return new LinkedHashMap(2);
                case 3:
                    JsonToken t2 = p.nextToken();
                    if (t2 == JsonToken.END_ARRAY) {
                        if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                            return UntypedObjectDeserializer.NO_OBJECTS;
                        }
                        return new ArrayList(2);
                    }
                    if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
                        return mapArrayToArray(p, ctxt);
                    }
                    return mapArray(p, ctxt);
                case 4:
                default:
                    return ctxt.handleUnexpectedToken(Object.class, p);
                case 5:
                    break;
                case 6:
                    return p.getText();
                case 7:
                    if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                        return _coerceIntegral(p, ctxt);
                    }
                    return p.getNumberValue();
                case 8:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return p.getDecimalValue();
                    }
                    return p.getNumberValue();
                case 9:
                    return Boolean.TRUE;
                case 10:
                    return Boolean.FALSE;
                case 11:
                    return null;
                case 12:
                    return p.getEmbeddedObject();
            }
            return mapObject(p, ctxt);
        }

        @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            switch (p.currentTokenId()) {
                case 1:
                case 3:
                case 5:
                    return typeDeserializer.deserializeTypedFromAny(p, ctxt);
                case 2:
                case 4:
                default:
                    return ctxt.handleUnexpectedToken(Object.class, p);
                case 6:
                    return p.getText();
                case 7:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                        return p.getBigIntegerValue();
                    }
                    return p.getNumberValue();
                case 8:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return p.getDecimalValue();
                    }
                    return p.getNumberValue();
                case 9:
                    return Boolean.TRUE;
                case 10:
                    return Boolean.FALSE;
                case 11:
                    return null;
                case 12:
                    return p.getEmbeddedObject();
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Failed to find 'out' block for switch in B:7:0x0012. Please report as an issue. */
        /* JADX WARN: Removed duplicated region for block: B:16:0x004d  */
        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.lang.Object deserialize(com.fasterxml.jackson.core.JsonParser r6, com.fasterxml.jackson.databind.DeserializationContext r7, java.lang.Object r8) throws java.io.IOException {
            /*
                r5 = this;
                r0 = r5
                boolean r0 = r0._nonMerging
                if (r0 == 0) goto Le
                r0 = r5
                r1 = r6
                r2 = r7
                java.lang.Object r0 = r0.deserialize(r1, r2)
                return r0
            Le:
                r0 = r6
                int r0 = r0.currentTokenId()
                switch(r0) {
                    case 1: goto L36;
                    case 2: goto L34;
                    case 3: goto La2;
                    case 4: goto L34;
                    case 5: goto L46;
                    default: goto Ld9;
                }
            L34:
                r0 = r8
                return r0
            L36:
                r0 = r6
                com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
                r9 = r0
                r0 = r9
                com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.END_OBJECT
                if (r0 != r1) goto L46
                r0 = r8
                return r0
            L46:
                r0 = r8
                boolean r0 = r0 instanceof java.util.Map
                if (r0 == 0) goto Ld9
                r0 = r8
                java.util.Map r0 = (java.util.Map) r0
                r9 = r0
                r0 = r6
                java.lang.String r0 = r0.currentName()
                r10 = r0
            L59:
                r0 = r6
                com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
                r0 = r9
                r1 = r10
                java.lang.Object r0 = r0.get(r1)
                r11 = r0
                r0 = r11
                if (r0 == 0) goto L7b
                r0 = r5
                r1 = r6
                r2 = r7
                r3 = r11
                java.lang.Object r0 = r0.deserialize(r1, r2, r3)
                r12 = r0
                goto L83
            L7b:
                r0 = r5
                r1 = r6
                r2 = r7
                java.lang.Object r0 = r0.deserialize(r1, r2)
                r12 = r0
            L83:
                r0 = r12
                r1 = r11
                if (r0 == r1) goto L96
                r0 = r9
                r1 = r10
                r2 = r12
                java.lang.Object r0 = r0.put(r1, r2)
            L96:
                r0 = r6
                java.lang.String r0 = r0.nextFieldName()
                r1 = r0
                r10 = r1
                if (r0 != 0) goto L59
                r0 = r8
                return r0
            La2:
                r0 = r6
                com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
                r9 = r0
                r0 = r9
                com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.END_ARRAY
                if (r0 != r1) goto Lb2
                r0 = r8
                return r0
            Lb2:
                r0 = r8
                boolean r0 = r0 instanceof java.util.Collection
                if (r0 == 0) goto Ld9
                r0 = r8
                java.util.Collection r0 = (java.util.Collection) r0
                r9 = r0
            Lbf:
                r0 = r9
                r1 = r5
                r2 = r6
                r3 = r7
                java.lang.Object r1 = r1.deserialize(r2, r3)
                boolean r0 = r0.add(r1)
                r0 = r6
                com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
                com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.END_ARRAY
                if (r0 != r1) goto Lbf
                r0 = r8
                return r0
            Ld9:
                r0 = r5
                r1 = r6
                r2 = r7
                java.lang.Object r0 = r0.deserialize(r1, r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer.Vanilla.deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, java.lang.Object):java.lang.Object");
        }

        protected Object mapArray(JsonParser p, DeserializationContext ctxt) throws IOException {
            Object value = deserialize(p, ctxt);
            if (p.nextToken() == JsonToken.END_ARRAY) {
                ArrayList<Object> l = new ArrayList<>(2);
                l.add(value);
                return l;
            }
            ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] values = buffer.resetAndStart();
            int ptr = 0 + 1;
            values[0] = value;
            int totalSize = ptr;
            do {
                Object value2 = deserialize(p, ctxt);
                totalSize++;
                if (ptr >= values.length) {
                    values = buffer.appendCompletedChunk(values);
                    ptr = 0;
                }
                int i = ptr;
                ptr++;
                values[i] = value2;
            } while (p.nextToken() != JsonToken.END_ARRAY);
            ArrayList<Object> result = new ArrayList<>(totalSize);
            buffer.completeAndClearBuffer(values, ptr, result);
            ctxt.returnObjectBuffer(buffer);
            return result;
        }

        protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectBuffer buffer = ctxt.leaseObjectBuffer();
            Object[] values = buffer.resetAndStart();
            int ptr = 0;
            do {
                Object value = deserialize(p, ctxt);
                if (ptr >= values.length) {
                    values = buffer.appendCompletedChunk(values);
                    ptr = 0;
                }
                int i = ptr;
                ptr++;
                values[i] = value;
            } while (p.nextToken() != JsonToken.END_ARRAY);
            Object[] result = buffer.completeAndClearBuffer(values, ptr);
            ctxt.returnObjectBuffer(buffer);
            return result;
        }

        protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
            String nextFieldName;
            String key1 = p.currentName();
            p.nextToken();
            Object value1 = deserialize(p, ctxt);
            String key = p.nextFieldName();
            if (key == null) {
                LinkedHashMap<String, Object> result = new LinkedHashMap<>(2);
                result.put(key1, value1);
                return result;
            }
            LinkedHashMap<String, Object> result2 = new LinkedHashMap<>();
            result2.put(key1, value1);
            do {
                p.nextToken();
                Object newValue = deserialize(p, ctxt);
                Object oldValue = result2.put(key, newValue);
                if (oldValue != null) {
                    return _mapObjectWithDups(p, ctxt, result2, key, oldValue, newValue, p.nextFieldName());
                }
                nextFieldName = p.nextFieldName();
                key = nextFieldName;
            } while (nextFieldName != null);
            return result2;
        }

        protected Object _mapObjectWithDups(JsonParser p, DeserializationContext ctxt, Map<String, Object> result, String initialKey, Object oldValue, Object newValue, String nextKey) throws IOException {
            boolean squashDups = ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES);
            if (squashDups) {
                _squashDups(result, initialKey, oldValue, newValue);
            }
            while (nextKey != null) {
                p.nextToken();
                Object newValue2 = deserialize(p, ctxt);
                Object oldValue2 = result.put(nextKey, newValue2);
                if (oldValue2 != null && squashDups) {
                    _squashDups(result, nextKey, oldValue2, newValue2);
                }
                nextKey = p.nextFieldName();
            }
            return result;
        }

        private void _squashDups(Map<String, Object> result, String key, Object oldValue, Object newValue) {
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
}
